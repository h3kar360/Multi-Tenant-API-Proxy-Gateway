package org.h3kar360.dto;

public record EnvironmentRequest(String apiName, String targetBaseUrl, String apiKey, String proxyKeyToken) {
}
