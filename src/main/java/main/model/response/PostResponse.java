package main.model.response;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Integer count;
    private List<PostBodyResponse> posts;
}
