package main.model.response;

import lombok.Data;

@Data
public class RegisterResponse {
    private boolean result;
    private RegisterErrors errors;
}
