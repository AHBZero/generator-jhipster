package <%=packageName%>.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;

/**
 * Created by deividi on 09/02/17.
 */
public class AuthenticationToken extends AbstractAuthenticationToken {

    private static final long serialVersionUID = 1L;

    private final Object principal;
    private String deviceId;
    private String accessToken;
    private String credentials;
    private String userId;

    public AuthenticationToken(Object principal, String credentials, String deviceId) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.deviceId = deviceId;
        setAuthenticated(false);
    }

    public AuthenticationToken(Object principal, String credentials, String userId, Collection<? extends GrantedAuthority> authorities, Map<String, String> deviceParams) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        this.setDetails(deviceParams);
        this.userId = userId;
        super.setAuthenticated(true);
    }

    public AuthenticationToken(Object principal, String credentials, String deviceId, Map<String, String> parameters) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.deviceId = deviceId;
        setDetails(parameters);
        setAuthenticated(false);
    }

    @Override
    public String getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public String getUserId() {
        return userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

}
