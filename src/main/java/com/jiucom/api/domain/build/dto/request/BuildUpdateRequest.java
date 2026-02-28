package com.jiucom.api.domain.build.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BuildUpdateRequest {

    @Size(max = 100, message = "견적 이름은 100자 이하여야 합니다.")
    private String name;

    @Size(max = 500, message = "설명은 500자 이하여야 합니다.")
    private String description;

    @JsonProperty("isPublic")
    private Boolean isPublic;

    @Valid
    private List<BuildPartRequest> parts; // null이면 미변경
}
