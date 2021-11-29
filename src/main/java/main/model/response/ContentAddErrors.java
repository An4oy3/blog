package main.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentAddErrors {
    private String title;
    private String text;
    private String image;
    private String email;
    private String photo;
    private String name;
    private String password;
}
