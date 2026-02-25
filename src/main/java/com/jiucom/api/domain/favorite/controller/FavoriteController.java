package com.jiucom.api.domain.favorite.controller;

import com.jiucom.api.domain.favorite.dto.response.FavoriteResponse;
import com.jiucom.api.domain.favorite.service.FavoriteService;
import com.jiucom.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApiResponse<Page<FavoriteResponse>>> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<FavoriteResponse> favorites = favoriteService.getMyFavorites(page, size);
        return ResponseEntity.ok(ApiResponse.ok(favorites));
    }

    @Operation(summary = "관심상품 추가")
    @PostMapping("/parts/{partId}")
    public ResponseEntity<ApiResponse<Void>> addFavorite(@PathVariable Long partId) {
        favoriteService.addFavorite(partId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(null));
    }

    @Operation(summary = "관심상품 삭제")
    @DeleteMapping("/parts/{partId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(@PathVariable Long partId) {
        favoriteService.removeFavorite(partId);
        return ResponseEntity.ok(ApiResponse.ok());
    }
}
