package com.jiucom.api.domain.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardResponse {

    private long totalUsers;
    private long todaySignups;
    private long totalBuilds;
    private long totalParts;
    private long totalPosts;
    private List<RecentUserResponse> recentUsers;

    @Getter
    @Builder
    public static class RecentUserResponse {
        private Long id;
        private String email;
        private String nickname;
        private String role;
        private String createdAt;
    }
}
