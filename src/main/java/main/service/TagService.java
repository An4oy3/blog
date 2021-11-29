package main.service;

import main.model.*;
import main.model.repositories.PostRepository;
import main.model.repositories.Tag2PostRepository;
import main.model.repositories.TagRepository;
import main.model.response.TagBodyResponse;
import main.model.response.TagResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
            resultList.add(TagBodyResponse.builder()
                    .name(currentTag.getName())
                    .weight(weightTag).build());
        }
        return TagResponse.builder()
                .tags(resultList).build();
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
