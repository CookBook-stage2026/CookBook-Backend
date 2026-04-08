# Security Architecture
This document describes the security architecture of the system, including the various components and their interactions.

## Core Components
### JwtService
Located in the infrastructure security package (in shared), this service manages the lifecycle of JSON Web Tokens. It utilizes the HMAC SHA-256 algorithm for signing.
- Token Generation: Encodes the internal User UUID as the JWT "Subject" (sub). It includes the user's display name and email as custom claims to minimize database lookups on the frontend.
- Cryptographic Implementation: Uses Keys.hmacShaKeyFor with a Base64-decoded secret.
- Validation: Implements signature verification and expiration checking. It returns a boolean status or extracts claims for use in security filters.

### OAuth2AuthService
This service orchestrates the OAuth 2.0 Authorization Code Flow. It acts as the intermediary between the frontend application and third-party Identity Providers (IdP).
- Dynamic URL Construction: Utilizes ClientRegistrationRepository to build provider-specific authorization URLs, ensuring correct scopes and redirect URIs are passed to Google, GitHub, or Microsoft (more services can be added in the future if needed).
- Back-channel Communication: Uses RestClient to perform the exchange of the authorization code for an access token.
- Payload Negotiation: Explicitly sets the Accept: application/json header during the token exchange to ensure compatibility with providers (like GitHub) that default to form-encoded responses.
- User Provisioning: Bridges the authentication result with the UserApi. It implements a "find or create" logic where social connections are mapped to internal user accounts via the autoSaveAfterLogin method.

So as you see, this service performs the role of integrating external authentication providers while maintaining the internal user management system.

### OAuth2UserInfo
A data transfer record designed to normalize disparate profile payloads from different providers into a consistent internal format.
- Google: Maps the sub attribute to the provider ID.
- GitHub: Extracts the numeric id and handles the absence of a "name" field by falling back to the "login" handle.
- Microsoft: Implements specific logic to retrieve the user's identity, prioritizing the email field and falling back to name where necessary.

### SecurityConfig
The SecurityConfig class serves as the central definition point for the application's security posture.
- Hybrid Statelessness: The application utilizes a stateless policy for standard API requests (via JWT Access Tokens). However, a stateful layer is maintained for "Remember Me" functionality, where Refresh Tokens are persisted in the database to manage long-lived sessions and allow for secure token revocation.
- Request Authorization: Public access is explicitly granted to /auth/** endpoints to allow for the login and refresh processes. All other application endpoints require authenticated access.
- Filter Integration: Injects the JwtAuthFilter specifically before the UsernamePasswordAuthenticationFilter to resolve identity before reaching the controllers.

### JwtAuthFilter
This component is a OncePerRequestFilter responsible for intercepting every incoming HTTP request to validate credentials.
- Header Parsing: It inspects the Authorization header for the Bearer prefix. If absent, the request proceeds through the filter chain without authentication.
- Token Validation: If a token is present, the filter utilizes JwtService to verify the signature and expiration.
- Context Population & Transaction Management: Upon successful validation, it extracts the user ID, retrieves the user from the UserApi, and populates the SecurityContextHolder. **Note:** Database calls loading users and their lazy-loaded collections (like social connections) are scoped within a `@Transactional(readOnly = true)` context to prevent `LazyInitializationException` during filter execution.

## Security Flow
### Phase 1: Authentication
1. Frontend Request: The client calls GET /auth/{provider}/url.
2. URL Generation: OAuth2AuthService retrieves the ClientRegistration and constructs the redirect URI for the IdP.
3. User Consent: The user authenticates with the external IdP and is redirected back to the frontend with an authorization code.
4. Exchange: The client sends the code to POST /auth/{provider}/callback.
5. Token Handshake: The backend exchanges the code for an IdP access token using a RestClient POST request (enforcing application/json acceptance).
6. Profile Synchronization: The backend fetches the user profile, maps it through OAuth2UserInfo, and ensures the user exists in the local database.
7. JWT Issuance: A local JWT is generated and returned to the client.

### Phase 2: Authorization
1. Request Attachment: For all subsequent API calls, the frontend attaches the JWT in the Authorization: Bearer <token> header.
2. Interception: JwtAuthFilter extracts the token before the request reaches the controller.
3. Verification: The filter checks the token's validity and extracts the user UUID.
4. Identity Setting: The filter fetches the user from the database and places the user object into the SecurityContext.
5. Execution: The controller processes the request, recognizing the user as authenticated.

### Phase 3: Token Refresh & Session Management
1. Expiration Handling: If an access token expires, the backend returns a `401 Unauthorized` or `403 Forbidden` (403 most of the time).
2. Frontend Interception: The Angular `auth.interceptor` catches this specific error and pauses the failed API request.
3. Background Refresh: The interceptor triggers a background call to POST `/auth/refresh` using the stored Refresh Token. To prevent infinite loops, the interceptor strictly excludes the `/auth/refresh` endpoint from attaching the expired access token.
4. Replay or Logout: If the refresh succeeds, the intercepted request is replayed transparently with the new token. If the refresh fails (or the refresh token is invalid), the interceptor forces a frontend logout, clearing local storage and redirecting to the login screen.

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