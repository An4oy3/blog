package main.controller;

import main.model.request.Mode;
import main.model.request.PostRequest;
import main.model.request.TagRequest;
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

    @GetMapping("/api/post")
    public PostResponse post(@RequestParam(value = "offset", defaultValue = "0") String offset, @RequestParam(value = "limit", defaultValue = "10") String limit, @RequestParam(value = "mode", defaultValue = "recent")String mode){
        return postService.getPosts(offset, limit, mode);
    }

    @GetMapping("/api/tag")
    public TagResponse tag(@RequestParam(value = "query", defaultValue = "") String query){
        return tagService.getTags(query);
    }

    /*==============================================================================================================================================================================================================================
    Этап №3
    Методы:
        GET /api/post/search
        GET /api/calendar
        GET /api/post/byDate
        GET /api/post/byTag
        GET /api/post/{ID}
        GET /api/auth/captcha - В контроллере ApiAuthController
        POST /api/auth/register - В контроллере ApiPostController
     */

    //GET /api/post/search
    @GetMapping("/api/post/search")
    public PostResponse postSearch(@RequestParam(value = "offset") String offset, @RequestParam(value = "limit") String limit, @RequestParam(value = "query", defaultValue = "") String query){
        return postService.getPostsSearch(offset, limit, query);
    }
    //====================================

    //GET /api/calendar
    @GetMapping("/api/calendar")
    public CalendarResponse getPostsByYear(@RequestParam(value = "year") String year){

        return postService.getPostsByYear(year);
    }
    //====================================

    //GET /api/post/byDate
    @GetMapping("/api/post/byDate")
    public PostResponse getPostsByDate(@RequestParam(value = "offset") String offset, @RequestParam(value = "limit") String limit, @RequestParam(value = "date") String date){
        return postService.getPostsByDate(offset, limit, date);
    }
    //====================================

    //GET /api/post/byTag
    @GetMapping("/api/post/byTag")
    public PostResponse getPostsByTag(@RequestParam(value = "offset") String offset, @RequestParam(value = "limit") String limit, @RequestParam(value = "tag") String tag){
        return postService.getPostsByTag(offset, limit, tag);
    }
    //====================================

    //GET /api/post/{ID}
    @GetMapping("api/post/{id}")
    public PostBodyResponse getPostById(@PathVariable String id){
        return postService.getPostById(id);
    }
    //====================================



}
