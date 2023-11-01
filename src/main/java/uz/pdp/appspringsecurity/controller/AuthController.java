package uz.pdp.appspringsecurity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appspringsecurity.entity.User;
import uz.pdp.appspringsecurity.payload.ApiResult;
import uz.pdp.appspringsecurity.payload.SignInDTO;
import uz.pdp.appspringsecurity.payload.SignUpDTO;
import uz.pdp.appspringsecurity.payload.TokenDTO;
import uz.pdp.appspringsecurity.repository.UserRepository;
import uz.pdp.appspringsecurity.security.JWTProvider;
import uz.pdp.appspringsecurity.service.AuthService;
import uz.pdp.appspringsecurity.security.CurrentUserDetails;
import uz.pdp.appspringsecurity.utils.AppConstants;

@RestController
@RequestMapping(AuthController.BASE_PATH)
@RequiredArgsConstructor
public class AuthController {
    public static final String BASE_PATH = AppConstants.BASE_PATH_V1 + "/auth";
    public static final String LOGIN_PATH = "/login";
    public static final String REGISTER_PATH = "/register";
    public static final String REFRESH_PATH = "/refresh-token";
    public static final String VERIFY_PATH = "/verify";


    private final AuthService authService;
    private final JWTProvider jwtProvider;
    private final UserRepository userRepository;

    @PostMapping(LOGIN_PATH)
    public ApiResult<TokenDTO> login(@Valid @RequestBody SignInDTO signInDTO) {
        return authService.login(signInDTO);
    }


    @PostMapping(REGISTER_PATH)
    public ApiResult<String> register(@RequestBody @Valid SignUpDTO signUpDTO) {
        return authService.register(signUpDTO);
    }


    @GetMapping(REFRESH_PATH)
    public ApiResult<TokenDTO> refreshToken(@RequestHeader(AppConstants.AUTH_HEADER) String accessToken,
                                            @RequestHeader(AppConstants.REFRESH_TOKEN_HEADER) String refreshToken) {

        if (!jwtProvider.accessTokenExpired(accessToken))
            throw new RuntimeException("TOKEN TUGAMABDI");

        if (jwtProvider.refreshTokenExpired(refreshToken))
            throw new RuntimeException("REFRESH TOKEN TUGABDI");

        String userId = jwtProvider.getSubjectFromRefreshToken(refreshToken);
        User user = userRepository.findById(Integer.valueOf(userId)).orElseThrow(RuntimeException::new);

        CurrentUserDetails currentUserDetails = new CurrentUserDetails(user);
        if (!AppConstants.userCanEnter(currentUserDetails))
            throw new RuntimeException();

        TokenDTO tokenDTO = TokenDTO.builder()
                .accessToken(jwtProvider.generateAccessToken(currentUserDetails))
                .refreshToken(jwtProvider.generateRefreshToken(currentUserDetails))
                .build();
        return ApiResult.successResponse(tokenDTO);
    }

    @GetMapping(VERIFY_PATH)
    public ApiResult<String> verifyAccount(@RequestParam String email,
                                           @RequestParam String code) {
        return authService.verifyAccount(email, code);
    }
}
