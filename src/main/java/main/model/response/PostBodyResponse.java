package main.model.response;

import lombok.Data;

import java.util.List;


@Data
public class PostBodyResponse {
    private Integer id;
    private Long timestamp;
    private boolean active;
    private UserBodyResponse user;
    private String title;
    private String text;
    private String announce;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer commentCount;
    private Integer viewCount;
    private List<CommentBodyResponse> comments;
    private List<String> tags;
}
