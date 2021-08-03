package main.model.response;

import lombok.Getter;
import lombok.Setter;
import main.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


public class AuthResponse {

    @Getter
    @Setter
    private boolean result;
    @Getter
    @Setter
    private User user;
}
