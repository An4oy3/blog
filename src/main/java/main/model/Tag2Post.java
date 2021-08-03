package main.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class Tag2Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NonNull
    private int id;

    @Getter
    @Setter
    @NonNull
    @Column(name = "post_id")
    private int postId;

    @Getter
    @Setter
    @NonNull
    @Column(name = "tag_id")
    private int tagId;

}
