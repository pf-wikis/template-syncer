package io.github.pfwikis.templatesyncer;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageQuery {
    private List<Page> pages;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Page {
        private int pageid;
        private String title;
    }
}