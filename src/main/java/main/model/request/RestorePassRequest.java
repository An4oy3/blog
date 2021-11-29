package main.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestorePassRequest {
    private String email;
    private String password;
    private String code;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
