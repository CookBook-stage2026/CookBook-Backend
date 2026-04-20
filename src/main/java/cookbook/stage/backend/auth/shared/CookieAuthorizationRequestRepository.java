package cookbook.stage.backend.auth.shared;

import cookbook.stage.backend.shared.domain.OAuth2Exception;
import cookbook.stage.backend.shared.infrastructure.security.CookieUtils;
import io.jsonwebtoken.io.Decoders;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.Set;

@Component
public class CookieAuthorizationRequestRepository implements
        AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    private static final int COOKIE_EXPIRE_SECONDS = 180;

    private final CookieUtils cookieUtils;
    private final SecretKeySpec secretKey;
    private final ObjectMapper objectMapper;

    public CookieAuthorizationRequestRepository(
            CookieUtils cookieUtils,
            @Value("${app.jwt.secret}") String jwtSecret) {
        this.cookieUtils = cookieUtils;
        byte[] secretBytes = Decoders.BASE64.decode(jwtSecret);
        this.secretKey = new SecretKeySpec(secretBytes, "HmacSHA512");
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return cookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> deserialize(cookie.getValue()))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            cookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            return;
        }

        cookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
                serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS, true);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(
            HttpServletRequest request, HttpServletResponse response) {
        return loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        cookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
    }

    private String serialize(OAuth2AuthorizationRequest request) {
        try {
            OAuth2AuthorizationRequestDto dto = OAuth2AuthorizationRequestDto.from(request);
            byte[] payload = objectMapper.writeValueAsBytes(dto);

            String base64Payload = Base64.getUrlEncoder().withoutPadding().encodeToString(payload);
            String signature = computeSignature(payload);

            return base64Payload + "." + signature;
        } catch (Exception e) {
            throw new OAuth2Exception("Failed to serialize OAuth2 request", e);
        }
    }

    private OAuth2AuthorizationRequest deserialize(String cookieValue) {
        try {
            String[] parts = cookieValue.split("\\.");
            if (parts.length != 2) {
                return null;
            }

            String base64Payload = parts[0];
            String providedSignature = parts[1];
            byte[] payload = Base64.getUrlDecoder().decode(base64Payload);

            String expectedSignature = computeSignature(payload);
            if (!MessageDigest.isEqual(expectedSignature.getBytes(), providedSignature.getBytes())) {
                return null;
            }

            OAuth2AuthorizationRequestDto dto = objectMapper.readValue(payload, OAuth2AuthorizationRequestDto.class);
            return dto.toAuthorizationRequest();

        } catch (Exception _) {
            return null;
        }
    }

    private String computeSignature(byte[] data) {
        Mac mac;
        try {
            mac = Mac.getInstance("HmacSHA512");
            mac.init(secretKey);
            byte[] signatureBytes = mac.doFinal(data);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new OAuth2Exception("Failed to compute signature", e);
        }
    }

    /**
     * DTO for JSON serialization of OAuth2AuthorizationRequest.
     */
    private record OAuth2AuthorizationRequestDto(
            String authorizationUri,
            String clientId,
            Map<String, Object> attributes,
            Map<String, Object> additionalParameters,
            String authorizationRequestUri,
            Set<String> scopes,
            String state,
            String responseType,
            String redirectUri
    ) {
        static OAuth2AuthorizationRequestDto from(OAuth2AuthorizationRequest req) {
            return new OAuth2AuthorizationRequestDto(
                    req.getAuthorizationUri(),
                    req.getClientId(),
                    req.getAttributes(),
                    req.getAdditionalParameters(),
                    req.getAuthorizationRequestUri(),
                    req.getScopes(),
                    req.getState(),
                    req.getResponseType().getValue(),
                    req.getRedirectUri()
            );
        }

        OAuth2AuthorizationRequest toAuthorizationRequest() {
            return OAuth2AuthorizationRequest.authorizationCode()
                    .authorizationUri(authorizationUri)
                    .clientId(clientId)
                    .redirectUri(redirectUri)
                    .scopes(scopes)
                    .state(state)
                    .attributes(attributes)
                    .additionalParameters(additionalParameters)
                    .authorizationRequestUri(authorizationRequestUri)
                    .build();
        }
    }
}
