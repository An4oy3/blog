package main.service;

import main.model.CaptchaCodes;
import main.model.CaptchaRepository;
import main.model.User;
import main.model.UserRepository;
import main.model.request.RegisterRequest;
import main.model.response.RegisterErrors;
import main.model.response.RegisterResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RegisterService {
    private final UserRepository userRepository;
    private final CaptchaRepository captchaRepository;

    public RegisterService(UserRepository userRepository, CaptchaRepository captchaRepository) {
        this.userRepository = userRepository;
        this.captchaRepository = captchaRepository;
    }

    public RegisterResponse register(RegisterRequest request){
        RegisterResponse response = new RegisterResponse();
        RegisterErrors errors = new RegisterErrors();

        CaptchaCodes captchaCode = captchaRepository.findOneBySecretCode(request.getCaptcha_secret());
        if(!captchaCode.getCode().equalsIgnoreCase(request.getCaptcha())){
            response.setResult(false);
            errors.setCaptcha("Код с картинки введён неверно");
            response.setErrors(errors);
            return response;
        }
        User user = userRepository.findOneByEmail(request.getEmail());
        if(user == null){
            user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            userRepository.addUser(user.getEmail(), user.getName(), user.getPassword());
            response.setResult(true);
            return response;
        } else {
            response.setResult(false);
            errors.setEmail("Этот e-mail уже зарегистрирован");
            response.setErrors(errors);
            return response;
        }
    }
}
