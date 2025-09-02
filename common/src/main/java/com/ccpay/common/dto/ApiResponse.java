package com.ccpay.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private T data;
    private ApiError error;
    private ApiMetadata metadata;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiError {
        private String code;
        private String message;
        private String field;
        private Object rejectedValue;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiMetadata {
        private String traceId;
        private String version;
        private Long timestamp;
        private Integer duration;
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .data(data)
                .metadata(ApiMetadata.builder()
                        .timestamp(System.currentTimeMillis())
                        .version("1.0")
                        .build())
                .build();
    }
    
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .error(ApiError.builder()
                        .code(code)
                        .message(message)
                        .build())
                .metadata(ApiMetadata.builder()
                        .timestamp(System.currentTimeMillis())
                        .version("1.0")
                        .build())
                .build();
    }
}