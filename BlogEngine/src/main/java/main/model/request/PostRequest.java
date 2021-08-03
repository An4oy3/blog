package main.model.request;

import lombok.Getter;
import lombok.Setter;

public class PostRequest {
    @Getter
    @Setter
    private Integer offset;
    @Getter
    @Setter
    private Integer limit;
    @Getter
    @Setter
    private Mode mode;

}
