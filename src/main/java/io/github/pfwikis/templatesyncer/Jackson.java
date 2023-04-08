package io.github.pfwikis.templatesyncer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Jackson {

    public static ObjectMapper get() {
        return new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
    }
}
