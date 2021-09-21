package main.model.request;

import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class PostAddRequest {
    private Date timestamp;
    private int active;
    private String title;
    private List<String> tags;
    private String text;
}
