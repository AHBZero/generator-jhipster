package <%=packageName%>.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Deividi
 */
@Component("authorizationServerTokenServices")
@Primary
public class AuthorizationServerTokenServicesImpl extends DefaultTokenServices implements AuthorizationServerTokenServices, UserTokenService {

    private final JdbcTokenStore tokenStore;
    private final ClientDetailsService clientDetailsService;

    @Autowired
    private JdbcClientDetailsService jdbcClientDetailsService;

    @Autowired
    public AuthorizationServerTokenServicesImpl(JdbcTokenStore tokenStore, ClientDetailsService clientDetailsService) {
        setTokenStore(tokenStore);
        setClientDetailsService(clientDetailsService);
        setTokenEnhancer(new CustomTokenEnhancer());
        setSupportRefreshToken(true);
        setReuseRefreshToken(false);

        this.tokenStore = tokenStore;
        this.clientDetailsService = clientDetailsService;
    }

    @Transactional
    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        return super.createAccessToken(authentication);
    }

    @Transactional
    @Override
    public OAuth2AccessToken refreshAccessToken(String refreshToken, TokenRequest tokenRequest) throws AuthenticationException {
        return super.refreshAccessToken(refreshToken, tokenRequest);
    }

    @Transactional(readOnly = true)
    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return super.getAccessToken(authentication);
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException {
        OAuth2AccessToken accessToken = tokenStore.readAccessToken(accessTokenValue);
        if (accessToken == null) {
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        } else if (accessToken.isExpired()) {
            tokenStore.removeAccessToken(accessToken);
            throw new InvalidTokenException("Access token expired: " + accessTokenValue);
        }

        OAuth2Authentication result = tokenStore.readAuthentication(accessToken);

        if (clientDetailsService != null) {
            String clientId = result.getOAuth2Request().getClientId();
            try {
                clientDetailsService.loadClientByClientId(clientId);
            } catch (ClientRegistrationException e) {
                throw new InvalidTokenException("Client not valid: " + clientId, e);
            }
        }

        return result;
    }

    @Override
    public boolean revokeToken(String tokenValue) {
        return super.revokeToken(tokenValue);
    }

    @Override
    public List<String> findTokensByUsername(String username) {
        return getAllTokensForUser(username).collect(Collectors.toList());
    }

    @Override
    public boolean removeToken(String token) {
        return revokeToken(token);
    }

    @Override
    public boolean removeTokensByUsername(String username) {
        getAllTokensForUser(username).forEach(this::removeToken);
        return true;
    }

    private Stream<String> getAllTokensForUser(String username) {
        return jdbcClientDetailsService.listClientDetails()
            .stream()
            .map(detail -> tokenStore.findTokensByClientIdAndUserName(detail.getClientId(), username))
            .reduce((reducer, tokensPerClientId) -> {
                reducer.addAll(tokensPerClientId);
                return reducer;
            })
            .orElseGet(ArrayList::new)
            .stream()
            .filter(Objects::nonNull)
            .map(OAuth2AccessToken::getValue);
    }
}
