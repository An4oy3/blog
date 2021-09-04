package main.model.response;

import lombok.Data;

@Data
public class CommentBodyResponse {
    private Integer id;
    private Long timestamp;
    private String text;
    private UserBodyResponse user;
}
