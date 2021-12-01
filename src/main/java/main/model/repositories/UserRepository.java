package main.model.repositories;

import main.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query(value = "select u FROM User u WHERE u.email = :email")
    User findOneByEmail(@Param("email") String email);

    Optional<User> findByCode(String code);

    @Modifying
    @Query(value = "update User u set u.photo = null where u.id = :userId")
    void deletePhoto(@Param("userId") Integer userId);

    @Modifying
    @Query(value = "INSERT INTO users(email, name, password, reg_time, is_moderator) values (:email, :name, :password, now(), 0)", nativeQuery = true)
    int addUser(@Param("email") String email, @Param("name") String name, @Param("password") String password);
}
