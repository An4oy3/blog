package main.controller;

import main.model.request.CommentRequest;
import main.model.request.ModerationRequest;
import main.model.request.ProfileChangeRequest;
import main.model.request.ProfileDeletePhotoRequest;
import main.model.response.*;
import main.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@RestController
public class ApiGeneralController {
    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final PostService postService;
    private final TagService tagService;
    private final GeneralService generalService;



    public ApiGeneralController(InitResponse initResponse, SettingsService settingsService, PostService postService, TagService tagService, GeneralService generalService) {
        this.initResponse = initResponse;
        this.settingsService = settingsService;
        this.postService = postService;
        this.tagService = tagService;
        this.generalService = generalService;
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

    @PostMapping(value = "/api/image", consumes = {"multipart/form-data"})
    @ResponseBody
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Object> image(@RequestPart("image")MultipartFile file) throws IOException {
        return generalService.image(file);
    }

    @PostMapping("/api/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<Object> comment(@RequestBody CommentRequest request, Principal principal){
        return generalService.comment(request, principal);
    }

    @PostMapping(value = "/api/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public boolean moderate(@RequestBody ModerationRequest request, Principal principal){
        return generalService.moderate(request, principal);
    }

    @PostMapping(value = "/api/profile/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> profile(@ModelAttribute ProfileChangeRequest request, Principal principal) throws IOException {
        return generalService.profile(request, principal);
    }

    @PostMapping(value = "/api/profile/my", consumes = "application/json")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> profileDeletePhoto(@RequestBody ProfileDeletePhotoRequest request, Principal principal) throws IOException {
        return generalService.profileDeletePhoto(request, principal);
    }
}
