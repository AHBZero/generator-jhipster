package <%=packageName%>.security;

import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by deividi on 09/02/17.
 */
public class CustomTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String GRANT_TYPE = "grant_type";
    private static final String REFRESH_TOKEN = "refresh_token";
    private final AuthenticationManager authenticationManager;
    private final ClientDetailsService clientDetailsService;
    private final AuthorizationServerTokenServices tokenServices;
    private final TokenStore tokenStore;

    public CustomTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, AuthenticationManager authenticationManager, TokenStore tokenStore) {
        super(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE_PASSWORD);
        this.authenticationManager = authenticationManager;
        this.tokenServices = tokenServices;
        this.clientDetailsService = clientDetailsService;
        this.tokenStore = tokenStore;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> requestParameters = tokenRequest.getRequestParameters();
        String grantType = requestParameters.get(GRANT_TYPE);

        if (!grantType.equalsIgnoreCase(REFRESH_TOKEN)) {

            Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());

            String username = parameters.get("username");

            if (parameters.get("email") != null) {
                username = parameters.get("email");
            }

            String deviceId = parameters.get("device_id");
            String password = parameters.get("password");
            parameters.remove("password");

            Authentication userAuth = new AuthenticationToken(username, password, deviceId, parameters);
            try {
                userAuth = authenticationManager.authenticate(userAuth);
            } catch (AccountStatusException | BadCredentialsException ase) {
                throw new BadCredentialsException(ase.getMessage());
            }

            if (userAuth == null || !userAuth.isAuthenticated()) {
                throw new InvalidGrantException("Could not authenticate user: " + username);
            }

            OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
            return new OAuth2Authentication(storedOAuth2Request, userAuth);

        } else {
            String refresh = requestParameters.get("refresh_token");
            OAuth2RefreshToken oAuth2RefreshToken = new CustomOAuth2AccessToken(refresh);
            OAuth2Authentication oAuth2Authentication = tokenStore.readAuthenticationForRefreshToken(oAuth2RefreshToken);

            Map<String, String> parameters = new LinkedHashMap<>(tokenRequest.getRequestParameters());

            parameters.put("device_id", parameters.get("device_id"));

            oAuth2Authentication.setDetails(parameters);
            return oAuth2Authentication;
        }
    }

    protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
        if (tokenRequest.getGrantType().equals("password")) {
            return tokenServices.createAccessToken(getOAuth2Authentication(client, tokenRequest));
        } else {
            return tokenServices.refreshAccessToken(tokenRequest.getRequestParameters().get("refresh_token"), tokenRequest);
        }
    }

    public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {

        String clientId = tokenRequest.getClientId();
        ClientDetails client = clientDetailsService.loadClientByClientId(clientId);
        validateGrantType(grantType, client);

        logger.debug("Getting access token for: " + clientId);

        return getAccessToken(client, tokenRequest);

    }
}
