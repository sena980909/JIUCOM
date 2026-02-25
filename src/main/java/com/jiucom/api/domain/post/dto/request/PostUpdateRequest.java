package com.jiucom.api.domain.post.dto.request;

import com.jiucom.api.domain.post.entity.enums.BoardType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    private String title;

    private String content;

    private BoardType boardType;
}
