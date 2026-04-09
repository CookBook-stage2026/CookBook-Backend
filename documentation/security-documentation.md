# Security Architecture
This document describes the security architecture of the system, including the various components and their interactions.

## Core Components
### JwtService & NimbusJwtDecoder
Handling the lifecycle of JSON Web Tokens is split between token generation and validation.
- Issuance (JwtService): Generates JWTs upon successful login or refresh. It encodes the internal User UUID as the subclaim and includes the user's email and name to minimize database lookups on the frontend. It uses Keys.hmacShaKeyFor with a Base64-decoded secret.- Cryptographic Implementation: Uses Keys.hmacShaKeyFor with a Base64-decoded secret.
- Validation (NimbusJwtDecoder): Defined as a bean in SecurityConfig, this native Spring Security component automatically intercepts incoming API requests, validates the HMAC SHA-256 signature, and enforces expiration dates.

### CookieAuthorizationRequestRepository
A crucial component for maintaining statelessness during the initial OAuth2 redirect hop.
- State Management: Temporarily stores the OAuth2AuthorizationRequest in an encrypted, signed HTTP-only cookie (oauth2_auth_request) instead of an HTTP Session.
- Tamper Resistance: Serializes the request and appends an HMAC SHA-256 signature to the cookie payload. Upon deserialization, it verifies the signature to prevent tampering.
- Remember Me: Captures and stores the frontend's rememberMe preference in a temporary cookie to be processed after the OAuth2 flow completes.

### RefreshTokenService & AuthController
Manages long-lived sessions for users who opt into "Remember Me" functionality.
- Storage: Stores a hashed/random UUID in the RefreshTokenRepository mapped to the user's UUID and an expiration date.
- One-Session Policy: Enforces a strict one-session-per-user rule by deleting any existing refresh tokens for a user before issuing a new one.
- Rotation: Exposes the /auth/refresh endpoint via AuthController. It validates the incoming refresh_token cookie, checks expiration, and issues a fresh Access Token.

### OAuth2UserInfo
A data transfer record designed to normalize disparate profile payloads from different providers into a consistent internal format.
- Google: Maps the sub attribute to the provider ID.
- GitHub: Extracts the numeric id and handles the absence of a "name" field by falling back to the "login" handle.
- Microsoft: Implements specific logic to retrieve the user's identity, prioritizing the email field and falling back to name where necessary.

### SecurityConfig
The SecurityConfig class serves as the central definition point for the application's security.
- Stateless Policy: Configures SessionCreationPolicy.STATELESS, ensuring the backend does not rely on traditional HTTP sessions.
- Request Authorization: Public access is explicitly granted to /auth/** endpoints to allow for the login and refresh processes. All other application endpoints require authenticated access.
- OAuth2 Login: Configures the application as an OAuth2 client, overriding the default session-based request repository with CookieAuthorizationRequestRepository. It also hooks in the custom OAuth2AuthenticationSuccessHandler to finalize the login process.
- Resource Server: Enables JWT-based API authorization, relying on a NimbusJwtDecoder bean to validate incoming Bearer tokens using HMAC SHA-256.
- CORS: Dynamically configures Cross-Origin Resource Sharing based on the app.cors.allowed-origins environment variable.

### OAuth2AuthenticationSuccessHandler
Executes immediately after Spring Security successfully exchanges the authorization code for an Identity Provider (IdP) access token.
- Identity Normalization: Passes the IdP payload to OAuth2UserInfo to normalize the data (handling stuff like GitHub's lack of a guaranteed email).
- User Provisioning: Interfaces with UserApi to find the existing user by social connection or auto-register a new user.
- Token Dispatch: Cleans up the temporary authorization cookies, generates a short-lived Access Token, and (if the remember_me cookie is present) issues a long-lived Refresh Token. Both are attached as secure cookies, and the user is redirected to the frontend callback URL.

## Security Flow
### Phase 1: Authentication
1. Initiation: The frontend redirects the user to /oauth2/authorization/{provider} (e.g., google, github).
2. State Preservation: Spring Security intercepts the request. CookieAuthorizationRequestRepository serializes the authorization request and saves it as a signed cookie.
3. User Consent: The backend redirects the user to the IdP's login page.
4. Callback: The IdP redirects back to /login/oauth2/code/{provider} with an authorization code.
5. Exchange: Spring Security automatically executes the code-to-token exchange with the IdP in the background.
6. Success Handling: OAuth2AuthenticationSuccessHandler takes over, normalizes the user profile, provisions the internal database account, and attaches the access_token (and optionally refresh_token) cookies.
7. Redirection: The backend redirects to the frontend callback URL.

### Phase 2: Authorization
1. API Request: The frontend extracts the access token (or sends the cookie, depending on frontend setup) and attaches it to standard API requests. Note: Spring's Resource Server expects a Authorization: Bearer <token> header.
2. Interception: Spring Security's built-in Resource Server interceptor catches the request.
3. Validation: The NimbusJwtDecoder validates the token's signature and expiration.
4. Context Population: Spring Security automatically populates the SecurityContextHolder with the authenticated user details based on the JWT claims.
5. Execution: The request is passed to the relevant `@RestController`.

### Phase 3: Token Refresh & Session Management
1. Expiration: An API call fails with a `401 Unauthorized` status due to an expired JWT.
2. Interception (Frontend): The frontend interceptor pauses the failed request.
3. Refresh Call: The frontend makes a background POST request to `/auth/refresh`. The browser automatically includes the HTTP-Only refresh_token cookie.
4. Validation: AuthController validates the token via RefreshTokenService.
5. Re-issuance: If valid, a new Access Token is generated and returned to the client.
6. Resumption: The frontend resumes the paused API call with the fresh token. If the refresh fails, the user is logged out.

## Configuration and Environment Variables
For this to work correctly, the following environment variables must be set:
- spring.security.oauth2.client.registration.google.client-id
- spring.security.oauth2.client.registration.google.client-secret
- spring.security.oauth2.client.registration.github.client-id
- spring.security.oauth2.client.registration.github.client-secret
- spring.security.oauth2.client.registration.microsoft.client-id
- spring.security.oauth2.client.registration.microsoft.client-secret
- app.jwt.secret (Base64-encoded secret for signing JWTs)
- app.cors.allowed-origins (Comma-separated list of allowed origins for CORS)

If any questions arise about the security implementation or if you need to add support for additional providers, you can contact me through [mail](mailto:r.vanmele2005@gmail.com).