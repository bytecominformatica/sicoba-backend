package br.com.clairtonluz.sicoba.config.security;

import br.com.clairtonluz.sicoba.config.Constants;
import br.com.clairtonluz.sicoba.config.EnvironmentFactory;
import br.com.clairtonluz.sicoba.model.entity.security.User;
import br.com.clairtonluz.sicoba.model.pojo.UserPojo;
import br.com.clairtonluz.sicoba.service.security.UserService;
import br.com.clairtonluz.sicoba.util.StringUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * Created by clairton on 02/06/17.
 */
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
    private final UserService userService;

    public JWTLoginFilter(String url, HttpMethod method, AuthenticationManager authManager, UserService userService) {
        super(new AntPathRequestMatcher(url, method.name()));
        this.userService = userService;
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException, IOException, ServletException {

        User creds = getCredentials(req);
        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        creds.getUsername(),
                        creds.getPassword(),
                        Collections.emptyList()
                )
        );
    }

    private User getCredentials(HttpServletRequest request) {
        User user = new User();
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtil.isEmpty(authorization) && authorization.startsWith(Constants.BASIC_AUTH_PREFIX)) {
            String token = authorization.substring(Constants.BASIC_AUTH_PREFIX.length() + 1);
            String[] split = new String(Base64.decode(token.getBytes())).split(":");
            user.setUsername(split[0]);
            user.setPassword(split[1]);
        }

        return user;
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest req, HttpServletResponse res, FilterChain chain,
            Authentication auth) throws IOException, ServletException {

        res.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, Constants.ALLOW_ORIGIN);
        UserPojo user = new UserPojo(userService.buscarPorUsername(auth.getName()));
        TokenAuthenticationService.addAuthentication(res, user);
    }
}
