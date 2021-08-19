package main.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;


@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Integer> {
    Page<Post> findAllByTime(Timestamp time, Pageable pageable);
}
