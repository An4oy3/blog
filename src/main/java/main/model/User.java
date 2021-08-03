package main.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @NonNull
    private int id;

    @Getter
    @Setter
    @NonNull
    @Column(name = "is_moderator")
    private byte isModerator;

    @Getter
    @Setter
    @NonNull
    @Column(name = "reg_time")
    private Date regTime;

    @Getter
    @Setter
    @NonNull
    private String name;

    @Getter
    @Setter
    @NonNull
    private String email;

    @Getter
    @Setter
    @NonNull
    private String password;

    @Getter
    @Setter
    private String code;

    @Getter
    @Setter
    private String photo;
    //drop table captcha_codes,global_settings,post_comments,post_votes,posts,tag2post,tags,users;
    //insert into users('id', 'is_moderator', 'reg_time', 'name', 'email', 'password') VALUES('1', '1', '2021-07-24', 'Федор', 'fedor@gmail.com', '123');

//    insert into users('id','is_moderator', 'reg_time', 'name', 'email', 'password') VALUES('2', '0', '20210724', 'Петр', 'petya@gmail.com', '321');
//    insert into posts('is_active', 'moderation_status', 'moderator_id', 'user_id', 'time', 'title', 'text', 'view_count') VALUES('1', 'NEW', '1', '1', '202107242207', 'Заголовок первого поста', 'Текст первого поста', '0');
//    insert into post_comments('post_id', 'user_id', 'time', 'text') VALUES('1', '2', '202107242227', 'Первый комментарий');
//    insert into tags('name') VALUES('Первый');
//    insert into tags('name') VALUES('First post');
//    insert into tag2post('post_id', 'tag_id') VALUES('1', '1');
//    insert into tag2post('post_id', 'tag_id') VALUES('1', '2');

}
