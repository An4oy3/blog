package main.model.response;

import lombok.Data;

@Data
public class ContentAddErrors {
    private String title;
    private String text;
    private String image;
    private String email;
    private String photo;
    private String name;
    private String password;
}
