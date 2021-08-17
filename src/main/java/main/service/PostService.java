package main.service;

import main.model.*;
import main.model.response.PostResponse;
import main.model.response.PostBodyResponse;
import main.model.response.UserBodyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final PostVoteRepository postVoteRepository;

    public PostService(PostRepository postRepository, PostVoteRepository postVoteRepository) {
        this.postRepository = postRepository;
        this.postVoteRepository = postVoteRepository;
    }
    //Этап 2. Метод GET /api/post
    public PostResponse getPosts(String offset, String limit, String mode){
        List<PostBodyResponse> posts = new ArrayList<>(); //Список, который будет возвращаться в качестве ответа(PostResponse)

        Sort sort =  Sort.by(Sort.Direction.DESC, "time"); //Сортировка по умолчанию, по дате - от новых к старым;

        if(mode.equals("popular"))
            sort = Sort.by(Sort.Direction.DESC, "comments"); //Сортировка по кол-ву комментов
        else if(mode.equals("best"))
            sort = Sort.by(Sort.Direction.DESC, "votes"); //Сортировка по кол-ву лайков
        else if(mode.equals("early"))
            sort = Sort.by(Sort.Direction.ASC, "time"); //Сортировка по дате - от старых к новым


        Pageable sortedByRecent = PageRequest.of(Integer.parseInt(offset), Integer.parseInt(limit), sort); //
        Page<Post> postPage = postRepository.findAll(sortedByRecent); //Получаем все посты из репозитория(БД)
        for (Post post : postPage) {
            if(post.getIsActive() != 1 || post.getTime().compareTo(new Date()) != -1){ //если поле IsActive у поста не равно 1 или дата публикации позже текущей даты
                continue;                                                              //Добавить проверку на статус модерации. Убрал, т.к. в БД нету постов с нужным статусом
            }
                //List<PostVote> postVotePage = postVoteRepository.findAllByPostId(post.getId()); //Получаем список оценок(лайки/дизлайки) к текущему посту
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

            posts.add(postBodyResponse);
        }
        PostResponse postResponse = new PostResponse();

        postResponse.setCount(posts.size());
        postResponse.setPosts(posts);
        return postResponse;
    }
    //====================================

    //Этап 3. Метод GET /api/post/search
    public PostResponse getPostsSearch(String offset, String limit, String query){ // Как запрос query должен совпадать с постом?
        List<PostBodyResponse> postsResult = new ArrayList<>();
        Iterable<Post> postPage = postRepository.findAll();
        String[] queryArr = query.split(" "); //Разделяем переданный параметр query на отдельные слова по пробелу(ИЗМЕНИТЬ ЛОГИКУ РАЗБИЕНИЯ)

        for (Post post : postPage) {
            if(post.getIsActive() != 1 || post.getTime().compareTo(new Date()) != -1){ //если поле IsActive у поста не равно 1 или дата публикации позже текущей даты
                continue;                                                              //Добавить проверку на статус модерации. Убрал, т.к. в БД нету постов с нужным статусом
            }
            boolean isMatched = false;
            for (String s : post.getTitle().split(" ")) {
                for (String s1 : queryArr) {
                    if(s.equals(s1)){
                        isMatched = true;
                    }
                }
            }
            if(isMatched){
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

                int likeCount = 0;
                int dislikeCount = 0;
                for (PostVote vote : post.getVotes()) { //Считаем лайки и дизлайки поста
                    if(vote.getValue() == 1)
                        likeCount++;
                    else
                        dislikeCount++;
                }
                postBodyResponse.setLikeCount(likeCount);
                postBodyResponse.setDislikeCount(dislikeCount);
                postBodyResponse.setCommentCount(post.getComments().size());
                postBodyResponse.setViewCount(post.getViewCount());
                postsResult.add(postBodyResponse);
            }
        }
        if(postsResult.size() > 0){
            PostResponse postResponse = new PostResponse();
            postResponse.setCount(postsResult.size());
            postResponse.setPosts(postsResult);
            return postResponse;
        }
        else
           return getPosts(offset, limit, "recent");
    }
    //====================================

    private String removerTags(String html) {
        return html.replaceAll("\\<(/?[^\\>]+)\\>", " ").replaceAll("\\s+", " ").trim();
    }
}
