package com.jiucom.api.domain.part.repository;

import com.jiucom.api.domain.part.entity.Part;
import com.jiucom.api.domain.part.entity.enums.PartCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepository extends JpaRepository<Part, Long>, PartRepositoryCustom {

    Page<Part> findByCategory(PartCategory category, Pageable pageable);

    Page<Part> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Part> findByCategoryAndIsDeletedFalse(PartCategory category, Pageable pageable);
}
