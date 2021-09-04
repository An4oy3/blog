package main.controller;

import main.model.request.RegisterRequest;
import main.model.response.RegisterResponse;
import main.service.RegisterService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiPostController {
    private final RegisterService registerService;

    public ApiPostController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping("/api/auth/register")
    public RegisterResponse register(@RequestBody RegisterRequest request){
        return registerService.register(request);
    }
}
