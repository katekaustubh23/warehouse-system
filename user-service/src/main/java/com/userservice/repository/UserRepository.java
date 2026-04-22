package com.userservice.repository;

import com.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String username);

    Optional<User> findFirstBy();

    @Query(value="SELECT * FROM user_schema.users WHERE email= :username OR username= :username",nativeQuery = true)
    Optional<User> findByEmailOrUsername(@Param("username") String username);
}
