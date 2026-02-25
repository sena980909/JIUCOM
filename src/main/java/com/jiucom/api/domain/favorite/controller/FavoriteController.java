package com.jiucom.api.domain.favorite.controller;

import com.jiucom.api.domain.favorite.service.FavoriteService;
import com.jiucom.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Favorite", description = "관심상품 API")
@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "관심상품 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // TODO: implement
        return ResponseEntity.ok(ApiResponse.ok());
    }

    @Operation(summary = "관심상품 추가")
    @PostMapping("/parts/{partId}")
    public ResponseEntity<ApiResponse<?>> addFavorite(@PathVariable Long partId) {
        // TODO: implement
        return ResponseEntity.ok(ApiResponse.created(null));
    }

    @Operation(summary = "관심상품 삭제")
    @DeleteMapping("/parts/{partId}")
    public ResponseEntity<ApiResponse<?>> removeFavorite(@PathVariable Long partId) {
        // TODO: implement
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
