package br.com.fiap.Model.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record NewsPage(
        List<News> news,
        long total,
        int page,
        @JsonProperty("page_size") int pageSize) {
}
