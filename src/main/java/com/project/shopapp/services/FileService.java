package com.project.shopapp.services;

// import org.apache.catalina.User;
import org.springframework.stereotype.Service;

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

    public UserAvatar createUserAvatar(Long userId, UserAvatarDTO userAvatarDTO) throws DataNotFoundException {

        User existingUser = userRepository
                .findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find product with id: " + userAvatarDTO.getUserId()));
        UserAvatar newUserAvatar = UserAvatar.builder()
                .user(existingUser)
                .imageUrl(userAvatarDTO.getImageUrl())
                .build();
        // int size = productImageRepository.findByProductId(userId).size();
        // if(size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT){
        // throw new InvalidParamException(
        // "Number of images must be <= "
        // +ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
        // }
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

}
