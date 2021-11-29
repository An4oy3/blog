package main.service;

import main.model.repositories.UserRepository;
import main.model.request.LoginRequest;
import main.model.request.RestorePassRequest;
import main.model.response.LoginResponse;
import main.model.response.RestorePassResponse;
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

import java.security.Principal;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JavaMailSender mailSender;

    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager, JavaMailSender mailSender) {
        this.userRepository = userRepository;
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
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
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

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@blog.com");
        message.setTo(user.getEmail());
        message.setSubject("Восстановление пароля");
        String token = UUID.randomUUID().toString().replace("-", "");
        message.setText("Ссылка для восстановления пароля " + "http://blog.me/login/change-password/" + token);
        mailSender.send(message);
        user.setCode(token);
        userRepository.save(user);

        return RestorePassResponse.builder().result(true).build();
    }

    public RestorePassResponse password(RestorePassRequest request) {
        return null;
    }
}
