package main.service;

import main.model.*;
import main.model.repositories.*;
import main.model.request.CommentRequest;
import main.model.request.ModerationRequest;
import main.model.request.ProfileChangeRequest;
import main.model.request.ProfileDeletePhotoRequest;
import main.model.response.ContentAddErrors;
import main.model.response.ContentAddResponse;
import main.model.response.StatisticResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GeneralService {
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;
    private final GlobalSettingsRepository globalSettingsRepository;
    private final PostVoteRepository postVoteRepository;

    public GeneralService(PostRepository postRepository, PostCommentRepository postCommentRepository, UserRepository userRepository, GlobalSettingsRepository globalSettingsRepository, PostVoteRepository postVoteRepository) {
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.userRepository = userRepository;
        this.globalSettingsRepository = globalSettingsRepository;
        this.postVoteRepository = postVoteRepository;
    }

    //POST "/api/image/"
    public ResponseEntity<Object> image(MultipartFile file) throws IOException {
        if(((double)file.getSize()/(1024*1024)) > 3.0 || !(getFileExtension(file).equals(".jpg") || getFileExtension(file).equals(".png"))){
            return new ResponseEntity<>(ContentAddResponse.builder()
                    .errors(ContentAddErrors.builder()
                            .image("Размер файла превышает допустимый размер и/или файл неверного формата").build())
                    .build(), HttpStatus.BAD_REQUEST);
        }

        File filePath = new File("src/main/resources/upload/1/" + UUID.randomUUID().toString().substring(5) + getFileExtension(file));
        OutputStream os = new FileOutputStream(filePath);
        os.write(file.getBytes());
        os.close();
        return new ResponseEntity<>(filePath.getPath(), HttpStatus.OK);
    }
    //====================================

    //POST "/api/comment"
    public ResponseEntity<Object> comment(CommentRequest request, Principal principal){
        if(request.getText().length() < 2){
            return new ResponseEntity<>(ContentAddResponse.builder()
                    .errors(ContentAddErrors.builder().text("Текст комментария не задан или слишком короткий").build())
                    .build(), HttpStatus.BAD_REQUEST);
        }

        int postId = Integer.parseInt(request.getPostId());
        Integer parentId = request.getParentId() == null ? null : Integer.parseInt(request.getParentId());
        if(postId > postRepository.count() || postId < 0 || (parentId != null && (parentId > postCommentRepository.count() || parentId < 0))){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        PostComment postComment = new PostComment();
        postComment.setPost(postRepository.findPostById(postId));
        postComment.setUser(userRepository.findOneByEmail(principal.getName()));
        postComment.setTime(new Date());
        if(parentId != null) {
            postComment.setParentId(postCommentRepository.findOneById(parentId));
        }
        postComment.setText(request.getText());
        int id = postCommentRepository.save(postComment).getId();
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
    //====================================

    public boolean moderate(ModerationRequest request, Principal principal){
        System.out.println("\n\n\n\n\n\n");
        if(request.getPostId() > postRepository.count() || request.getPostId() < 0){
            return false;
        }
        Post post = postRepository.findPostById(request.getPostId());
        post.setModerationStatus(request.getDecision().equals("accept") ? ModerationStatus.ACCEPTED : ModerationStatus.DECLINED);
        post.setModeratorId(userRepository.findOneByEmail(principal.getName()));
        postRepository.save(post);
        return true;
    }

    //POST "/api/profile/my"
    public ResponseEntity<?> profile(ProfileChangeRequest request, Principal principal) throws IOException {
        User user = userRepository.findOneByEmail(principal.getName());

        if(request.getPassword() != null && request.getPassword().length() < 6 && !request.getPassword().isEmpty()){
        return ResponseEntity.ok(ContentAddResponse.builder()
                .errors(ContentAddErrors.builder().password("Пароль короче 6-ти символов").build())
                .build());
        }

        if(request.getName() != null && !request.getName().matches("[А-Яа-яA-Za-z]+")) {
        return ResponseEntity.ok(ContentAddResponse.builder()
                .errors(ContentAddErrors.builder().name("Имя указано неверно").build())
                .build());
        }
        if(userRepository.findOneByEmail(request.getEmail()) != null && !request.getEmail().equals(user.getEmail())){
        return ResponseEntity.ok(ContentAddResponse.builder()
                .errors(ContentAddErrors.builder().email("Этот e-mail уже зарегистрирован").build())
                .build());
        }
        if(request.getPhoto() != null){
            BufferedImage photo = ImageIO.read(request.getPhoto().getInputStream());
            photo = photo.getSubimage(36, 36, 36, 36);
            File filePath = new File("src/main/resources/upload/1/" + UUID.randomUUID().toString().substring(5) + getFileExtension(request.getPhoto()));
            ImageIO.write(photo, "png", filePath);
            user.setPhoto(filePath.getPath());
        }

        user.setName(request.getName() == null ? user.getName() : request.getName());
        user.setEmail(request.getEmail() == null ? user.getEmail() : request.getEmail());
        user.setPassword(request.getPassword() == null ? user.getPassword() : new BCryptPasswordEncoder().encode(request.getPassword()));

        userRepository.save(user);
        return ResponseEntity.ok(ContentAddResponse.builder()
                .result(true).build());
    }
    //====================================

    public ResponseEntity<?> profileDeletePhoto(ProfileDeletePhotoRequest request, Principal principal) throws IOException {
        User user = userRepository.findOneByEmail(principal.getName());
        if(request.getRemovePhoto() != null && request.getRemovePhoto() == 1){
            Files.delete(Path.of(user.getPhoto()));
            userRepository.deletePhoto(user.getId());
        }
        if(request.getPassword() != null && request.getPassword().length() < 6 && !request.getPassword().isEmpty()){
            return ResponseEntity.ok(ContentAddResponse.builder()
                    .errors(ContentAddErrors.builder().password("Пароль короче 6-ти символов").build())
                    .build());
        }
        if(request.getEmail() != null && userRepository.findOneByEmail(request.getEmail()) != null && !request.getEmail().equals(user.getEmail())){
            return ResponseEntity.ok(ContentAddResponse.builder()
                    .errors(ContentAddErrors.builder().email("Этот e-mail уже зарегистрирован").build())
                    .build());
        }

        if(request.getName() != null && !request.getName().matches("[А-Яа-яA-Za-z]+")) {
            return ResponseEntity.ok(ContentAddResponse.builder()
                    .errors(ContentAddErrors.builder().name("Имя указано неверно").build())
                    .build());
        }


        user.setEmail(request.getEmail() == null ? user.getEmail() : request.getEmail());
        user.setName(request.getName() == null ? user.getName() : request.getName());
        user.setPassword(request.getPassword() == null ? user.getPassword() : new BCryptPasswordEncoder().encode(request.getPassword()));

        userRepository.save(user);
        return ResponseEntity.ok(ContentAddResponse.builder()
                .result(true).build());
    }


    private String getFileExtension(MultipartFile file){
        if(file == null){
            return null;
        }
        String fileName = file.getOriginalFilename();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0){
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }


    public StatisticResponse userStatistic(Principal principal) {
        User user = userRepository.findOneByEmail(principal.getName());
        List<Post> posts = postRepository.findAllByUser(user);
        List<PostVote> likes = new ArrayList<>();
        List<PostVote> dislikes = new ArrayList<>();
        posts.forEach(post -> {
            likes.addAll(post.getVotes().stream().filter(postVote -> postVote.getValue() == 1).collect(Collectors.toList()));
            dislikes.addAll(post.getVotes().stream().filter(postVote -> postVote.getValue() == -1).collect(Collectors.toList()));
        });

        return StatisticResponse.builder()
                .postsCount(posts.size())
                .likesCount(likes.size())
                .dislikesCount(dislikes.size())
                .viewsCount(posts.stream().mapToLong(Post::getViewCount).sum())
                .firstPublication(posts.stream().min(Comparator.comparing(Post::getTime)).get().getTime().getTime()/1000)
                .build();

    }

    public ResponseEntity blogStatistic(Principal principal) {
        GlobalSetting setting = globalSettingsRepository.findByCode("STATISTICS_IS_PUBLIC");
        User user = userRepository.findOneByEmail(principal != null ? principal.getName() : "");
        if(setting.getValue().equals("NO") && user != null && user.getIsModerator() == 0){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        List<Post> posts = postRepository.findAll();
        List<PostVote> postVotes = postVoteRepository.findAll();
        List<PostVote> likes = postVotes.stream().filter(postVote -> postVote.getValue() == 1).collect(Collectors.toList());
        List<PostVote> dislikes = postVotes.stream().filter(postVote -> postVote.getValue() == -1).collect(Collectors.toList());

        return ResponseEntity.ok(StatisticResponse.builder().postsCount(posts.size())
                .likesCount(likes.size())
                .dislikesCount(dislikes.size())
                .viewsCount(posts.stream().mapToLong(Post::getViewCount).sum())
                .firstPublication(posts.stream().min(Comparator.comparing(Post::getTime)).get().getTime().getTime()/1000)
                .build());
    }
}
