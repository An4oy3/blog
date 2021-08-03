package main.service;

import main.model.Post;
import main.model.request.PostRequest;
import main.model.response.PostResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PostService {

    public PostResponse getPosts(){
        PostResponse postResponse = new PostResponse();
        postResponse.setCount(0);
        postResponse.setPosts(new ArrayList<>());
        return postResponse;
    }
}
