package com.taxi.booking;

import com.taxi.booking.dto.*;
import com.taxi.booking.util.JwtUtil;
import com.taxi.booking.util.Md5Util;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO register(RegisterUserRequest request) {
        if (request.phone == null || request.phone.isBlank()) {
            throw new IllegalArgumentException("Phone không được để trống");
        }

        if (userRepository.findByPhone(request.phone).isPresent()) {
            throw new IllegalArgumentException("Phone đã tồn tại");
        }

        User user = new User();
        user.setFullName(request.fullName);
        user.setEmail(request.email);
        user.setPhone(request.phone);
        user.setPassword(Md5Util.hash(request.password));
        user.setRole(
                request.role == null || request.role.isBlank()
                        ? "PASSENGER"
                        : request.role
        );

        User saved = userRepository.save(user);
        return mapToDTO(saved);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByPhone(request.phone)
                .orElseThrow(() -> new IllegalArgumentException("Sai phone hoặc password"));

        String hashedPassword = Md5Util.hash(request.password);

        if (!user.getPassword().equals(hashedPassword)) {
            throw new IllegalArgumentException("Sai phone hoặc password");
        }

        LoginResponse response = new LoginResponse();
        response.id = user.getId();
        response.fullName = user.getFullName();
        response.email = user.getEmail();
        response.phone = user.getPhone();
        response.role = user.getRole();
        response.token = JwtUtil.generateToken(user.getId(), user.getPhone());

        return response;
    }

    public UserResponseDTO getMe(String authHeader) {
        String token = extractToken(authHeader);
        Long userId = JwtUtil.getUserIdFromToken(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user"));

        return mapToDTO(user);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    private String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Token không hợp lệ");
        }
        return authHeader.substring(7);
    }

    private UserResponseDTO mapToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.id = user.getId();
        dto.fullName = user.getFullName();
        dto.email = user.getEmail();
        dto.phone = user.getPhone();
        dto.role = user.getRole();
        return dto;
    }
}