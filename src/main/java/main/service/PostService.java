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

    public PostResponse getPosts(String offset, String limit, String mode){
        List<PostBodyResponse> posts = new ArrayList<>(); //Список, который будет возвращаться в качестве ответа(PostResponse)

        Sort sort =  Sort.by(Sort.Direction.ASC, "time"); //Сортировка по умолчанию, по дате - от новых к старым;

        if(mode.equals("popular"))
            sort = Sort.by(Sort.Direction.DESC, "comments"); //Сортировка по кол-ву комментов
        else if(mode.equals("best"))
            sort = Sort.by(Sort.Direction.DESC, "votes"); //Как сортировать по лайкам, если такого поля у класса Пост нету??
        else if(mode.equals("early"))
            sort = Sort.by(Sort.Direction.DESC, "time"); //Сортировка по дате - от старых к новым


        Pageable sortedByRecent = PageRequest.of(Integer.parseInt(offset), Integer.parseInt(limit), sort); //
        Page<Post> postPage = postRepository.findAll(sortedByRecent); //Получаем все посты из репозитория(БД)
        for (Post post : postPage) {
            if(post.getIsActive() != 1 || post.getTime().compareTo(new Date()) != -1){ //если поле IsActive у поста не равно 1 или дата публикации позже текущей даты
                continue;                                                              //Добавить проверку на статус модерации. Убрал, т.к. в БД нету постов с нужным статусом
            }
                List<PostVote> postVotePage = postVoteRepository.findAllByPostId(post.getId()); //Получаем список оценок(лайки/дизлайки) к текущему посту
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

    private String removerTags(String html)
    {
        return html.replaceAll("\\<(/?[^\\>]+)\\>", " ").replaceAll("\\s+", " ").trim();
    }
}
