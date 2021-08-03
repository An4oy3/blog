package main.controller;

import main.model.request.PostRequest;
import main.model.request.TagRequest;
import main.model.response.PostResponse;
import main.model.response.SettingsResponse;
import main.model.response.TagResponse;
import main.service.PostService;
import main.service.SettingsService;
import main.service.TagService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import main.model.response.InitResponse;

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

    @GetMapping("/api/post")
    public PostResponse post(){
        return postService.getPosts();
    }

    @GetMapping("/api/tag")
    public TagResponse tag(){
        return tagService.getTags();
    }



}
