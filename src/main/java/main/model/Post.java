package main.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @NonNull
    private int id;

    @Getter
    @Setter
    @NonNull
    @Column(name = "is_active")
    private byte isActive;

    @Getter
    @Setter
    @NonNull
    @Column(name = "moderation_status")
    @Enumerated(EnumType.STRING)
    private ModerationStatus moderationStatus;

    @Getter
    @Setter
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "moderator_id")
    private User moderatorId;

    @Getter
    @Setter
    @NonNull
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User userId;

    @Getter
    @Setter
    @NonNull
    private Date time;

    @Getter
    @Setter
    @NonNull
    private String title;

    @Getter
    @Setter
    @NonNull
    private String text;

    @Getter
    @Setter
    @NonNull
    @Column(name = "view_count")
    private int viewCount;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private List<PostComment> comments;

    @Getter
    @Setter
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private List<PostVote> votes;
}
