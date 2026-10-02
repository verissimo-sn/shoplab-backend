package dev.shoplab.shared.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.shoplab.catalog.web.CreateProductRequest;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProblemDetail(
        String type,
        String title,
        int status,
        String detail,
        String code,
        List<FieldError> errors
) {
    public record FieldError(String field, String message) {}

    public static ProblemDetail of(int status, String title, String detail, String code){
        return new ProblemDetail("about:blank", title, status, detail, code, null);
    }
}
