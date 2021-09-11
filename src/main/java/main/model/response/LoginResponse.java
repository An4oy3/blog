package main.model.response;

import lombok.Data;

@Data
public class LoginResponse {
    private boolean result;
    private UserBodyResponse userBodyResponse;
}
