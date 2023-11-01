package uz.pdp.appspringsecurity.security;

import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import uz.pdp.appspringsecurity.entity.User;
import uz.pdp.appspringsecurity.payload.ApiResult;
import uz.pdp.appspringsecurity.payload.ErrorData;
import uz.pdp.appspringsecurity.repository.UserRepository;
import uz.pdp.appspringsecurity.utils.AppConstants;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final JWTProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final MessageSource messageSource;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(AppConstants.AUTH_HEADER);
        if (authorization != null) {
            UserDetails userDetails =
                    null;
            try {
                userDetails = authorization.startsWith(AppConstants.BASIC_AUTH) ?
                        getUserByBasicAuth(authorization)
                        : getUserByBearerAuth(authorization);
            } catch (ExpiredJwtException e) {
                setResponseAboutExpired(response);
                return;
            }

            if (userDetails != null) {
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void setResponseAboutExpired(HttpServletResponse response) throws IOException {
        ApiResult<List<ErrorData>> apiResult = ApiResult.errorResponse(
                messageSource.getMessage(
                        "accessTokenExpired",
                        null,
                        "accessTokenExpired",
                        LocaleContextHolder.getLocale()),
                417);
        response.setStatus(417);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(AppConstants.jsonValue(apiResult));
        response.setCharacterEncoding("UTF-8");
    }

    private UserDetails getUserByBearerAuth(String authorization) {

        try {
            authorization = authorization.substring(AppConstants.BEARER_AUTH.length());


            String userId = jwtProvider.getSubjectFromAccessToken(authorization);
            Optional<User> optionalUser = userRepository.findById(Integer.valueOf(userId));
            if (optionalUser.isEmpty())
                return null;
            User user = optionalUser.get();

            CurrentUserDetails principal = new CurrentUserDetails(user);
            if (!AppConstants.userCanEnter(principal))
                return null;
            return principal;
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            return null;
        } catch (Exception e) {
            System.out.println();
            return null;
        }


    }

    private UserDetails getUserByBasicAuth(String authorization) {
        try {
            authorization = authorization.substring(AppConstants.BASIC_AUTH.length());
            byte[] decode = Base64.getDecoder().decode(authorization);
            String[] split = new String(decode).split(":");

            String username = split[0];
            String password = split[1];

            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    username,
                    password
            ));
            return (CurrentUserDetails) authentication.getPrincipal();
        } catch (AuthenticationException e) {
            return null;
        }
    }


}
