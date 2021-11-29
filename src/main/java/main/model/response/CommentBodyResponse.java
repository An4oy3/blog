package main.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentBodyResponse {
    private Integer id;
    private Long timestamp;
    private String text;
    private LoginResponse.UserBodyResponse user;
}
