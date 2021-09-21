package main.model.repositories;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    @Query(value = "select p from Post p WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < now()")
    Page<Post> findAllPost(Pageable pageable);

    @Query(value = "select p from Post p WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < now() AND (p.text LIKE %:quary% OR p.title LIKE %:quary%)")
    Page<Post> findAllPostByQuery(@Param("quary") String quary, Pageable pageable);

    @Query(value = "select time, count(*) from posts  WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < now() AND (time <= :yearEnd AND time >= :yearBegin) group by time", nativeQuery = true)
    List findAllPostsByYear(@Param("yearBegin") LocalDate yearBegin, @Param("yearEnd") LocalDate yearEnd);

    @Query(value = "select distinct year(posts.time) from posts", nativeQuery = true)
    List<Integer> findAllYears();

    @Query(value = "select * from posts WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time = :date", nativeQuery = true)
    Page<Post> findAllByTime(@Param("date") String date, Pageable pageable);

    @Query(value = "select p from Post p WHERE time <= now() AND id = :id")
    Post findPostById(@Param("id")Integer id);

    @Query(value = "select p from Post p WHERE is_active = :isActive AND moderation_status = :moderationStatus AND user_id = :id")
    Page<Post> findAllByUserId(@Param("id")Integer userId, @Param("isActive")String isActive, @Param(value = "moderationStatus") String moderationStatus, Pageable pageable);

    @Query(value = "select p from Post p WHERE is_active = :isActive AND user_id = :id")
    Page<Post> findAllByUserId(@Param("id")Integer userId, @Param("isActive")String isActive, Pageable pageable);


    @Modifying
    @Query(value = "insert into posts(is_active, moderation_status, text, title, time, view_count, user_id) " +
                            "values (:is_active, :moderation_status, :text, :title, :time, 0, :user_id)", nativeQuery = true)
    void postAdd(@Param("is_active")Integer isActive,
                 @Param("moderation_status")String moderationStatus,
                 @Param("text")String text,
                 @Param("title")String title,
                 @Param("time")Date time,
                 @Param("user_id")Integer userId);

    @Query(value = "select p from Post p WHERE moderation_status = :moderation_status")
    Page<Post> findAllForModerate(@Param("moderation_status")String moderationStatus, Pageable pageable);
}
