package com.jiucom.api.domain.search.controller;

import com.jiucom.api.domain.search.dto.response.SearchResultResponse;
import com.jiucom.api.domain.search.dto.response.SuggestResponse;
import com.jiucom.api.domain.search.service.SearchService;
import com.jiucom.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
@Tag(name = "Search", description = "통합 검색 API")
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    @Operation(summary = "통합 검색", description = "키워드로 부품/게시글/견적 동시 검색")
    public ResponseEntity<ApiResponse<SearchResultResponse>> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.ok(searchService.search(keyword, size)));
    }

    @GetMapping("/suggest")
    @Operation(summary = "자동완성", description = "부품명/게시글 제목 상위 5개 자동완성")
    public ResponseEntity<ApiResponse<SuggestResponse>> suggest(
            @RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.ok(searchService.suggest(keyword)));
    }
}
