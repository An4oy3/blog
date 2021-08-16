package main.model;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Tag2PostRepository extends PagingAndSortingRepository<Tag2Post, Integer> {
    List<Tag2Post> findAllByTagId(int TagId);
}