package main.service;

import main.model.*;
import main.model.repositories.*;
import main.model.request.PostAddRequest;
import main.model.request.PostVoteRequest;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final Tag2PostRepository tag2PostRepository;
    private final UserRepository userRepository;
    private final PostVoteRepository postVoteRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository, Tag2PostRepository tag2PostRepository, UserRepository userRepository, PostVoteRepository postVoteRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
        this.tag2PostRepository = tag2PostRepository;
        this.userRepository = userRepository;
        this.postVoteRepository = postVoteRepository;
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
        return PostResponse.builder()
                .count(posts.size())
                .posts(posts).build();
    }

    //GET /api/post/byTag
    public PostResponse getPostsByTag(String offset, String limit, String tagName){
        List<PostBodyResponse> posts = new ArrayList<>();
        Tag tag = tagRepository.findOneByName(tagName);
            List<Post> postsFromTag = tag.getPostList(); // Список постов по переданному тегу
            for (Post post : postsFromTag) {
                posts.add(fillPostBodyResponse(post));
            }
        return PostResponse.builder()
                .count(posts.size())
                .posts(posts).build();
    }
    //====================================

    //GET /api/post/search
    public PostResponse getPostsSearch(String offset, String limit, String query){
        Pageable pageable = PageRequest.of(Integer.parseInt(offset), Integer.parseInt(limit));
        Page<Post> posts = postRepository.findAllPostByQuery(query, pageable);

        List<PostBodyResponse> postsResult = fillPostBodyResponseList(posts);

        return PostResponse.builder()
                .count(postsResult.size())
                .posts(postsResult).build();
    }
    //====================================

    //GET /api/calendar
    public CalendarResponse getPostsByYear(String year){
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
            Object[] obj = (Object[]) o;
            try {
                String date = obj[0].toString().substring(0, obj[0].toString().indexOf(" "));
                Integer postsByDate = Integer.parseInt(obj[1].toString());
                posts.put(date, postsByDate);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return CalendarResponse.builder()
                .posts(posts)
                .years(allYears)
                .build();
    }
    //====================================

    //Метод GET /api/post/byDate
    public PostResponse getPostsByDate(String offset, String limit, String date){
        Pageable pageable = PageRequest.of(Integer.parseInt(offset), Integer.parseInt(limit));
        Page<Post> resultList = postRepository.findAllByTime(date, pageable);
        List<PostBodyResponse> posts = fillPostBodyResponseList(resultList);

        return PostResponse.builder()
                .count(posts.size())
                .posts(posts).build();
    }
    //====================================

    //GET /api/post/{ID}
    public PostBodyResponse getPostById(@PathVariable String id){
        if(id.isEmpty() || Integer.parseInt(id) < 0 || Integer.parseInt(id) > postRepository.count()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Post post = postRepository.findPostById(Integer.parseInt(id)); //Условия на активность поста и статус модерации в методе репозитория не дают получить и ообразить свои посты.
        PostBodyResponse postBodyResponse = fillPostBodyResponse(post);//УБРАЛ ЭТИ УСЛОВИЯ "is_active = 1 AND moderation_status = 'ACCEPTED' AND"

        postBodyResponse.setActive(post.getIsActive() == 1);
        postBodyResponse.setText(post.getText());

        List<CommentBodyResponse> commentBodyResponses = new ArrayList<>();
        for (PostComment comment : post.getComments()) {
            commentBodyResponses.add(CommentBodyResponse.builder()
                    .id(comment.getId())
                    .text(comment.getText())
                    .timestamp(comment.getTime().getTime() / 1000)
                    .user(LoginResponse.UserBodyResponse.builder()
                            .id(comment.getUser().getId())
                            .name(comment.getUser().getName())
                            .photo(comment.getUser().getPhoto()).build())
                    .build());
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

        switch (status) {
            case "inactive":
                isActive = "0";
                break;
            case "pending":
                moderationStatus = "NEW";
                break;
            case "declined":
                moderationStatus = "DECLINED";
                break;
            case "published":
                moderationStatus = "ACCEPTED";
                break;
        }
        User user = userRepository.findOneByEmail(principal.getName());

        Page<Post> userPosts = moderationStatus.isEmpty() ? postRepository.findAllByUserId(user.getId(), isActive, pageable) : postRepository.findAllByUserId(user.getId(), isActive, moderationStatus, pageable);
        List<PostBodyResponse> result = fillPostBodyResponseList(userPosts);

        return PostResponse.builder()
                .count(result.size())
                .posts(result).build();
    }
    //====================================

    //POST api/post
    public ContentAddResponse postAdd(PostAddRequest request, Principal principal){
        if(request.getText().length() < 50){
            return ContentAddResponse.builder()
                    .errors(ContentAddErrors.builder()
                            .text("Текст публикации слишком короткий").build())
                    .build();
        }
        if(request.getTitle().length() < 3){
            return ContentAddResponse.builder()
                    .errors(ContentAddErrors.builder()
                            .title("Заголовок не установлен").build())
                    .build();
        }
        Post post = new Post();
        post.setIsActive((byte) request.getActive());
        post.setModerationStatus(ModerationStatus.NEW);
        post.setText(request.getText());
        post.setTitle(request.getTitle());
        post.setTime(request.getTimestamp().before(new Date()) ? new Date() : request.getTimestamp());
        post.setUser(userRepository.findOneByEmail(principal.getName()));

        post = postRepository.save(post);

        createTag(request, post);
        return ContentAddResponse.builder().result(true).build();
    }

    //====================================

    //PUT api/post/{id}
    public ContentAddResponse postPut(PostAddRequest request, String id, Principal principal){
        if(request.getText().length() < 50){
            return ContentAddResponse.builder()
                    .errors(ContentAddErrors.builder()
                            .text("Текст публикации слишком короткий").build())
                    .build();
        }
        if(request.getTitle().length() < 3){
            return ContentAddResponse.builder()
                    .errors(ContentAddErrors.builder()
                            .title("Заголовок не установлен").build())
                    .build();
        }
        if(id.isEmpty() || Integer.parseInt(id) < 0 || Integer.parseInt(id) > postRepository.count()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Post post = postRepository.findPostById(Integer.parseInt(id));
        post.setIsActive((byte) request.getActive());
        post.setText(request.getText());
        post.setTitle(request.getTitle());
        post.setTime(request.getTimestamp().before(new Date()) ? new Date() : request.getTimestamp());

        if(userRepository.findOneByEmail(principal.getName()).getIsModerator() == 0){
            post.setModerationStatus(ModerationStatus.NEW);
        }

        createTag(request, post);
        postRepository.save(post);
        return ContentAddResponse.builder().result(true).build();
    }
    //====================================

    //GET api/post/moderation
    public PostResponse getPostForModerate(String offset, String limit, String status){
        Pageable pageable = PageRequest.of(Integer.parseInt(offset), Integer.parseInt(limit));
        String moderationStatus = status.toUpperCase(Locale.ROOT);

        Page<Post> posts = postRepository.findAllForModerate(moderationStatus, pageable);
        List<PostBodyResponse> result = fillPostBodyResponseList(posts);

        return PostResponse.builder()
                .count(result.size())
                .posts(result).build();
    }

    //POST api/post/like
    public PostVoteResponse like(PostVoteRequest request, Principal principal) {
        Post post = postRepository.findById(request.getPostId()).orElse(null);
        if(post == null){
            return PostVoteResponse.builder().result(false).build();
        }
        User currentUser = userRepository.findOneByEmail(principal.getName());
        PostVote postVote = postVoteRepository.findByPostAndAndUser(post, currentUser).orElse(null);
        if(postVote == null){
            postVote = new PostVote();
            postVote.setPost(post);
            postVote.setTime(new Date());
            postVote.setUser(currentUser);
            postVote.setValue((byte) 1);
            return PostVoteResponse.builder().result(true).build();
        } else if(postVote.getValue() == 1){
            return PostVoteResponse.builder().result(false).build();
        } else {
            postVote.setValue((byte) 1);
            postVoteRepository.save(postVote);
            return PostVoteResponse.builder().result(true).build();
        }
    }

    public PostVoteResponse dislike(PostVoteRequest request, Principal principal) {
        Post post = postRepository.findById(request.getPostId()).orElse(null);
        if(post == null){
            return PostVoteResponse.builder().result(false).build();
        }
        User currentUser = userRepository.findOneByEmail(principal.getName());
        PostVote postVote = postVoteRepository.findByPostAndAndUser(post, currentUser).orElse(null);

        if(postVote == null){
            postVote = new PostVote();
            postVote.setPost(post);
            postVote.setTime(new Date());
            postVote.setUser(currentUser);
            postVote.setValue((byte) -1);
            return PostVoteResponse.builder().result(true).build();
        } else if(postVote.getValue() == -1){
            return PostVoteResponse.builder().result(false).build();
        } else {
            postVote.setValue((byte) -1);
            postVoteRepository.save(postVote);
            return PostVoteResponse.builder().result(true).build();
        }
    }



    //методы для заполнения обьектов
    private void createTag(PostAddRequest request, Post post) {
        for (String tagName : request.getTags()) {
            Tag tag = tagRepository.findOneByName(tagName);
            if(tag == null){
                tag = new Tag();
                tag.setName(tagName);
                tag.setPostList(List.of(post));
                tagRepository.save(tag);
            } else {
                List<Post> postList = tag.getPostList();
                if(!postList.contains(post)) {
                    postList.add(post);
                    tag.setPostList(postList);
                    tagRepository.save(tag);
                }
            }
        }
    }


    private PostBodyResponse fillPostBodyResponse(Post post){
        List<PostVote> postVotePage = post.getVotes(); // //Получаем список оценок(лайки/дизлайки) к текущему посту, без использования дополнительного репозитория
        int likeCount = 0;
        int dislikeCount = 0;
        for (PostVote postVote : postVotePage) {
            if(postVote.getValue() == 1)
                likeCount++;//Если значение поля 1 - то это лайк, если -1, то дизлайк
             else
                dislikeCount++;
        }
        return PostBodyResponse.builder()
                .user(LoginResponse.UserBodyResponse.builder()
                        .id(post.getUser().getId())
                        .name(post.getUser().getName())
                        .build())
                .id(post.getId())
                .timestamp(post.getTime().getTime()/1000)
                .title(post.getTitle())
                .announce(removerTags(post.getText().length() >= 150 ? post.getText().substring(0, 150) + " ..." : post.getText()))
                .likeCount(likeCount)
                .dislikeCount(dislikeCount)
                .commentCount(post.getComments().size())
                .viewCount(post.getViewCount())
                .active(post.getIsActive() == 1)
                .text(post.getText()).build();
    }

    private List<PostBodyResponse> fillPostBodyResponseList(Page<Post> posts){
        List<PostBodyResponse> postBodyResponseList = new ArrayList<>();
        for (Post post : posts) {
            postBodyResponseList.add(fillPostBodyResponse(post));
        }
        return postBodyResponseList;
    }

    private String removerTags(String html) {
        return html.replaceAll("\\<(/?[^\\>]+)\\>", " ").replaceAll("\\s+", " ").trim();
    }
}
