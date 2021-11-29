package main.model.repositories;

import main.model.Post;
import main.model.PostVote;
import main.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostVoteRepository extends PagingAndSortingRepository<PostVote, Integer> {
    List<PostVote> findAllByPostId(int PostId);
    Optional<PostVote> findByPostAndAndUser(Post post, User user);
}
