package io.github.pfwikis.templatesyncer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenQuery {
    private Tokens tokens;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tokens {
        private String csrftoken;
    }
}