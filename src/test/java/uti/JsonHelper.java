package uti;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

public class JsonHelper {
    private static final ObjectMapper mapper = createObjectMapper();

    public static <T extends Object> T toObject(byte[] json, Class<T> javaClass) {
        try {
            return mapper.readValue(json, javaClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
