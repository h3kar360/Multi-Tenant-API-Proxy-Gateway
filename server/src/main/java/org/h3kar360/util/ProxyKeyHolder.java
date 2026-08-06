package org.h3kar360.util;

import org.springframework.stereotype.Component;

@Component
public class ProxyKeyHolder {
    private final ThreadLocal<String> currentProxyKey = new ThreadLocal<>();

    public void set(String proxyKey) {
        currentProxyKey.set(proxyKey);
    }

    public String get() {
        return currentProxyKey.get();
    }

    public void clear() {
        currentProxyKey.remove();
    }
}
