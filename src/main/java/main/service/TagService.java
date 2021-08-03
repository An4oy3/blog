package main.service;

import main.model.response.TagResponse;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class TagService {
    public TagResponse getTags(){
        TagResponse tagResponse = new TagResponse();
        tagResponse.setTags(new HashMap<>());
        return tagResponse;
    }
}
