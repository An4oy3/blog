package main.model.response;

import lombok.Data;

@Data
public class ContentAddResponse {
    private boolean result;
    private ContentAddErrors errors;
}
