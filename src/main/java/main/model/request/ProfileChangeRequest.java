package main.model.request;

import lombok.Data;

@Data
public class ProfileChangeRequest {
    private String name;
    private String email;
    private String password;
    private Integer removePhoto;
}
