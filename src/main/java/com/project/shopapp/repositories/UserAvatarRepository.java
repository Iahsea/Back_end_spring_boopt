package com.project.shopapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.shopapp.models.UserAvatar;

public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long> {

}
