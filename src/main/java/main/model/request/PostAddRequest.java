package main.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostAddRequest {
    private Date timestamp;
    private int active;
    private String title;
    private List<String> tags;
    private String text;
}
