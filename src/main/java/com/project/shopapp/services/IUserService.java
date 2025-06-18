package com.project.shopapp.services;

import com.project.shopapp.dtos.UpdateUserDTO;
import com.project.shopapp.dtos.UserAvatarDTO;
import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.models.User;
import com.project.shopapp.models.UserAvatar;

public interface IUserService {
    User createUser(UserDTO userDTO) throws Exception;

    String login(String phoneNumber, String password, Long roleId) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User getUserDetailsFromRefreshToken(String token) throws Exception;

    User updateUser(Long userId, UpdateUserDTO updatedUserDTO) throws Exception;
    // UserAvatar createUserAvatar(Long id, UserAvatarDTO userAvatarDTO);
}
