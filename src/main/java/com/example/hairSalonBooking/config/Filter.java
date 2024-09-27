package com.example.hairSalonBooking.config;

import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.service.TokenService;
import com.nimbusds.jose.proc.SecurityContext;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;

@Component
public class Filter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

//    @Autowired
//            @Qualifier("HandlerExceptionResolver")
//    HandlerExceptionResolver resolver;


    private final List<String> AUTH_PERMISSION = List.of(
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resource/**",
            "/api/login",
            "/api/register"


    );

    public boolean checkIsPublicAPI(String uri){
        //uri: /api/register

        //Neu gap cai api trong list o tren => cho phep truy cap luoon
        AntPathMatcher pathMatcher = new AntPathMatcher();
        //Check token =>false
        return AUTH_PERMISSION.stream().anyMatch(pattern->pathMatcher.match(pattern,uri) );
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
//        // kiem tra request truoc khi dua xuong controller
//        filterChain.doFilter(request, response);
        boolean isPublicAPI = checkIsPublicAPI(request.getRequestURI());

        if (isPublicAPI) {
            filterChain.doFilter(request, response);
        } else {
            String token = getToken(request);

            if (token == null) {
                //ko duoc phep truy cap
//                resolver.resolveException(request,response,null,new Exception("empty token"));
                return;
            }
            //Have token
            //Check token -> if right --> access info account from token

            Account account;
            try {
                account = tokenService.getAccountByToken(token);

            } catch (ExpiredJwtException e) {
                //response token het han
                return;
            } catch (MalformedJwtException malformedJwtException) {
                //response wrong token
                return;
            }
            //token right
            //avaiale to access
            //save info of account
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(account, token, account.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            //Token is oke, access
            filterChain.doFilter(request, response);
        }
    }





    public String getToken(HttpServletRequest request){
        String authHeader =request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.substring(7);
    }
}
