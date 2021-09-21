package main.model.repositories;

import main.model.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentRepository extends JpaRepository<PostComment, Integer> {
    @Query("select p from PostComment p WHERE id = :id")
    PostComment findOneById(@Param("id") Integer id);

}
