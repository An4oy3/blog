package main.model.response;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarResponse {
    private List<Integer> years;
    private Map<String, Integer> posts;
}
