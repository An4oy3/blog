package main.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileChangeRequest {
    private MultipartFile photo;
    private String name;
    private String email;
    private String password;
    private Integer removePhoto;
}
