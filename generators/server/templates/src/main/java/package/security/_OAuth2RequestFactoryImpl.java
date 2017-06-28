package <%=packageName%>.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by deividi on 21/07/16.
 */
@Service("oAuth2RequestFactory")
public class OAuth2RequestFactoryImpl extends DefaultOAuth2RequestFactory implements OAuth2RequestFactory {

    @Autowired
    public OAuth2RequestFactoryImpl(ClientDetailsService clientDetailsService) {
        super(clientDetailsService);
    }

    @Override
    public AuthorizationRequest createAuthorizationRequest (Map<String, String> authorizationParameters) {
        return super.createAuthorizationRequest(authorizationParameters);
    }

    @Override
    public OAuth2Request createOAuth2Request (AuthorizationRequest request) {
        return super.createOAuth2Request(request);
    }

    @Override
    public OAuth2Request createOAuth2Request (ClientDetails client, TokenRequest tokenRequest) {
        return tokenRequest.createOAuth2Request(client);
    }

    @Override
    public TokenRequest createTokenRequest (Map<String, String> requestParameters, ClientDetails authenticatedClient) {
        return super.createTokenRequest(requestParameters, authenticatedClient);
    }

    @Override
    public TokenRequest createTokenRequest (AuthorizationRequest authorizationRequest, String grantType) {
        return super.createTokenRequest(authorizationRequest, grantType);
    }
}
