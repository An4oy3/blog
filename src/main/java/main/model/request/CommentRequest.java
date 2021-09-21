package main.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CommentRequest {
    @JsonProperty("parent_id")
    private String parentId;
    @JsonProperty("post_id")
    private String postId;
    private String text;
}
