package main.model;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostVoteRepository extends PagingAndSortingRepository<PostVote, Integer> {
    List<PostVote> findAllByPostId(int PostId);
}
