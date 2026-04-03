package com.appointment.dto.common;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResponseHelper {

    private ResponseHelper() {}

    public static Map<String, Object> created(String message, Object data) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    public static Map<String, Object> success(String message, Object data) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    public static Map<String, Object> deleted(String message) {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", message);
        return response;
    }
}
