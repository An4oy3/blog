package main.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProfileDeletePhotoRequest {
    private String photo;
    private String password;
    private String name;
    private String email;
    private Integer removePhoto;
}
