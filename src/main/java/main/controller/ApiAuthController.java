package main.controller;

import main.model.request.RegisterRequest;
import main.model.response.AuthResponse;
import main.model.response.CaptchaResponse;
import main.model.response.RegisterResponse;
import main.service.AuthService;
import main.service.CaptchaService;
import main.service.RegisterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiAuthController {
    private final AuthService authService;
    private final CaptchaService captchaService;
    private final RegisterService registerService;

    public ApiAuthController(AuthService authService, CaptchaService captchaService, RegisterService registerService) {
        this.authService = authService;
        this.captchaService = captchaService;
        this.registerService = registerService;
    }


    @GetMapping("/api/auth/check")
    public AuthResponse check(){
        return authService.getAuthCheck();
    }

    @GetMapping("/api/auth/captcha")
    public CaptchaResponse getCaptcha(){
        return captchaService.captchaGenerate();
    }

    @PostMapping("/api/auth/register")
    public RegisterResponse register(@RequestBody RegisterRequest request){
        return registerService.register(request);
    }

}
