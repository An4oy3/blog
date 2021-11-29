package main.service;

import main.model.CaptchaCodes;
import main.model.User;
import main.model.repositories.CaptchaRepository;
import main.model.repositories.UserRepository;
import main.model.request.RegisterRequest;
import main.model.response.RegisterErrors;
import main.model.response.RegisterResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterService {
    private final UserRepository userRepository;
    private final CaptchaRepository captchaRepository;

    public RegisterService(UserRepository userRepository, CaptchaRepository captchaRepository) {
        this.userRepository = userRepository;
        this.captchaRepository = captchaRepository;
    }

    //GET "/api/auth/register"
    public RegisterResponse register(RegisterRequest request){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

        CaptchaCodes captchaCode = captchaRepository.findOneBySecretCode(request.getCaptcha_secret());
        if(!captchaCode.getCode().equalsIgnoreCase(request.getCaptcha())){
            return RegisterResponse.builder()
                    .result(false)
                    .errors(RegisterErrors.builder()
                            .captcha("Код с картинки введён неверно").build())
                    .build();
        }
        if(!request.getName().matches("[А-Яа-яA-Za-z]+")){
            return RegisterResponse.builder()
                    .result(false)
                    .errors(RegisterErrors.builder()
                            .name("Имя указано неверно")
                            .build())
                    .build();
        }
        User user = userRepository.findOneByEmail(request.getEmail());
        if(user == null){
            user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setPassword(encoder.encode(request.getPassword()));
            userRepository.addUser(user.getEmail(), user.getName(), user.getPassword());
            return RegisterResponse.builder().result(true).build();
        } else {
            return RegisterResponse.builder()
                    .result(false)
                    .errors(RegisterErrors.builder()
                            .email("Этот e-mail уже зарегистрирован").build())
                    .build();
        }
    }
    //====================================
}
