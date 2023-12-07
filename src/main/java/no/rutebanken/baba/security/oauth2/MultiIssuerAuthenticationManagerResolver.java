package no.rutebanken.baba.security.oauth2;

import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.http.HttpServletRequest;
import org.entur.oauth2.RoRJwtDecoderBuilder;
import org.entur.oauth2.RorAuthenticationConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolve the @{@link AuthenticationManager} that should authenticate the current JWT token.
 * This is achieved by extracting the issuer from the token and matching it against either the Entur internal
 * issuer URI or the RoR Auth0 issuer URI.
 * The two @{@link AuthenticationManager}s (one for Entur internal Auth0, one for RoR Auth0) are instantiated during the first request and then cached.
 */
@Component
public class MultiIssuerAuthenticationManagerResolver
        implements AuthenticationManagerResolver<HttpServletRequest> {


    @Value("${baba.oauth2.resourceserver.auth0.entur.internal.jwt.audience}")
    private String enturInternalAuth0Audience;

    @Value("${baba.oauth2.resourceserver.auth0.entur.internal.jwt.issuer-uri}")
    private String enturInternalAuth0Issuer;

    @Value("${baba.oauth2.resourceserver.auth0.ror.jwt.audience}")
    private String rorAuth0Audience;

    @Value("${baba.oauth2.resourceserver.auth0.ror.jwt.issuer-uri}")
    private String rorAuth0Issuer;

    @Value("${baba.oauth2.resourceserver.auth0.ror.claim.namespace}")
    private String rorAuth0ClaimNamespace;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BearerTokenResolver resolver = new DefaultBearerTokenResolver();

    private final Map<String, AuthenticationManager> authenticationManagers = new ConcurrentHashMap<>();

    /**
     * Build a @{@link JwtDecoder} for Entur Partner Auth0 tenant.
     *
     * @return a @{@link JwtDecoder} for Auth0.
     */
    private JwtDecoder enturPartnerAuth0JwtDecoder() {
        return new RoRJwtDecoderBuilder().withIssuer(enturInternalAuth0Issuer)
                .withAudience(enturInternalAuth0Audience)
                .withAuth0ClaimNamespace(rorAuth0ClaimNamespace)
                .build();
    }

    /**
     * Build a @{@link JwtDecoder} for Ror Auth0 tenant.
     *
     * @return a @{@link JwtDecoder} for Auth0.
     */
    private JwtDecoder rorAuth0JwtDecoder() {
        return new RoRJwtDecoderBuilder().withIssuer(rorAuth0Issuer)
                .withAudience(rorAuth0Audience)
                .withAuth0ClaimNamespace(rorAuth0ClaimNamespace)
                .build();
    }

    private JwtDecoder jwtDecoder(String issuer) {
        if (enturInternalAuth0Issuer.equals(issuer)) {
            return enturPartnerAuth0JwtDecoder();
        } else if (rorAuth0Issuer.equals(issuer)) {
            return rorAuth0JwtDecoder();
        } else {
            throw new IllegalArgumentException("Received JWT token with unknown OAuth2 issuer: " + issuer);
        }
    }


    private String toIssuer(HttpServletRequest request) {
        try {
            String token = this.resolver.resolve(request);
            String issuer = JWTParser.parse(token).getJWTClaimsSet().getIssuer();
            logger.debug("Received JWT token from OAuth2 issuer {}", issuer);
            return issuer;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    AuthenticationManager fromIssuer(String issuer) {
        return Optional.ofNullable(issuer)
                .map(this::jwtDecoder)
                .map(this::jwtAuthenticationProvider)
                .orElseThrow(() -> new IllegalArgumentException("Received JWT token with null OAuth2 issuer"))::authenticate;
    }

    @Override
    public AuthenticationManager resolve(HttpServletRequest request) {
        return this.authenticationManagers.computeIfAbsent(toIssuer(request), this::fromIssuer);
    }

    private JwtAuthenticationProvider jwtAuthenticationProvider(JwtDecoder jwtDecoder) {
        JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
        jwtAuthenticationProvider.setJwtAuthenticationConverter(new RorAuthenticationConverter());
        return jwtAuthenticationProvider;
    }

}
