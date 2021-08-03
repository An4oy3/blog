package main.controller;

import main.model.response.AuthResponse;
import main.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiAuthController {
    private final AuthService authService;

    public ApiAuthController(AuthService authService) {
        this.authService = authService;
    }


    @GetMapping("/api/auth/check")
    public AuthResponse check(){
        return authService.getAuthCheck();
    }

}
