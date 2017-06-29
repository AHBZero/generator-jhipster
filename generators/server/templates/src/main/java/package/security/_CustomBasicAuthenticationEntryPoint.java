package <%=packageName%>.security;

import <%=packageName%>.web.rest.errors.ErrorVM;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by deividi on 11/02/17.
 */
@Configurable
public class CustomBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authEx)
        throws IOException, ServletException {
        response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ErrorVM error = new ErrorVM("Not authorized", authEx.getMessage());
        PrintWriter writer = response.getWriter();
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        writer.print(objectMapper.writeValueAsString(error));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        setRealmName("<%= baseName %>");
    }

    @Override
    public String getRealmName() {
        return "<%= baseName %>";
    }

    @Override
    public void setRealmName(String realmName) {
        super.setRealmName(realmName);
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
