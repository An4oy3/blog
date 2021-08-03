package main.service;

import main.model.response.AuthResponse;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public AuthResponse getAuthCheck(){
        AuthResponse authResponse = new AuthResponse();
        authResponse.setResult(false);
        return authResponse;
    }
}
