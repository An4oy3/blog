package main.controller;

import main.model.request.LoginRequest;
import main.model.request.RegisterRequest;
import main.model.request.RestorePassRequest;
import main.model.response.CaptchaResponse;
import main.model.response.LoginResponse;
import main.model.response.RegisterResponse;
import main.model.response.RestorePassResponse;
import main.service.AuthService;
import main.service.CaptchaService;
import main.service.RegisterService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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
    public LoginResponse check(Principal principal){
        return authService.check(principal);
    }

    @GetMapping("/api/auth/captcha")
    public CaptchaResponse getCaptcha(){
        return captchaService.captchaGenerate();
    }

    @PostMapping("/api/auth/register")
    public RegisterResponse register(@RequestBody RegisterRequest request){
        return registerService.register(request);
    }

    @PostMapping("/api/auth/login")
    public LoginResponse login(@RequestBody LoginRequest request){
        return authService.login(request);
    }

    @GetMapping("/api/auth/logout")
    @PreAuthorize("hasAuthority('user:write')")
    public LoginResponse logout(Principal principal){
        return authService.logout(principal);
    }

    @PostMapping("/api/auth/restore")
    public RestorePassResponse restore(@RequestBody RestorePassRequest request){
        return authService.restore(request);
    }

    @PostMapping("/api/auth/password")
    public RestorePassResponse password(@RequestBody RestorePassRequest request){
        return authService.password(request);
    }

}
