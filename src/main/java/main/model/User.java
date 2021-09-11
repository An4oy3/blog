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

    public Role getRole(){
        return isModerator == 1 ? Role.MODERATOR : Role.USER;
    }


}
