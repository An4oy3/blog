package main.service;

import main.model.repositories.UserRepository;
import main.model.request.LoginRequest;
import main.model.response.LoginResponse;
import main.model.response.UserBodyResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse check(Principal principal){
        if(principal == null){
            return new LoginResponse();
        }
        return fillLoginResponse(principal.getName());
    }

    public LoginResponse login(LoginRequest request){
        main.model.User currentUser = userRepository.findOneByEmail(request.getEmail());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        if(!encoder.matches(request.getPassword(), currentUser.getPassword())){
            return new LoginResponse();
        }

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        User user = (User) auth.getPrincipal();

        return fillLoginResponse(user.getUsername());
    }

    public LoginResponse logout(Principal principal){
        SecurityContextHolder.clearContext();
        LoginResponse response = new LoginResponse();
        response.setResult(true);

        return response;
    }


    private LoginResponse fillLoginResponse(String email){
        main.model.User currentUser = userRepository.findOneByEmail(email);
        if(currentUser == null){
            throw new UsernameNotFoundException(email);
        }
        UserBodyResponse userBodyResponse = new UserBodyResponse();
        userBodyResponse.setId(currentUser.getId());
        userBodyResponse.setEmail(currentUser.getEmail());
        userBodyResponse.setModeration(currentUser.getIsModerator() == 1);
        userBodyResponse.setName(currentUser.getName());
        userBodyResponse.setPhoto(currentUser.getPhoto());

        LoginResponse response = new LoginResponse();
        response.setResult(true);
        response.setUser(userBodyResponse);

        return response;
    }
}
