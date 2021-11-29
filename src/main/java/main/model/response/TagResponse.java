package main.model.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagResponse {
    private List<TagBodyResponse> tags;
}
