package main.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterRequest {
    @JsonProperty("e_mail")
    private String email;
    private String password;
    private String name;
    private String captcha;
    private String captcha_secret;
}
