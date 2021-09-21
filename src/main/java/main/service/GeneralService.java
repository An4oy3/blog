package main.service;

import main.model.ModerationStatus;
import main.model.Post;
import main.model.PostComment;
import main.model.User;
import main.model.repositories.PostCommentRepository;
import main.model.repositories.PostRepository;
import main.model.repositories.UserRepository;
import main.model.request.CommentRequest;
import main.model.request.ModerationRequest;
import main.model.request.ProfileChangeRequest;
import main.model.response.ContentAddErrors;
import main.model.response.ContentAddResponse;
import org.imgscalr.Scalr;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.*;
import java.security.Principal;
import java.util.Date;
import java.util.UUID;

@Service
public class GeneralService {
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final UserRepository userRepository;

    public GeneralService(PostRepository postRepository, PostCommentRepository postCommentRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.postCommentRepository = postCommentRepository;
        this.userRepository = userRepository;
    }

    //POST "/api/image/"
    public ResponseEntity<Object> image(MultipartFile file) throws IOException {
        if(((double)file.getSize()/(1024*1024)) > 3.0 || !(getFileExtension(file).equals(".jpg") || getFileExtension(file).equals(".png"))){
            ContentAddResponse response = new ContentAddResponse();
            ContentAddErrors error = new ContentAddErrors();
            error.setImage("Размер файла превышает допустимый размер и/или файл неверного формата");
            response.setErrors(error);
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
            ContentAddErrors error = new ContentAddErrors();
            error.setText("Текст комментария не задан или слишком короткий");
            ContentAddResponse response = new ContentAddResponse();
            response.setErrors(error);
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<?> profile(ProfileChangeRequest request, MultipartFile photo, Principal principal) throws IOException {
        User user = userRepository.findOneByEmail(principal.getName());
        ContentAddResponse response = new ContentAddResponse();
        ContentAddErrors errors = new ContentAddErrors();
        if(photo != null && (double)photo.getSize()/(1024*1024) > 5.0){
            response.setResult(false);
            errors.setPhoto("Фото слишком большое, нужно не более 5 Мб");
            response.setErrors(errors);
            return ResponseEntity.ok(response);
        }
        if(userRepository.findOneByEmail(request.getEmail()) != null && !request.getEmail().equals(user.getEmail())){
            response.setResult(false);
            errors.setEmail("Этот e-mail уже зарегистрирован");
            response.setErrors(errors);
            return ResponseEntity.ok(response);
        }
        if(!request.getName().matches("[А-Яа-яA-Za-z]+")) {
            response.setResult(false);
            errors.setName("Имя указано неверно");
            response.setErrors(errors);
            return ResponseEntity.ok(response);
        }
        if(request.getPassword() != null && request.getPassword().length() < 6 && !request.getPassword().isEmpty()){
            response.setResult(false);
            errors.setPassword("Пароль короче 6-ти символов");
            response.setErrors(errors);
            return ResponseEntity.ok(response);
        }

        user.setName(request.getName() == null ? user.getName() : request.getName());
        user.setEmail(request.getEmail() == null ? user.getEmail() : request.getEmail());
        user.setPassword(request.getPassword() == null ? user.getPassword() : new BCryptPasswordEncoder().encode(request.getPassword()));

        if(photo != null) {
            File filePath = new File("src/main/resources/upload/1/" + UUID.randomUUID().toString().substring(5) + getFileExtension(photo));
            OutputStream os = new FileOutputStream(filePath);
            os.write(photo.getBytes());
            os.close();
            user.setPhoto(filePath.getPath());
        }
        userRepository.save(user);
        response.setResult(true);
        return ResponseEntity.ok(response);
    }
    //====================================


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



}
