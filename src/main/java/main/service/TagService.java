package main.service;

import main.model.*;
import main.model.response.TagBodyResponse;
import main.model.response.TagResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TagService {
    private final TagRepository tagRepository;
    private final Tag2PostRepository tag2PostRepository;
    private final PostRepository postRepository;

    public TagService(TagRepository tagRepository, Tag2PostRepository tag2PostRepository, PostRepository postRepository) {
        this.tagRepository = tagRepository;
        this.tag2PostRepository = tag2PostRepository;
        this.postRepository = postRepository;
    }


    public TagResponse getTags(String query){
        List<TagBodyResponse> resultList = new ArrayList<>();//Список, который будем возвращать(Внутри обьекты с полями, которые запрашивает клиент(name, weight)
        Iterable<Tag> allTags = tagRepository.findAll();
        List<Tag> tagList = new ArrayList<>(); //Список всех тегов из БД
        for (Tag allTag : allTags) {
            tagList.add(allTag);
        }
        Tag mostPopularTag = getMostPopularTag(tagList); //Находим наиболее популярный тэг
        double dWeightMax = (double) tag2PostRepository.findAllByTagId(mostPopularTag.getId()).size() / postRepository.count();//Ненормированный вес самого популярного тега(кол-во постов с данным тегом делим на общее кол-во постов)
        double k = 1/dWeightMax; //Коэффициент для нормализации

        for (Tag currentTag : tagList) {
            double dWeightTag = (double) tag2PostRepository.findAllByTagId(currentTag.getId()).size()/postRepository.count();//Нормированный вес текущего тега(кол-во постов с данным тегом делим на общее кол-во постов)
            double weightTag = dWeightTag * k; //Получаем нормированый вес текущего тега

            TagBodyResponse tagBodyResponse = new TagBodyResponse();
            tagBodyResponse.setName(currentTag.getName());
            tagBodyResponse.setWeight(weightTag);
            resultList.add(tagBodyResponse);
        }

        TagResponse tagResponse = new TagResponse();
        tagResponse.setTags(resultList);
        return tagResponse;
    }
    private Tag getMostPopularTag(List<Tag> tags){
        if(tags.size() <= 0){
            return null;
        }
        int max = 0;
        Tag popularTag = null;
        for (Tag tag : tags) {
            List<Tag2Post> tag2Posts = tag2PostRepository.findAllByTagId(tag.getId());
            if(tag2Posts.size() > max){
                max = tag2Posts.size();
                popularTag = tag;
            }
        }
        return popularTag;
    }

}
