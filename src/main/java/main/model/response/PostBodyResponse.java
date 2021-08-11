package main.model.response;

import lombok.Data;

import java.util.Date;


@Data
public class PostBodyResponse {
    private Integer id;
    private Long timestamp;
    private UserBodyResponse user;
    private String title;
    private String announce;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer commentCount;
    private Integer viewCount;
}
