package main.controller;

import main.model.response.*;
import main.service.PostService;
import main.service.SettingsService;
import main.service.TagService;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiGeneralController {
    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final PostService postService;
    private final TagService tagService;



    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService, PostService postService, TagService tagService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.postService = postService;
        this.tagService = tagService;
    }

    @GetMapping("/api/init")
    public InitResponse init(){
        return initResponse;
    }

    @GetMapping("/api/settings")
    public SettingsResponse settings(){
        return settingsService.getGlobalSettings();
    }



    @GetMapping("/api/tag")
    public TagResponse tag(@RequestParam(value = "query", defaultValue = "") String query){
        return tagService.getTags(query);
    }

    @GetMapping("/api/calendar")
    public CalendarResponse getPostsByYear(@RequestParam(value = "year") String year){
        return postService.getPostsByYear(year);
    }

}
