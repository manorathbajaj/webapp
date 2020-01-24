package com.manorath.csye6225.repository;

import com.manorath.csye6225.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("select t from  User t where t.email = ?1")
    User findUserByEmail(String email);
}
