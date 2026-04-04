package com.gravitlauncher.simplecabinet.web.service.openapi;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class SecurityOperationCustomizer implements OperationCustomizer {
    private final RequestMappingHandlerMapping handlerMapping;

    public SecurityOperationCustomizer(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        if (!StringUtils.hasText(operation.getSummary())) {
            operation.setSummary(buildSummary(handlerMethod));
        }
        if (!StringUtils.hasText(operation.getDescription())) {
            operation.setDescription(buildDescription(handlerMethod));
        }
        if (operation.getTags() == null || operation.getTags().isEmpty()) {
            operation.addTagsItem(resolveTag(handlerMethod));
        }

        addDefaultResponse(operation, "400", "Invalid request or business rule violation");
        addDefaultResponse(operation, "403", "Access denied");
        addDefaultResponse(operation, "404", "Entity not found");
        addDefaultResponse(operation, "500", "Internal server error");

        PreAuthorize preAuthorize = handlerMethod.getMethodAnnotation(PreAuthorize.class);
        if (preAuthorize == null) {
            preAuthorize = handlerMethod.getBeanType().getAnnotation(PreAuthorize.class);
        }

        if (preAuthorize != null) {
            operation.addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
            addDefaultResponse(operation, "401", "JWT token is missing or invalid");
        }

        return operation;
    }

    private void addDefaultResponse(Operation operation, String code, String description) {
        if (operation.getResponses() == null || !operation.getResponses().containsKey(code)) {
            operation.getResponses().addApiResponse(code, new ApiResponse()
                    .description(description)
                    .content(new Content().addMediaType("application/json",
                            new MediaType().schema(new Schema<>().$ref("#/components/schemas/ApiError")))));
        }
    }

    private String buildSummary(HandlerMethod handlerMethod) {
        String methodName = handlerMethod.getMethod().getName();
        String resource = humanizeResource(handlerMethod.getBeanType().getSimpleName());
        String path = resolvePattern(handlerMethod);

        return switch (methodName) {
            case "auth", "authorize" -> "Authorize user";
            case "refresh", "refreshToken" -> "Refresh access token";
            case "logout" -> "Logout current session";
            case "register" -> "Register user";
            case "regConfirm" -> "Confirm registration";
            case "getUserInfo", "currentuser" -> "Get current user";
            default -> {
                if (methodName.startsWith("getPage") || methodName.equals("findAll") || methodName.equals("getAll")) {
                    yield "List " + resource;
                }
                if (methodName.startsWith("search")) {
                    yield "Search " + resource;
                }
                if (methodName.startsWith("getBy") || methodName.startsWith("findBy")) {
                    yield "Get " + resource;
                }
                if (methodName.startsWith("create") || methodName.startsWith("new") || methodName.startsWith("push")) {
                    yield "Create " + resource;
                }
                if (methodName.startsWith("update") || methodName.startsWith("set")) {
                    yield "Update " + resource;
                }
                if (methodName.startsWith("delete") || methodName.startsWith("unassign") || methodName.startsWith("remove")) {
                    yield "Delete " + resource;
                }
                if (methodName.startsWith("upload")) {
                    yield "Upload " + resource;
                }
                if (methodName.startsWith("buy")) {
                    yield "Purchase " + resource;
                }
                if (methodName.startsWith("check")) {
                    yield "Check " + resource;
                }
                if (methodName.startsWith("prepare")) {
                    yield "Prepare " + resource;
                }
                if (methodName.startsWith("enable")) {
                    yield "Enable " + resource;
                }
                if (methodName.startsWith("disable")) {
                    yield "Disable " + resource;
                }
                if (methodName.startsWith("transfer")) {
                    yield "Transfer " + resource;
                }
                yield methodName + " [" + path + "]";
            }
        };
    }

    private String buildDescription(HandlerMethod handlerMethod) {
        String path = resolvePattern(handlerMethod);
        String security = hasPreAuthorize(handlerMethod)
                ? "Requires a valid JWT bearer token with the permissions enforced by Spring Security."
                : "Public endpoint unless restricted by surrounding infrastructure.";
        return "Route: `" + path + "`. " + security;
    }

    private boolean hasPreAuthorize(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(PreAuthorize.class)
                || handlerMethod.getBeanType().isAnnotationPresent(PreAuthorize.class);
    }

    private String resolveTag(HandlerMethod handlerMethod) {
        String packageName = handlerMethod.getBeanType().getPackageName();
        if (packageName.contains(".controller.admin")) {
            return "Admin";
        }
        if (packageName.contains(".controller.cabinet")) {
            return "Cabinet";
        }
        if (packageName.contains(".controller.launcher")) {
            return "Launcher";
        }
        if (packageName.contains(".controller.shop")) {
            return "Shop";
        }
        if (packageName.contains(".controller.payment")) {
            return "Webhooks";
        }
        if (packageName.contains(".controller.integration")) {
            return "Integration";
        }
        return "Public";
    }

    private String humanizeResource(String controllerName) {
        String baseName = controllerName
                .replace("Controller", "")
                .replace("Admin", "")
                .replace("Cabinet", "")
                .replace("Launcher", "");
        if (baseName.endsWith("ies")) {
            return baseName.substring(0, baseName.length() - 3).toLowerCase(Locale.ROOT) + "ies";
        }
        if (baseName.endsWith("s")) {
            return baseName.toLowerCase(Locale.ROOT);
        }
        return baseName.toLowerCase(Locale.ROOT);
    }

    private String resolvePattern(HandlerMethod handlerMethod) {
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMapping.getHandlerMethods().entrySet()) {
            if (entry.getValue().equals(handlerMethod)) {
                Set<String> patterns = entry.getKey().getPatternValues();
                if (!patterns.isEmpty()) {
                    return patterns.iterator().next();
                }
            }
        }
        return "/";
    }
}
