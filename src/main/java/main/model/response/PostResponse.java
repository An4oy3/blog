package main.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class PostResponse {
    @Getter
    @Setter
    private Integer count;
    @Getter
    @Setter
    private List<PostBodyResponse> posts;
}
