package main.service;

import main.model.*;
import main.model.repositories.PostRepository;
import main.model.repositories.Tag2PostRepository;
import main.model.repositories.TagRepository;
import main.model.repositories.UserRepository;
import main.model.response.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.util.*;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final Tag2PostRepository tag2PostRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository, Tag2PostRepository tag2PostRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.tag2PostRepository = tag2PostRepository;
        this.userRepository = userRepository;
    }

    //Этап 2. Метод GET /api/post
    public PostResponse getPosts(String offset, String limit, String mode){
        Sort sort =  Sort.by(Sort.Direction.DESC, "time"); //Сортировка по умолчанию, по дате - от новых к старым;

        if(mode.equals("popular"))
            sort = Sort.by("comments.size").descending();
        else if(mode.equals("best"))
            sort = Sort.by("votes.size").descending();
        else if(mode.equals("early"))
            sort = Sort.by(Sort.Direction.ASC, "time");

        Pageable pageable = PageRequest.of(Integer.parseInt(offset), Integer.parseInt(limit), sort);
        Page<Post> list = postRepository.findAllPost(pageable);
        List<PostBodyResponse> posts = fillPostBodyResponseList(list);
        return setPostResponseValue(posts);
    }
    //====================================

    //GET /api/post/byTag
    public PostResponse getPostsByTag(String offset, String limit, String tagName){
        List<PostBodyResponse> posts = new ArrayList<>();
        List<Tag> tagList = tagRepository.findAllByName(tagName);
        for (Tag tag : tagList) {
            List<Post> postsFromTag = tag.getPostList(); // Список постов по переданному тегу
            for (Post post : postsFromTag) {
                PostBodyResponse postBodyResponse = fillPostBodyResponse(post);
                posts.add(postBodyResponse);
            }
        }
        return setPostResponseValue(posts);
    }
    //====================================

    //GET /api/post/search
    public PostResponse getPostsSearch(String offset, String limit, String query){
        Pageable pageable = PageRequest.of(Integer.parseInt(offset), Integer.parseInt(limit));
        Page<Post> posts = postRepository.findAllPostByQuery(query, pageable);

        List<PostBodyResponse> postsResult = fillPostBodyResponseList(posts);

        return setPostResponseValue(postsResult);
    }
    //====================================

    //GET /api/calendar
    public CalendarResponse getPostsByYear(String year){
        CalendarResponse calendarResponse = new CalendarResponse();
        try {
            int y = Integer.parseInt(year);
        } catch (NumberFormatException e){
           Calendar date = Calendar.getInstance();
           int y = date.get(Calendar.YEAR);
           year = String.valueOf(y);
        }

        LocalDate beginYear = LocalDate.ofYearDay(Integer.parseInt(year), 1);
        LocalDate endYear = LocalDate.ofYearDay(Integer.parseInt(year), 365);
        List postsByYear = postRepository.findAllPostsByYear(beginYear, endYear);
        List<Integer> allYears = postRepository.findAllYears();
        Map<String, Integer> posts = new TreeMap<>();
        for (Object o : postsByYear) {
            Object obj[] = (Object[]) o;
            try {
                String date = obj[0].toString().substring(0, obj[0].toString().indexOf(" "));
                Integer postsByDate = Integer.parseInt(obj[1].toString());
                posts.put(date, postsByDate);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        calendarResponse.setPosts(posts);
        calendarResponse.setYears(allYears);
        return calendarResponse;
    }
    //====================================

    //Метод GET /api/post/byDate
    public PostResponse getPostsByDate(String offset, String limit, String date){
        Pageable pageable = PageRequest.of(Integer.parseInt(offset), Integer.parseInt(limit));
        Page<Post> resultList = postRepository.findAllByTime(date, pageable);
        List<PostBodyResponse> posts = fillPostBodyResponseList(resultList);

        return setPostResponseValue(posts);
    }
    //====================================

    //GET /api/post/{ID}
    public PostBodyResponse getPostById(@PathVariable String id){
        if(id.isEmpty() || Integer.parseInt(id) < 0 || Integer.parseInt(id) > postRepository.count()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Post post = postRepository.findPostById(Integer.parseInt(id));
        PostBodyResponse postBodyResponse = fillPostBodyResponse(post);

        postBodyResponse.setActive(post.getIsActive() == 1);
        postBodyResponse.setText(post.getText());

        List<CommentBodyResponse> commentBodyResponses = new ArrayList<>();
        for (PostComments comment : post.getComments()) {
            CommentBodyResponse commentBodyResponse = new CommentBodyResponse();
            commentBodyResponse.setId(comment.getId());
            commentBodyResponse.setText(comment.getText());
            commentBodyResponse.setTimestamp(comment.getTime().getTime() / 1000);
                UserBodyResponse userBodyResponse = new UserBodyResponse();
                userBodyResponse.setId(comment.getUser().getId());
                userBodyResponse.setName(comment.getUser().getName());
                userBodyResponse.setPhoto(comment.getUser().getPhoto());
            commentBodyResponse.setUser(userBodyResponse);

            commentBodyResponses.add(commentBodyResponse);
        }
        postBodyResponse.setComments(commentBodyResponses);

        List<String> tags = new ArrayList<>();
        List<Tag2Post> tag2Post = tag2PostRepository.findAllByPostId(post.getId());
        for (Tag2Post currentTag2Post : tag2Post) {
            if(tagRepository.findById(currentTag2Post.getTagId()).isPresent()) {
                tags.add(tagRepository.findById(currentTag2Post.getTagId()).get().getName());
            }
        }
        postBodyResponse.setTags(tags);


        return postBodyResponse;
    }
    //====================================

    //GET /api/post/my
    public PostResponse getMyPosts(String offset, String limit, String status, Principal principal){
        Pageable pageable = PageRequest.of(Integer.parseInt(offset), Integer.parseInt(limit));
        String isActive = "1";
        String moderationStatus = "";

        if(status.equals("inactive")){
            isActive = "0";
        } else if(status.equals("pending")){
            moderationStatus = "NEW";
        } else if(status.equals("declined")) {
            moderationStatus = "DECLINED";
        } else if(status.equals("published")){
            moderationStatus = "ACCEPTED";
        }
        User user = userRepository.findOneByEmail(principal.getName());
        Page<Post> userPosts = postRepository.findAllByUserId(user.getId(), isActive, moderationStatus, pageable);
        List<PostBodyResponse> result = fillPostBodyResponseList(userPosts);

        return setPostResponseValue(result);
    }
    //====================================



    //методы для заполнения обьектов
    private PostBodyResponse fillPostBodyResponse(Post post){
        List<PostVote> postVotePage = post.getVotes(); // //Получаем список оценок(лайки/дизлайки) к текущему посту, без использования дополнительного репозитория
        int likeCount = 0;
        int dislikeCount = 0;
        for (PostVote postVote : postVotePage) {
            if(postVote.getValue() == 1){ //Если значение поля 1 - то это лайк, если -1, то дизлайк
                likeCount++;
            } else {
                dislikeCount++;
            }
        }
        PostBodyResponse postBodyResponse = new PostBodyResponse(); //Создаем обьект, который состоит из тех полей, которые front может обработать и инициализируем их все
        UserBodyResponse userBodyResponse = new UserBodyResponse(); //Одно из полей обьекта PostBodyResponse - это отдельный обьект с полями юзера
        userBodyResponse.setId(post.getUserId().getId());
        userBodyResponse.setName(post.getUserId().getName());
        postBodyResponse.setId(post.getId());
        postBodyResponse.setTimestamp(post.getTime().getTime()/1000);
        postBodyResponse.setUser(userBodyResponse);
        postBodyResponse.setTitle(post.getTitle());
        String announce = post.getText().length() >= 150 ? post.getText().substring(0, 150) + " ..." : post.getText();
        postBodyResponse.setAnnounce(removerTags(announce));
        postBodyResponse.setLikeCount(likeCount);
        postBodyResponse.setDislikeCount(dislikeCount);
        postBodyResponse.setCommentCount(post.getComments().size());
        postBodyResponse.setViewCount(post.getViewCount());
        return postBodyResponse;
    }

    private List<PostBodyResponse> fillPostBodyResponseList(Page<Post> posts){
        List<PostBodyResponse> postBodyResponseList = new ArrayList<>();
        for (Post post : posts) {
            PostBodyResponse postBodyResponse = fillPostBodyResponse(post);
            postBodyResponseList.add(postBodyResponse);
        }
        return postBodyResponseList;
    }

    private PostResponse setPostResponseValue(List<PostBodyResponse> postBodyResponses){
        PostResponse postResponse = new PostResponse();
        postResponse.setCount(postBodyResponses.size());
        postResponse.setPosts(postBodyResponses);
        return postResponse;
    }

    private String removerTags(String html) {
        return html.replaceAll("\\<(/?[^\\>]+)\\>", " ").replaceAll("\\s+", " ").trim();
    }
}
