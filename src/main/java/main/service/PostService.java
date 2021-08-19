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
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    //private final PostVoteRepository postVoteRepository;

    public PostService(PostRepository postRepository, TagRepository tagRepository) {
        this.postRepository = postRepository;
        this.tagRepository = tagRepository;
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
            PostBodyResponse postBodyResponse = fillPostBodyResponse(post);
//                //List<PostVote> postVotePage = postVoteRepository.findAllByPostId(post.getId()); //Получаем список оценок(лайки/дизлайки) к текущему посту
//                List<PostVote> postVotePage = post.getVotes(); // //Получаем список оценок(лайки/дизлайки) к текущему посту, без использования дополнительного репозитория
//                int likeCount = 0;
//                int dislikeCount = 0;
//                for (PostVote postVote : postVotePage) {
//                    if(postVote.getValue() == 1){ //Если значение поля 1 - то это лайк, если -1, то дизлайк
//                        likeCount++;
//                    } else {
//                        dislikeCount++;
//                    }
//                }
//            PostBodyResponse postBodyResponse = new PostBodyResponse(); //Создаем обьект, который состоит из тех полей, которые front может обработать и инициализируем их все
//            UserBodyResponse userBodyResponse = new UserBodyResponse(); //Одно из полей обьекта PostBodyResponse - это отдельный обьект с полями юзера
//                userBodyResponse.setId(post.getUserId().getId());
//                userBodyResponse.setName(post.getUserId().getName());
//            postBodyResponse.setId(post.getId());
//            postBodyResponse.setTimestamp(post.getTime().getTime()/1000);
//            postBodyResponse.setUser(userBodyResponse);
//            postBodyResponse.setTitle(post.getTitle());
//            String announce = post.getText().length() >= 150 ? post.getText().substring(0, 150) + " ..." : post.getText();
//            postBodyResponse.setAnnounce(removerTags(announce));
//            postBodyResponse.setLikeCount(likeCount);
//            postBodyResponse.setDislikeCount(dislikeCount);
//            postBodyResponse.setCommentCount(post.getComments().size());
//            postBodyResponse.setViewCount(post.getViewCount());
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
                    if(s.equalsIgnoreCase(s1)){
                        isMatched = true;
                    }
                }
            }
            if(isMatched){
//                PostBodyResponse postBodyResponse = new PostBodyResponse(); //Создаем обьект, который состоит из тех полей, которые front может обработать и инициализируем их все
//                UserBodyResponse userBodyResponse = new UserBodyResponse(); //Одно из полей обьекта PostBodyResponse - это отдельный обьект с полями юзера
//                userBodyResponse.setId(post.getUserId().getId());
//                userBodyResponse.setName(post.getUserId().getName());
//                postBodyResponse.setId(post.getId());
//                postBodyResponse.setTimestamp(post.getTime().getTime()/1000);
//                postBodyResponse.setUser(userBodyResponse);
//                postBodyResponse.setTitle(post.getTitle());
//                String announce = post.getText().length() >= 150 ? post.getText().substring(0, 150) + " ..." : post.getText();
//                postBodyResponse.setAnnounce(removerTags(announce));
//
//                int likeCount = 0;
//                int dislikeCount = 0;
//                for (PostVote vote : post.getVotes()) { //Считаем лайки и дизлайки поста
//                    if(vote.getValue() == 1)
//                        likeCount++;
//                    else
//                        dislikeCount++;
//                }
//                postBodyResponse.setLikeCount(likeCount);
//                postBodyResponse.setDislikeCount(dislikeCount);
//                postBodyResponse.setCommentCount(post.getComments().size());
//                postBodyResponse.setViewCount(post.getViewCount());
                PostBodyResponse postBodyResponse = fillPostBodyResponse(post);
                postsResult.add(postBodyResponse);
            }
        }
        if(postsResult.size() > 0){
            PostResponse postResponse = new PostResponse();
            postResponse.setCount(postsResult.size());
            postResponse.setPosts(postsResult);
            return postResponse;
        }
        else {
            return getPosts(offset, limit, "recent");
        }
    }
    //====================================

    //Этап 3. Метод GET /api/calendar
    public CalendarResponse getPostsByYear(String year){
        CalendarResponse calendarResponse = new CalendarResponse();
        //Проверяем корректность переданного параметра year
        try {
            int y = Integer.parseInt(year);
        } catch (NumberFormatException e){
           Calendar date = Calendar.getInstance();
           int y = date.get(Calendar.YEAR);
           year = String.valueOf(y); //если строка пустая или некорректная, то задаем текущий год.
        }

        //Открываем сессию, для передачи HQL-запроса
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
        SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
        Session session = sessionFactory.openSession();

        //SQL запрос для получения списка годов, за которые была хотя бы одна публикация
        String queryYears = "select distinct year(posts.time) from posts";
        List years = session.createSQLQuery(queryYears).list(); //Список всех годов, за которые делались публикации
        calendarResponse.setYears(years);

        Map<String, Integer> posts = new TreeMap<>(); // Мапа, которая будет добавлена в CalendarResponse. Параметр posts;

        //Запрос на получение списка дат, в которые были опублекованы посты + группировка по этим датам.
        String queryPostCount = "select time, count(*) FROM " + Post.class.getSimpleName() + " WHERE time > '" + year + "-01-01' AND time < '" + year + "-12-31' group by time";
        //результат запроса.Таблица, где одна колонка список всех дат за указанный год, а вторая колонка кол-во постов размещенных в соответсвтвующую дату
        List postCount = session.createQuery(queryPostCount).list();
        for (int i = 0; i < postCount.size(); i++) {
            //Одна колонка из списка результатов(Первый элемент массива - Дата, когда был опубликован пост. Второй элемент - кол-во постов, опубликованных в эту дату)
            Object res[] = (Object[]) postCount.get(i);
            String res1 = res[0].toString().substring(0, res[0].toString().indexOf(" ")); //Обрезаем дату к формату yyyy-mm-dd
            posts.put(res1, Integer.parseInt(res[1].toString()));
        }

        calendarResponse.setPosts(posts);
        sessionFactory.close();
        return calendarResponse;
    }
    //====================================

    //Этап 3. Метод GET /api/post/byDate
    public PostResponse getPostsByDate(String offset, String limit, String date){
        List<PostBodyResponse> posts = new ArrayList<>(); //Список, который будет возвращаться в качестве ответа(PostResponse)

        //Преобразовываем переданную дату в формат, в котором она хранится в базе данных.
        Timestamp timestamp = Timestamp.valueOf(date + " 00:00:00");
        Pageable pageable = PageRequest.of(Integer.parseInt(offset), Integer.parseInt(limit));
        Page<Post> resultList = postRepository.findAllByTime(timestamp, pageable); //****УТОЧНИТЬ КАК СДЕЛАТЬ, ЧТОБЫ МЕТОД ВОЗВРАЩАЛ ПОСТЫ СРАВНИВАЯ ТОЛЬКО ДАТУ, БЕЗ УЧЕТА ВРЕМЕНИ(HH:MM:SS)

        for (Post post : resultList) {
            if(post.getIsActive() != 1 || post.getTime().compareTo(new Date()) != -1){ //если поле IsActive у поста не равно 1 или дата публикации позже текущей даты
                continue;                                                              //Добавить проверку на статус модерации. Убрал, т.к. в БД нету постов с нужным статусом
            }
            PostBodyResponse postBodyResponse = fillPostBodyResponse(post);
            posts.add(postBodyResponse);
        }
        PostResponse postResponse = new PostResponse();
        postResponse.setCount(posts.size());
        postResponse.setPosts(posts);
        return postResponse;
    }
    //====================================

    //Этап 3. Метод GET /api/post/byTag
    public PostResponse getPostsByTag(String offset, String limit, String tagName){
        List<PostBodyResponse> posts = new ArrayList<>(); //Список, который будет возвращаться в качестве ответа(PostResponse)
        List<Tag> tagList = tagRepository.findAllByName(tagName); //Тег, который был передан в кач-ве параметра
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


    //Метод для заполнения обьекта PostBodyResponse. Нужные поля обьекта post копируются в обьект postBodyResponse
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
