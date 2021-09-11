package main.model.repositories;

import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "select u FROM User u WHERE email = :email")
    User findOneByEmail(@Param("email") String email);

    @Modifying
    @Query(value = "INSERT INTO users(email, name, password, reg_time, is_moderator) values (:email, :name, :password, now(), 0)", nativeQuery = true)
    int addUser(@Param("email") String email, @Param("name") String name, @Param("password") String password);
}
