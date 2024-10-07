package com.example.dream_shop.service.user;

import com.example.dream_shop.dto.UserDto;
import com.example.dream_shop.model.User;
import com.example.dream_shop.request.CreateUserRequest;
import com.example.dream_shop.request.UserUpdateRequest;

public interface IUserService {

    User getUserByID(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UserUpdateRequest request, Long userId);
    void deleteUser(Long userId);

    UserDto convertUserToDto(User user);
}
