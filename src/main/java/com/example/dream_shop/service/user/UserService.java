package com.example.dream_shop.service.user;

import com.example.dream_shop.dto.UserDto;
import com.example.dream_shop.exception.AlreadyExistException;
import com.example.dream_shop.exception.ResourceNotFounException;
import com.example.dream_shop.model.User;
import com.example.dream_shop.repository.UserRepository;
import com.example.dream_shop.request.CreateUserRequest;
import com.example.dream_shop.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements  IUserService{
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public User getUserByID(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFounException("User not found!"));
    }

    @Override
    public User createUser(CreateUserRequest request) {
        return Optional.of(request)
                .filter(user -> !userRepository.existsByEmail(request.getEmail()))
                .map(req ->{
                    User user = new User();
                    user.setEmail(request.getEmail());
                    user.setPassword(request.getPassword());
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    return userRepository.save(user);
                }).orElseThrow(() -> new AlreadyExistException("Ops" + request.getEmail() + "already exists!"));
    }

    @Override
    public User updateUser(UserUpdateRequest request, Long userId) {
        return userRepository.findById(userId).map(existingUser ->{
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            return userRepository.save(existingUser);
        }).orElseThrow(()-> new ResourceNotFounException("User not found"));

        // Retrieve the user by ID, throw exception if not found
        //User existingUser = userRepository.findById(userId)
        //        .orElseThrow(() -> new ResourceNotFounException("User not found"));

        // Update fields
        //existingUser.setFirstName(request.getFirstName());
        //existingUser.setLastName(request.getLastName());
        // Add other fields you want to update here

        // Save the updated user
        //return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId).ifPresentOrElse(userRepository :: delete, ()->{
            throw new ResourceNotFounException("User not found");
        });
    }

    @Override
    public UserDto convertUserToDto(User user){
        return modelMapper.map(user, UserDto.class);
    }
}
