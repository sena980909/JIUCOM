package com.jiucom.api.domain.search.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class SuggestResponse {
    private List<String> partNames;
    private List<String> postTitles;
}
