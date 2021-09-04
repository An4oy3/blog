package main.model.response;

import lombok.Data;

@Data
public class RegisterErrors {
    private String email;
    private String name;
    private String password;
    private String captcha;
}
