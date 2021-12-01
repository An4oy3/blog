package main.service;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.UUIDClock;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import main.model.CaptchaCodes;
import main.model.repositories.CaptchaRepository;
import main.model.repositories.UserRepository;
import main.model.request.LoginRequest;
import main.model.request.RestorePassRequest;
import main.model.response.LoginResponse;
import main.model.response.RegisterErrors;
import main.model.response.RestorePassResponse;
import org.hibernate.type.UUIDBinaryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {

    private String secretKey = "password";

    private final UserRepository userRepository;
    private final CaptchaRepository captchaRepository;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public AuthService(UserRepository userRepository, CaptchaRepository captchaRepository, AuthenticationManager authenticationManager, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.captchaRepository = captchaRepository;
        this.authenticationManager = authenticationManager;
        this.mailSender = mailSender;
    }

    public LoginResponse check(Principal principal){
        if(principal == null){
            return new LoginResponse();
        }
        main.model.User currentUser = userRepository.findOneByEmail(principal.getName());
        if(currentUser == null)
            throw new UsernameNotFoundException(principal.getName());
        return LoginResponse.builder()
                .result(true)
                .user(LoginResponse.UserBodyResponse.builder()
                        .id(currentUser.getId())
                        .email(currentUser.getEmail())
                        .moderation(currentUser.getIsModerator() == 1)
                        .name(currentUser.getName())
                        .photo(currentUser.getPhoto())
                        .build()).build();
    }

    public LoginResponse login(LoginRequest request){
        main.model.User currentUser = userRepository.findOneByEmail(request.getEmail());

        if(currentUser != null && !encoder.matches(request.getPassword(), currentUser.getPassword())){
            return new LoginResponse();
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = (User) auth.getPrincipal();
        currentUser = userRepository.findOneByEmail(user.getUsername());
        return LoginResponse.builder()
                .result(true)
                .user(LoginResponse.UserBodyResponse.builder()
                        .id(currentUser.getId())
                        .email(currentUser.getEmail())
                        .moderation(currentUser.getIsModerator() == 1)
                        .name(currentUser.getName())
                        .photo(currentUser.getPhoto())
                        .build())
                .build();
    }

    public LoginResponse logout(Principal principal){
        SecurityContextHolder.clearContext();
        return LoginResponse.builder()
                .result(true)
                .build();
    }

    public RestorePassResponse restore(RestorePassRequest request) {
        main.model.User user = userRepository.findOneByEmail(request.getEmail());
        if(user == null){
            return RestorePassResponse.builder().result(false).build();
        }
        UUID token = Generators.timeBasedGenerator().generate();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@blog.com");
        message.setTo(user.getEmail());
        message.setSubject("Восстановление пароля");
        message.setText("Ссылка для восстановления пароля " + "http://127.0.0.1:8080/login/change-password/" + token);
        mailSender.send(message);
        user.setCode(token.toString());
        userRepository.save(user);

        return RestorePassResponse.builder().result(true).build();
    }

    public RestorePassResponse password(RestorePassRequest request) {
        CaptchaCodes captcha = captchaRepository.findOneBySecretCode(request.getCaptchaSecret());
        if(captcha == null || !captcha.getCode().equals(request.getCaptcha())){
            return RestorePassResponse.builder().result(false)
                    .errors(RegisterErrors.builder().captcha("Код с картинки введён неверно")
                            .build())
                    .build();
        }
        Date validityToken = new Date(UUID.fromString(request.getToken()).timestamp() + 600000);

        if(validityToken.before(new Date())){
            return RestorePassResponse.builder().result(false)
                    .errors(RegisterErrors.builder().code("Ссылка для восстановления пароля устарела.\n" +
                            "        <a href=\n" +
                            "        http://127.0.0.1:8080/auth/restore>Запросить ссылку снова</a>").build())
                    .build();
        }
        main.model.User user = userRepository.findByCode(request.getToken()).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        user.setPassword(encoder.encode(request.getPassword()));
        userRepository.save(user);
        return RestorePassResponse.builder().result(true).build();
    }
}
