package main.service;

import main.model.Post;
import main.model.repositories.PostRepository;
import main.model.repositories.TagRepository;
import main.model.repositories.UserRepository;
import main.model.request.PostAddRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Principal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "application.yaml")
@AutoConfigureMockMvc
public class PostServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PostRepository postRepository;
    @MockBean
    private TagRepository tagRepository;
    @Mock
    private Principal principal;

    @Autowired
    private PostService postService;

    @Test
    public void getPosts() {
    }

    @Test
    public void getPostsByTag() {
    }

    @Test
    public void getPostsSearch() {
    }

    @Test
    public void getPostsByYear() {
    }

    @Test
    public void getPostsByDate() {
    }

    @Test
    public void getPostById() {
    }

    @Test
    public void getMyPosts() {
    }

    @Test
    public void postAdd() {
        PostAddRequest request = new PostAddRequest(new Date(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)), 1, "testTitle",
                List.of("testTag1", "testTag2"), "testText");
        postService.postAdd(request,principal);
        //Assert.assertNotNull();
    }

    @Test
    public void postPut() {
    }

    @Test
    public void getPostForModerate() {
    }
}