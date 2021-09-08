package main.controller;

import main.model.response.PostBodyResponse;
import main.model.response.PostResponse;
import main.service.PostService;
import org.springframework.web.bind.annotation.*;

@RestController
public class ApiPostController {
    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }


    @GetMapping("/api/post")
    public PostResponse post(@RequestParam(value = "offset", defaultValue = "0") String offset, @RequestParam(value = "limit", defaultValue = "10") String limit, @RequestParam(value = "mode", defaultValue = "recent")String mode){
        return postService.getPosts(offset, limit, mode);
    }

    @GetMapping("/api/post/search")
    public PostResponse postSearch(@RequestParam(value = "offset") String offset, @RequestParam(value = "limit") String limit, @RequestParam(value = "query", defaultValue = "") String query){
        return postService.getPostsSearch(offset, limit, query);
    }

    @GetMapping("/api/post/byDate")
    public PostResponse getPostsByDate(@RequestParam(value = "offset") String offset, @RequestParam(value = "limit") String limit, @RequestParam(value = "date") String date){
        return postService.getPostsByDate(offset, limit, date);
    }

    @GetMapping("/api/post/byTag")
    public PostResponse getPostsByTag(@RequestParam(value = "offset") String offset, @RequestParam(value = "limit") String limit, @RequestParam(value = "tag") String tag){
        return postService.getPostsByTag(offset, limit, tag);
    }

    @GetMapping("api/post/{id}")
    public PostBodyResponse getPostById(@PathVariable String id){
        return postService.getPostById(id);
    }

}
