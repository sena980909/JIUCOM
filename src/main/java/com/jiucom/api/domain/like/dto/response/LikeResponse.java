package com.jiucom.api.domain.like.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class LikeResponse {
    private boolean liked;
    private long likeCount;
}
