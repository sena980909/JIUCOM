package com.jiucom.api.domain.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 2, max = 30, message = "닉네임은 2~30자여야 합니다.")
    private String nickname;

    private String profileImageUrl;
}
