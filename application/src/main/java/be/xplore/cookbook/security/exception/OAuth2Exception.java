package be.xplore.cookbook.security.exception;

public class OAuth2Exception extends RuntimeException {
    public OAuth2Exception(String message, Exception e) {
        super(message, e);
    }

    public OAuth2Exception(String message) {
        super(message);
    }
}
