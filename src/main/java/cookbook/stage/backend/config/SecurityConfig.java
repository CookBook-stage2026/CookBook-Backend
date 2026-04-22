package cookbook.stage.backend.config;

import cookbook.stage.backend.repository.CookieAuthorizationRequestRepository;
import cookbook.stage.backend.util.OAuth2AuthenticationSuccessHandler;
import io.jsonwebtoken.io.Decoders;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.util.WebUtils;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers:*}")
    private String allowedHeaders;

    private final OAuth2AuthenticationSuccessHandler handler;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;

    public SecurityConfig(OAuth2AuthenticationSuccessHandler handler,
                          CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository) {
        this.handler = handler;
        this.cookieAuthorizationRequestRepository = cookieAuthorizationRequestRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authEndpoint -> authEndpoint
                                .baseUri("/oauth2/authorization")
                                .authorizationRequestRepository(cookieAuthorizationRequestRepository)
                        )
                        .redirectionEndpoint(redirectionEndpoint -> redirectionEndpoint
                                .baseUri("/login/oauth2/code/*")
                        )
                        .successHandler(handler)
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .bearerTokenResolver(cookieTokenResolver())
                        .jwt(Customizer.withDefaults())
                );
        return http.build();
    }

    @Bean
    public BearerTokenResolver cookieTokenResolver() {
        return request -> {
            Cookie cookie = WebUtils.getCookie(request, "access_token");
            return cookie != null ? cookie.getValue() : null;
        };
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] secretBytes = Decoders.BASE64.decode(jwtSecret);
        SecretKeySpec secretKey = new SecretKeySpec(secretBytes, "HmacSHA512");

        return NimbusJwtDecoder.withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(allowedOrigins));
        config.setAllowedMethods(List.of(allowedMethods.split(",")));
        config.setAllowedHeaders(List.of(allowedHeaders.split(",")));
        config.setAllowCredentials(true);
        return _ -> config;
    }
}
