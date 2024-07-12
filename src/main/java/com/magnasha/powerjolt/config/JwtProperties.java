package com.magnasha.powerjolt.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Data
@Setter
@Getter
public class JwtProperties {
    private int expiration;
    private String secret;


}
