package com.belova.entity.repository;

import com.belova.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Override
    User findOne(Long aLong);
    User findByLogin (String login);
}
