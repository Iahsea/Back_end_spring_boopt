package com.project.shopapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.shopapp.models.UserAvatar;

public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long> {

    List<UserAvatar> findByUserId(Long userId);

}
