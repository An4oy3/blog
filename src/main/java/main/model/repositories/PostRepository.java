package main.model.repositories;

import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;


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

    @Query(value = "select p from Post p WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time <= now() AND id = :id")
    Post findPostById(@Param("id")Integer id);

    @Query(value = "select p from Post p WHERE is_active = :isActive AND moderation_status = :moderationStatus AND user_id = :id")
    Page<Post> findAllByUserId(@Param("id")Integer userId, @Param("isActive")String isActive, @Param(value = "moderationStatus") String moderationStatus, Pageable pageable);
}
