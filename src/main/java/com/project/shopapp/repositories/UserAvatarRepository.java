package com.project.shopapp.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.shopapp.models.UserAvatar;

public interface UserAvatarRepository extends JpaRepository<UserAvatar, Long> {

    Optional<List<UserAvatar>> findByUserId(Long userId);

}
