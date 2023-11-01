package uz.pdp.appspringsecurity.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import uz.pdp.appspringsecurity.controller.AuthController;
import uz.pdp.appspringsecurity.controller.PaymentController;
import uz.pdp.appspringsecurity.security.CurrentUserDetails;

public interface AppConstants {
    String AUTH_HEADER = "Authorization";
    String REFRESH_TOKEN_HEADER = "RefreshToken";
    String BASIC_AUTH = "Basic ";
    String BEARER_AUTH = "Bearer ";
    ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    String BASE_PATH = "/api";
    String BASE_PATH_V1 = BASE_PATH + "/v1";


    String[] OPEN_PAGES = {
            "/home/open",
            AuthController.BASE_PATH + "/**",
    };

    String[] CASHIER_GET_PAGES = {
            PaymentController.BASE_PATH,
            PaymentController.BASE_PATH + "/*",
    };

    static String jsonValue(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean userCanEnter(CurrentUserDetails principal) {
        return (principal.isAccountNonExpired() && principal.isAccountNonLocked()
                && principal.isCredentialsNonExpired() && principal.isEnabled());
    }
}
