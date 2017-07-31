package br.com.clairtonluz.sicoba.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by clairton on 04/06/17.
 */
public class JWTAuthenticationFilter extends GenericFilterBean {
    private AuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    private AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            chain.doFilter(req, res);
        } else {
            try {
                Authentication authentication = attemptAuthentication(request, response);
                successfulAuthentication(request, response, chain, authentication);
            } catch (AuthenticationException e) {
                unsuccessfulAuthentication(request, response, e);
            }
        }
    }

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        System.out.println("attemptAuthentication");
        Authentication authentication = null;
        try {
            authentication = TokenAuthenticationService.getAuthentication(request);
            if (authentication == null) {
                throw new BadCredentialsException("Nenhuma usuário encontrado, faça o login com um usuário existente");
            }
        } catch (ExpiredJwtException e) {
            throw new BadCredentialsException("Sua sessão expirou faça o login novamente", e);
        } catch (SignatureException e) {
            throw new BadCredentialsException("Credenciais Inválida, por favor faça o login", e);
        }
        return authentication;
    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        System.out.println("successfulAuthentication");
        chain.doFilter(request, response);
//        response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, Constants.ALLOW_ORIGIN);
//        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        System.out.println("unsuccessfulAuthentication");
        failureHandler.onAuthenticationFailure(request, response, failed);
    }
}
