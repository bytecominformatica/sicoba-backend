package br.com.clairtonluz.sicoba.config.security;

import br.com.clairtonluz.sicoba.config.Constants;
import br.com.clairtonluz.sicoba.model.pojo.UserPojo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static java.util.Collections.emptyList;

/**
 * Created by clairton on 02/06/17.
 */
public class TokenAuthenticationService {

    static void addAuthentication(HttpServletResponse res, UserPojo user) {
        String JWT = Jwts.builder()
                .setSubject(user.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + Constants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, Constants.JWT_SECRET)
                .compact();

        try {
            user.setToken(Constants.TOKEN_AUTH_PREFIX + " " + JWT);
            res.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
            res.getWriter().write(new ObjectMapper().writeValueAsString(user));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    static Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith(Constants.TOKEN_AUTH_PREFIX)) {
            String user = Jwts.parser()
                    .setSigningKey(Constants.JWT_SECRET)
                    .parseClaimsJws(token.replace(Constants.TOKEN_AUTH_PREFIX, ""))
                    .getBody()
                    .getSubject();

            return user != null ?
                    new UsernamePasswordAuthenticationToken(user, null, emptyList()) :
                    null;
        }
        return null;
    }
}
