package main.controller;

import main.model.response.AuthResponse;
import main.model.response.CaptchaResponse;
import main.service.AuthService;
import main.service.CaptchaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiAuthController {
    private final AuthService authService;
    private final CaptchaService captchaService;

    public ApiAuthController(AuthService authService, CaptchaService captchaService) {
        this.authService = authService;
        this.captchaService = captchaService;
    }


    @GetMapping("/api/auth/check")
    public AuthResponse check(){
        return authService.getAuthCheck();
    }

    @GetMapping("/api/auth/captcha")
    public CaptchaResponse getCaptcha(){
        return captchaService.captchaGenerate();
    }

}
