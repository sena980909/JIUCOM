package com.jiucom.api.domain.post.dto.request;

import com.jiucom.api.domain.post.entity.enums.BoardType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostUpdateRequest {

    @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
    private String title;

    @Size(max = 10000, message = "내용은 10000자 이하여야 합니다.")
    private String content;

    private BoardType boardType;
}
