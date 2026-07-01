package org.h3kar360.repository.projection;

public interface ApiInfoOnly {
    String getApiUrl();

    Integer getConnectTimeout();

    Integer getReadTimeout();
}
