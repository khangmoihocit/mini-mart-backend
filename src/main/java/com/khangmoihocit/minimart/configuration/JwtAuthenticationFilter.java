package com.khangmoihocit.minimart.configuration;

import com.khangmoihocit.minimart.dto.UserDetailsCustom;
import com.khangmoihocit.minimart.repository.TokenRepository;
import com.khangmoihocit.minimart.utils.JwtUtil;
import com.khangmoihocit.minimart.service.impl.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j(topic = "JWT AUTHENTICATION  FILTER")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    JwtUtil jwtUtil;
    UserDetailsServiceImpl userDetailService;
    TokenRepository tokenRepository;
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    //trước khi vào controller sẽ vào filter này để check token, check đầu -> security config
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String accessToken = authHeader.substring(7);
            final String userEmail = jwtUtil.extractUsername(accessToken);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if(userEmail != null && authentication == null){
                //ktra user có trong db ko
                UserDetailsCustom userDetails = (UserDetailsCustom) userDetailService.loadUserByUsername(userEmail);

                if(jwtUtil.validateToken(accessToken, userDetails.getUsername()) && userDetails.isEnabled()){
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }else{
                    log.warn("Authentication failed for user {}: Account is disabled.", userEmail);
                }
            }
            filterChain.doFilter(request, response);
            return;

        }
        catch(ExpiredJwtException e){
            log.warn("JWT Token has expired: {}", e.getMessage());
            // Khi token hết hạn, ủy quyền cho EntryPoint để trả về lỗi 401
            jwtAuthenticationEntryPoint.commence(request, response, new AuthenticationException("JWT Token has expired") {});
        }
        catch (Exception e) {
            log.error("JWT Token error: {}", e.getMessage());
            // Các lỗi JWT khác cũng trả về 401
            jwtAuthenticationEntryPoint.commence(request, response, new AuthenticationException("JWT Token error") {});
        }
//        filterChain.doFilter(request, response);
    }
}