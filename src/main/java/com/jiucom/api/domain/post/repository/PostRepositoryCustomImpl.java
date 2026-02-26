package com.jiucom.api.domain.post.repository;

import com.jiucom.api.domain.post.entity.Post;
import com.jiucom.api.domain.post.entity.QPost;
import com.jiucom.api.domain.post.entity.enums.BoardType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> searchPosts(String keyword, BoardType boardType, Pageable pageable) {
        QPost post = QPost.post;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(post.isDeleted.eq(false));

        if (keyword != null && !keyword.isBlank()) {
            builder.and(post.title.containsIgnoreCase(keyword)
                    .or(post.content.containsIgnoreCase(keyword)));
        }
        if (boardType != null) {
            builder.and(post.boardType.eq(boardType));
        }

        List<Post> content = queryFactory.selectFrom(post)
                .where(builder)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.select(post.count()).from(post).where(builder).fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<String> suggestTitles(String keyword, int limit) {
        QPost post = QPost.post;
        return queryFactory.select(post.title)
                .from(post)
                .where(post.isDeleted.eq(false)
                        .and(post.title.startsWithIgnoreCase(keyword)))
                .orderBy(post.createdAt.desc())
                .limit(limit)
                .fetch();
    }
}
