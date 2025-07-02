package com.project.shopapp.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

// import org.apache.catalina.User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.dtos.UserAvatarDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.models.User;
import com.project.shopapp.models.UserAvatar;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.repositories.UserAvatarRepository;
import com.project.shopapp.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FileService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductImageRepository productImageRepository;
    private final UserAvatarRepository userAvatarRepository;

    public UserAvatar createUserAvatar(Long userId, UserAvatarDTO userAvatarDTO)
            throws DataNotFoundException, InvalidParamException {

        User existingUser = userRepository
                .findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find product with id: " + userAvatarDTO.getUserId()));
        UserAvatar newUserAvatar = UserAvatar.builder()
                .user(existingUser)
                .imageUrl(userAvatarDTO.getImageUrl())
                .build();
        int size = userAvatarRepository.findByUserId(userId).isPresent()
                ? userAvatarRepository.findByUserId(userId).get().size()
                : 0;
        if (size >= UserAvatar.MAXIMUM_IMAGES_PER_USER) {
            throw new InvalidParamException(
                    "Number of images must be <= "
                            + UserAvatar.MAXIMUM_IMAGES_PER_USER);
        }
        return this.userAvatarRepository.save(newUserAvatar);

    }

    public ProductImage createProductImage(
            Long productId,
            ProductImageDTO productImageDTO) throws Exception {
        Product existingProduct = productRepository
                .findById(productId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find product with id: " + productImageDTO.getProductId()));
        ProductImage newProductImage = ProductImage.builder()
                .product(existingProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();
        int size = productImageRepository.findByProductId(productId).size();
        if (size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException(
                    "Number of images must be <= "
                            + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        }
        return productImageRepository.save(newProductImage);
    }

    public String storeFile(MultipartFile file, String folder) throws IOException {
        if (file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format");
        }
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        // Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        // Đường dẫn đến thư mục mà bạn muốn lưu file
        java.nio.file.Path uploadDir = Paths.get("uploads", folder);
        // Kiểm tra và tạo thư mục nếu nó không tồn tại
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // Đường dẫn đầy đủ đến file
        java.nio.file.Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        // Sao chép file vào thư mục đích
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }

    public UserAvatar updateUserAvatar(User user, String fileName) {
        Optional<List<UserAvatar>> userAvatarOptional = this.userAvatarRepository.findByUserId(user.getId());
        UserAvatar userAvatar;
        if (userAvatarOptional.isPresent() && !userAvatarOptional.get().isEmpty()) {
            userAvatar = userAvatarOptional.get().get(0); // Lấy avatar đầu tiên
        } else {
            userAvatar = new UserAvatar();
            userAvatar.setUser(user); // Gán user mới cho avatar nếu chưa có
        }
        userAvatar.setImageUrl(fileName);
        return userAvatarRepository.save(userAvatar);
    }

}
