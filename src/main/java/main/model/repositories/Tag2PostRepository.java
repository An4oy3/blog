package main.model.repositories;

import main.model.Tag2Post;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Tag2PostRepository extends PagingAndSortingRepository<Tag2Post, Integer> {
    List<Tag2Post> findAllByTagId(int tagId);

    List<Tag2Post> findAllByPostId(int postId);
}
