package main.service;

import main.model.*;
import main.model.response.CalendarResponse;
import main.model.response.PostResponse;
import main.model.response.PostBodyResponse;
import main.model.response.UserBodyResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
    }

    //Этап 2. Метод GET /api/post
    public PostResponse getPosts(String offset, String limit, String mode){
        List<PostBodyResponse> posts = new ArrayList<>();
        Sort sort =  Sort.by(Sort.Direction.DESC, "time"); //Сортировка по умолчанию, по дате - от новых к старым;

        if(mode.equals("popular"))
            sort = Sort.by("comments.size").descending();
        else if(mode.equals("best"))
            sort = Sort.by("votes.size").descending();
        else if(mode.equals("early"))
            sort = Sort.by(Sort.Direction.ASC, "time");

        Pageable pageable = PageRequest.of(Integer.parseInt(offset), Integer.parseInt(limit), sort);
        Page<Post> list = postRepository.findAllPost(pageable);
        for (Post post : list) {
            PostBodyResponse postBodyResponse = fillPostBodyResponse(post);
            posts.add(postBodyResponse);
        }
        PostResponse postResponse = new PostResponse();

        postResponse.setCount(posts.size());
        postResponse.setPosts(posts);
        return postResponse;
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
        PostResponse postResponse = new PostResponse();
        postResponse.setCount(posts.size());
        postResponse.setPosts(posts);
        return postResponse;
    }
    //====================================

    //GET /api/post/search
    public PostResponse getPostsSearch(String offset, String limit, String query){
        List<PostBodyResponse> postsResult = new ArrayList<>();

        Pageable pageable = PageRequest.of(Integer.parseInt(offset), Integer.parseInt(limit));
        Page<Post> posts = postRepository.findAllPostByQuery(query, pageable);
        for (Post post : posts) {
            PostBodyResponse postBodyResponse = fillPostBodyResponse(post);
            postsResult.add(postBodyResponse);
        }

        PostResponse postResponse = new PostResponse();
        postResponse.setPosts(postsResult);
        postResponse.setCount(postsResult.size());
        return postResponse;
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
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        String yearBegin = year + "-01-01 00:00:00";
        String yearEnd = year + "-12-31 23:59:59";
        Date begin = null;
        Date end = null;
        try {
            begin = df.parse(yearBegin);
            end = df.parse(yearEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List postsByYear = postRepository.findAllPostsByYear(begin, end);
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
        List<PostBodyResponse> posts = new ArrayList<>();

        Pageable pageable = PageRequest.of(Integer.parseInt(offset), Integer.parseInt(limit));
        Page<Post> resultList = postRepository.findAllByTime(date, pageable);
        for (Post post : resultList) {
            PostBodyResponse postBodyResponse = fillPostBodyResponse(post);
            posts.add(postBodyResponse);
        }

        PostResponse postResponse = new PostResponse();
        postResponse.setCount(posts.size());
        postResponse.setPosts(posts);
        return postResponse;
    }
    //====================================

    //Метод для заполнения обьекта PostBodyResponse.
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

    private String removerTags(String html) {
        return html.replaceAll("\\<(/?[^\\>]+)\\>", " ").replaceAll("\\s+", " ").trim();
    }
}
