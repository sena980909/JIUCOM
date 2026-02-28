package com.jiucom.api.domain.build.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BuildCreateRequest {

    @NotBlank(message = "견적 이름은 필수입니다.")
    @Size(max = 100, message = "견적 이름은 100자 이하여야 합니다.")
    private String name;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    private String description;

    @JsonProperty("isPublic")
    private boolean isPublic = false;

    @NotEmpty(message = "최소 1개 이상의 부품이 필요합니다.")
    @Valid
    private List<BuildPartRequest> parts;
}
