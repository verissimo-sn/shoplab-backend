package dev.shoplab.shared.web;

import dev.shoplab.shared.errors.BusinessRuleException;
import dev.shoplab.shared.errors.ResourceNotFoundException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class ExceptionMappers {
    public static final Logger LOG = Logger.getLogger(ExceptionMappers.class);

    @ServerExceptionMapper
    public RestResponse<ProblemDetail> notFound(ResourceNotFoundException e) {
        return problem(ProblemDetail.of(404, "Not Found", e.getMessage(), "NOT_FOUND"));
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetail> businessRule(BusinessRuleException e) {
        return problem(ProblemDetail.of(422, "Business rule violated", e.getMessage(), e.code()));
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetail> conflict(OptimisticLockException e) {
        return problem(ProblemDetail.of(409, "Conflict", "Resource was modified concurrently, retry", "CONCURRENTLY_UPDATE"));
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetail> violation(ConstraintViolationException e) {
        var errors = e.getConstraintViolations().stream()
                .map(v-> new ProblemDetail.FieldError(lastNode(v.getPropertyPath().toString()), v.getMessage()))
                .toList();
        return problem(new ProblemDetail("about:blank", "Invalid Request", 400, "One or more fields are invalid", "VALIDATION_ERROR", errors));
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetail> webApplication(WebApplicationException e) {
        var status = e.getResponse().getStatus();
        var reason = e.getResponse().getStatusInfo().getReasonPhrase();
        return problem(ProblemDetail.of(status, reason, e.getMessage(), reason.toUpperCase().replace(' ', '_')));
    }

    @ServerExceptionMapper
    public RestResponse<ProblemDetail> unexpected(Exception e) {
        LOG.error("unexpected error", e);
        return problem(ProblemDetail.of(500, "Internal error", "Unexpected error", "INTERNAL_ERROR"));
    }

    private static RestResponse<ProblemDetail> problem(ProblemDetail body) {
        return RestResponse.ResponseBuilder.<ProblemDetail>create(body.status())
                .type("application/problem+json")
                .entity(body)
                .build();
    }

    private static String lastNode(String path) {
        return path.substring(path.lastIndexOf('.') + 1);
    }
}
