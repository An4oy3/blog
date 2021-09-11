package main.model.response;

import lombok.Data;

@Data
public class UserBodyResponse {
    private Integer id;
    private String name;
    private String photo;
    private String email;
    private boolean moderation;
    private int moderationCount;
    private boolean settings;
}
