package com.example.pm_tool.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchResponse {
    private List<IssueResponse> items;
    private long total;
}
