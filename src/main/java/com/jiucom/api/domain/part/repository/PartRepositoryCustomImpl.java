package com.jiucom.api.domain.part.repository;

import com.jiucom.api.domain.part.entity.Part;
import com.jiucom.api.domain.part.entity.QPart;
import com.jiucom.api.domain.part.entity.enums.PartCategory;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PartRepositoryCustomImpl implements PartRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Part> searchParts(String keyword, PartCategory category,
                                  Integer minPrice, Integer maxPrice, Pageable pageable) {
        QPart part = QPart.part;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(part.isDeleted.eq(false));

        if (keyword != null && !keyword.isBlank()) {
            builder.and(part.name.containsIgnoreCase(keyword)
                    .or(part.manufacturer.containsIgnoreCase(keyword)));
        }
        if (category != null) {
            builder.and(part.category.eq(category));
        }
        if (minPrice != null) {
            builder.and(part.lowestPrice.goe(minPrice));
        }
        if (maxPrice != null) {
            builder.and(part.lowestPrice.loe(maxPrice));
        }

        JPAQuery<Part> query = queryFactory.selectFrom(part)
                .where(builder)
                .orderBy(getOrderSpecifier(pageable, part))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Part> content = query.fetch();
        long total = queryFactory.select(part.count()).from(part).where(builder).fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<String> suggestNames(String keyword, int limit) {
        QPart part = QPart.part;
        return queryFactory.select(part.name)
                .from(part)
                .where(part.isDeleted.eq(false)
                        .and(part.name.startsWithIgnoreCase(keyword)))
                .orderBy(part.name.asc())
                .limit(limit)
                .fetch();
    }

    private OrderSpecifier<?> getOrderSpecifier(Pageable pageable, QPart part) {
        if (pageable.getSort().isSorted()) {
            var order = pageable.getSort().iterator().next();
            String property = order.getProperty();
            boolean asc = order.isAscending();

            return switch (property) {
                case "price" -> asc ? part.lowestPrice.asc() : part.lowestPrice.desc();
                case "name" -> asc ? part.name.asc() : part.name.desc();
                default -> part.lowestPrice.asc();
            };
        }
        return part.lowestPrice.asc();
    }
}
