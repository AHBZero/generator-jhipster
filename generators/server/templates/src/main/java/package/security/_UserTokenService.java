package <%=packageName%>.security;

import java.util.List;

/**
 * Created by deividi on 21/07/16.
 */
public interface UserTokenService {

    List<String> findTokensByUsername(String username);

    boolean removeToken(String token);

    boolean removeTokensByUsername(String username);

}
