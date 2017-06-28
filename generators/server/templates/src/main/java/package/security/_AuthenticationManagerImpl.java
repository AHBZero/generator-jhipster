package <%=packageName%>.security;

/**
 * Created by deividi on 21/07/16.
 */

import <%=packageName%>.domain.Authority;
import <%=packageName%>.domain.User;
import <%=packageName%>.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.exceptions.UserDeniedAuthorizationException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by deividi on 14/01/16.
 */
@Service("authenticationManager")
@Primary
public class AuthenticationManagerImpl implements AuthenticationManager {

    private final Logger log = LoggerFactory.getLogger(AuthenticationManagerImpl.class);

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JdbcClientDetailsService jdbcClientDetailsService;

    @Autowired
    public AuthenticationManagerImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JdbcClientDetailsService jdbcClientDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcClientDetailsService = jdbcClientDetailsService;
    }

    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        AuthenticationToken authenticationToken = (AuthenticationToken) authentication;
        String login = (String) authentication.getPrincipal();

        log.info("Authenticating login {}", login);

        if (login == null) {
            throw new BadCredentialsException("username not found");
        }

        User user = userRepository.findOneByLogin(login).orElseThrow(() -> new BadCredentialsException("User not Found."));

        if (!user.getActivated()) {
            throw new UserDeniedAuthorizationException("User " + login + " was not activated");
        }

        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("Invalid password.");
        }
        if (!passwordEncoder.matches((CharSequence) authentication.getCredentials(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password.");
        }

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Authority authority : user.getAuthorities()) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getName());
            grantedAuthorities.add(grantedAuthority);
        }

        @SuppressWarnings("unchecked") Map<String, String> authenticationDetails = (Map<String, String>) authentication.getDetails();
        List<ClientDetails> details = jdbcClientDetailsService.listClientDetails();
        Optional<ClientDetails> clientId = details.stream().filter(d -> d.getClientId().equals(authenticationDetails.get("client_id"))).findFirst();

        if (!clientId.isPresent() || !authenticationDetails.get("client_id").equals(clientId.get().getClientId())) {
            throw new BadCredentialsException("Invalid clientid perauthentication.");
        }

        return new AuthenticationToken(user.getLogin(), user.getPassword(), user.getId(), grantedAuthorities, authenticationDetails);
    }

}
