package main.model.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
public class CalendarResponse {
    @Getter
    @Setter
    private List<Integer> years;
    @Getter
    @Setter
    private Map<String, Integer> posts;
}
