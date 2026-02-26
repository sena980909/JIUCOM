package com.jiucom.api.domain.part.repository;

import com.jiucom.api.domain.part.entity.Part;
import com.jiucom.api.domain.part.entity.enums.PartCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PartRepositoryCustom {

    Page<Part> searchParts(String keyword, PartCategory category, Integer minPrice, Integer maxPrice, Pageable pageable);

    List<String> suggestNames(String keyword, int limit);
}
