package main.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

public class TagResponse {
    @Getter
    @Setter
    private List<Map<String, Double>> tags;
}
