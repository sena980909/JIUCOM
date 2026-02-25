package com.jiucom.api.domain.admin.service;

import com.jiucom.api.domain.admin.dto.response.DashboardResponse;
import com.jiucom.api.domain.build.repository.BuildRepository;
import com.jiucom.api.domain.part.repository.PartRepository;
import com.jiucom.api.domain.post.repository.PostRepository;
import com.jiucom.api.domain.user.entity.User;
import com.jiucom.api.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final BuildRepository buildRepository;
    private final PartRepository partRepository;
    private final PostRepository postRepository;

    public DashboardResponse getDashboard() {
        long totalUsers = userRepository.count();
        long totalBuilds = buildRepository.count();
        long totalParts = partRepository.count();
        long totalPosts = postRepository.countByIsDeletedFalse();

        // Today signups
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todaySignups = userRepository.countByCreatedAtAfter(todayStart);

        // Recent 5 users
        Pageable top5 = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<DashboardResponse.RecentUserResponse> recentUsers = userRepository.findAll(top5)
                .getContent()
                .stream()
                .map(user -> DashboardResponse.RecentUserResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .role(user.getRole().name())
                        .createdAt(user.getCreatedAt().toString())
                        .build())
                .toList();

        return DashboardResponse.builder()
                .totalUsers(totalUsers)
                .todaySignups(todaySignups)
                .totalBuilds(totalBuilds)
                .totalParts(totalParts)
                .totalPosts(totalPosts)
                .recentUsers(recentUsers)
                .build();
    }

    public Page<DashboardResponse.RecentUserResponse> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return userRepository.findAll(pageable)
                .map(user -> DashboardResponse.RecentUserResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .role(user.getRole().name())
                        .createdAt(user.getCreatedAt().toString())
                        .build());
    }
}
