package main.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private boolean result;
    private UserBodyResponse user;

    @lombok.Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserBodyResponse {
        private Integer id;
        private String name;
        private String photo;
        private String email;
        private boolean moderation;
        private int moderationCount;
        private boolean settings;
    }

}


