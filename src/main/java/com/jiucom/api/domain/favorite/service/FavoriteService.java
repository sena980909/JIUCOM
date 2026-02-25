package com.jiucom.api.domain.favorite.service;

import com.jiucom.api.domain.favorite.dto.response.FavoriteResponse;
import com.jiucom.api.domain.favorite.entity.Favorite;
import com.jiucom.api.domain.favorite.repository.FavoriteRepository;
import com.jiucom.api.domain.part.entity.Part;
import com.jiucom.api.domain.part.repository.PartRepository;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.repository.UserRepository;
import com.jiucom.api.global.exception.GlobalException;
import com.jiucom.api.global.exception.code.GlobalErrorCode;
import com.jiucom.api.global.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final PartRepository partRepository;
    private final UserRepository userRepository;

    public Page<FavoriteResponse> getMyFavorites(int page, int size) {
        Long userId = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return favoriteRepository.findByUser(user, pageable)
                .map(FavoriteResponse::from);
    }

    @Transactional
    public void addFavorite(Long partId) {
        Long userId = SecurityUtil.getCurrentUserId();

        if (favoriteRepository.existsByUserIdAndPartId(userId, partId)) {
            return; // already exists, ignore
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.USER_NOT_FOUND));
        Part part = partRepository.findById(partId)
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.PART_NOT_FOUND));

        Favorite favorite = Favorite.builder()
                .user(user)
                .part(part)
                .build();

        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long partId) {
        Long userId = SecurityUtil.getCurrentUserId();
        Favorite favorite = favoriteRepository.findByUserIdAndPartId(userId, partId)
                .orElseThrow(() -> new GlobalException(GlobalErrorCode.RESOURCE_NOT_FOUND));
        favoriteRepository.delete(favorite);
    }

    public boolean isFavorite(Long partId) {
        Long userId = SecurityUtil.getCurrentUserId();
        return favoriteRepository.existsByUserIdAndPartId(userId, partId);
    }
}
