package br.com.clairtonluz.sicoba.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
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
        try {
            Authentication authentication = TokenAuthenticationService.getAuthentication(request);
            if (authentication == null) {
                throw new BadCredentialsException("Nenhuma usuário encontrado, faça o login com um usuário existente");
            }
            return authentication;
        } catch (ExpiredJwtException e) {
            throw new BadCredentialsException("Sua sessão expirou faça o login novamente", e);
        } catch (SignatureException e) {
            throw new BadCredentialsException("Credenciais Inválida, por favor faça o login", e);
        }
    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Falha na authenticação: " + failed.getMessage());
    }
}
