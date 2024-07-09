package site.billingwise.api.serverapi.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    protected static final String[] ALLOWED_ORIGIN_LIST = {
            "http://localhost:5173",
            "https://billingwise.site",
            "https://*.billingwise.site"
    };
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(
                        "http://localhost:5173",
                        "https://billingwise.site",
                        "https://*.billingwise.site"
                        )
                .allowedMethods("*")
                .allowCredentials(true);
    }
}

