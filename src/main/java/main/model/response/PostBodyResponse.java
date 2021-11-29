package main.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostBodyResponse {
    private Integer id;
    private Long timestamp;
    private boolean active;
    private LoginResponse.UserBodyResponse user;
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
