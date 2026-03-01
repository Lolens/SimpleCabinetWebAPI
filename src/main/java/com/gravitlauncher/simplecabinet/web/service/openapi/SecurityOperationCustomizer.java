package com.gravitlauncher.simplecabinet.web.service.openapi;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class SecurityOperationCustomizer implements OperationCustomizer {

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        // Check method-level annotation first, then class-level
        PreAuthorize preAuthorize = handlerMethod.getMethodAnnotation(PreAuthorize.class);
        if (preAuthorize == null) {
            preAuthorize = handlerMethod.getBeanType().getAnnotation(PreAuthorize.class);
        }

        if (preAuthorize != null) {
            // Add security requirement to the operation
            operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));

            // Optionally document the 401 response
            operation.getResponses().addApiResponse("401",
                    new ApiResponse().description("Unauthorized – JWT token is missing or invalid"));
        }

        return operation;
    }
}