package main.service;

import com.github.cage.Cage;
import main.model.CaptchaCodes;
import main.model.CaptchaRepository;
import main.model.response.CaptchaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Formatter;
import java.util.List;
import java.util.UUID;

@Service
@Component
public class CaptchaService {
    @Value("${blog.captchaDeleteTimeMinutes}")
    private String captchaTimeDelete;
    private final CaptchaRepository captchaRepository;

    public CaptchaService(CaptchaRepository captchaRepository) {
        this.captchaRepository = captchaRepository;
    }

    public CaptchaResponse captchaGenerate(){
        Cage cage = new Cage();

        String code = cage.getTokenGenerator().next();
        String secret = UUID.randomUUID().toString();

        captchaRepository.addCaptcha(secret, code);
        CaptchaCodes captchaCode = captchaRepository.findOneBySecretCode(secret);

        CaptchaResponse captchaResponse = new CaptchaResponse();

        byte[] image = cage.draw(captchaCode.getCode());
        String base64 = "data:image/png;base64, " + Base64.getEncoder().encodeToString(image);
        captchaResponse.setImage(base64);
        captchaResponse.setSecret(captchaCode.getSecret_code());

        //Удаление устаревших каптч из БД
        LocalDateTime deleteTime = LocalDateTime.now().minusMinutes(Long.parseLong(captchaTimeDelete));
        captchaRepository.deleteCaptcha(deleteTime);

        return captchaResponse;
    }
}
