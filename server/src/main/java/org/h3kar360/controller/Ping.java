package org.h3kar360.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class Ping {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @GetMapping("/health")
    public String health() {
        return "health";
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}
