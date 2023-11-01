package uz.pdp.appspringsecurity.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.appspringsecurity.controller.AuthController;
import uz.pdp.appspringsecurity.entity.Role;
import uz.pdp.appspringsecurity.entity.User;
import uz.pdp.appspringsecurity.exceptions.RestException;
import uz.pdp.appspringsecurity.payload.ApiResult;
import uz.pdp.appspringsecurity.payload.SignInDTO;
import uz.pdp.appspringsecurity.payload.SignUpDTO;
import uz.pdp.appspringsecurity.payload.TokenDTO;
import uz.pdp.appspringsecurity.repository.RoleRepository;
import uz.pdp.appspringsecurity.repository.UserRepository;
import uz.pdp.appspringsecurity.security.JWTProvider;
import uz.pdp.appspringsecurity.security.CurrentUserDetails;

import java.util.Objects;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JWTProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JavaMailSender javaMailSender;

    @Value("${app.hostname}")
    private String hostname;

    public AuthService(UserRepository userRepository,
                       @Lazy AuthenticationManager authenticationManager,
                       JWTProvider jwtProvider,
                       @Lazy PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository,
                       JavaMailSender javaMailSender) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.javaMailSender = javaMailSender;
    }


    public ApiResult<TokenDTO> login(SignInDTO signInDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInDTO.getUsername(),
                        signInDTO.getPassword()));

        CurrentUserDetails user = (CurrentUserDetails) authentication.getPrincipal();


        return ApiResult.successResponse(TokenDTO.builder()
                .accessToken(jwtProvider.generateAccessToken(user))
                .refreshToken(jwtProvider.generateRefreshToken(user))
                .build());
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username + " topilmadi"));
        return new CurrentUserDetails(user);
    }

    public ApiResult<String> register(SignUpDTO signUpDTO) {
        if (!Objects.equals(signUpDTO.getPassword(), signUpDTO.getPrePassword()))
            throw new RestException("Password not match");

        if (userRepository.existsByEmail(signUpDTO.getUsername()))
            throw new RestException("User already registered", HttpStatus.CONFLICT);

        Role role = roleRepository.findByIsUserTrue().orElseThrow(() -> new RestException("user role topilmadi"));

        String verificationCode = UUID.randomUUID().toString();

        User user = User.builder()
                .name(signUpDTO.getName())
                .email(signUpDTO.getUsername())
                .password(passwordEncoder.encode(signUpDTO.getPassword()))
                .role(role)
                .verificationCode(verificationCode)
                .build();

        userRepository.save(user);
        new Thread(() -> sendEmailForRegister(signUpDTO.getUsername(), verificationCode)).start();

        return ApiResult.successResponse("Successfully registered");
    }

    private void sendEmailForRegister(String toEmail, String code) {
        try {
            MimeMessage mailMessage = javaMailSender.createMimeMessage();

            mailMessage.setRecipients(Message.RecipientType.TO, toEmail);
            mailMessage.setSubject("Please verify");

            String verificationUrl = hostname + AuthController.BASE_PATH + AuthController.VERIFY_PATH + "?email=" + toEmail + "&code=" + code;
            mailMessage.setContent(
                    String.format("""
                            <h1>If you registered out web-site, please verify your account via press link </h1>
                            <button style="background-color: #6666ee; color: white"><a href="%s">Verify account</a></button>
                            """, verificationUrl),
                    "text/html; charset=utf-8"
            );

            //http://localhost/api/auth/verify?email=siro@gmail.com&code=12345

            javaMailSender.send(mailMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public ApiResult<String> verifyAccount(String email, String code) {
        //1-user not found
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RestException("User not found"));

        //2-enabled true
        if (user.isEnabled())
            throw new RestException("User already verified");

        //3-code not match
        if (!code.equals(user.getVerificationCode()))
            throw new RestException("Code not match");

        user.setEnabled(true);
        user.setVerificationCode(null);
        userRepository.save(user);

        return ApiResult.successResponse("User activated");
    }
}
