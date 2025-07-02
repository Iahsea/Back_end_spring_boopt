package com.project.shopapp.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
// import java.util.Objects;
import java.util.UUID;

import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.dtos.UserAvatarDTO;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.models.User;
import com.project.shopapp.models.UserAvatar;
import com.project.shopapp.services.FileService;
import com.project.shopapp.services.ProductService;
import com.project.shopapp.services.UserService;
import com.project.shopapp.utils.MessageKeys;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("${api.prefix}")
@RequiredArgsConstructor
public class FileController {

    private final ProductService productService;
    private final LocalizationUtils localizationUtils;
    private final FileService fileService;
    private final UserService userService;

    @PostMapping(value = "products/uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // POST http://localhost:8088/v1/api/products
    public ResponseEntity<?> uploadImages(
            @PathVariable("id") Long productId,
            @RequestParam(defaultValue = "") String folder,
            @RequestParam(name = "files", required = false) List<MultipartFile> files) {
        try {
            Product existingProduct = productService.getProductById(productId);
            files = files == null ? new ArrayList<MultipartFile>() : files;
            if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest().body(localizationUtils
                        .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_MAX_5));
            }
            List<ProductImage> productImages = new ArrayList<>();
            for (MultipartFile file : files) {
                if (file.getSize() == 0) {
                    continue;
                }
                // Kiểm tra kích thước file và định dạng
                if (file.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body(localizationUtils
                                    .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE));
                }

                String fileName = file.getOriginalFilename();
                List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
                boolean isValidExtension = allowedExtensions.stream()
                        .anyMatch(item -> fileName.toLowerCase().endsWith("." + item));

                if (!isValidExtension) {
                    throw new Exception("Invalid file extension. Only allow " + allowedExtensions.toString());
                }

                // Lưu file và cập nhật thumbnail trong DTO
                String filename = this.fileService.storeFile(file, folder); // Thay thế hàm này với code của bạn để lưu
                                                                            // file
                // lưu vào đối tượng product trong DB => sẽ làm sau
                // lưu vào bảng product_images
                ProductImage productImage = fileService.createProductImage(
                        existingProduct.getId(),
                        ProductImageDTO.builder()
                                .imageUrl(filename)
                                .build());
                productImages.add(productImage);
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "users/uploads/{id}")
    // POST http://localhost:8088/v1/api/products
    public ResponseEntity<?> uploadAvatar(
            @PathVariable("id") Long userId,
            @RequestParam("folder") String folder,
            @RequestParam(name = "files", required = false) MultipartFile files) {
        try {
            User existingUser = userService.getUserById(userId);
            // files = files == null ? new ArrayList<MultipartFile>() : files;
            // if (files == null) {
            // throw new Exception("File not found");
            // }
            // if (files.length > UserAvatar.MAXIMUM_IMAGES_PER_USER) {
            // return ResponseEntity.badRequest().body(localizationUtils
            // .getLocalizedMessage(MessageKeys.UPLOAD_AVATAR_MAX_1));
            // }
            // List<ProductImage> productImages = new ArrayList<>();
            // if (files.getSize() == 0) {
            // continue;
            // }
            // Kiểm tra kích thước file và định dạng
            if (files.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
                return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                        .body(localizationUtils
                                .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE));
            }

            String fileName = files.getOriginalFilename();
            List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
            boolean isValidExtension = allowedExtensions.stream()
                    .anyMatch(item -> fileName.toLowerCase().endsWith("." + item));

            if (!isValidExtension) {
                throw new Exception("Invalid file extension. Only allow " + allowedExtensions.toString());
            }

            // Lưu file và cập nhật thumbnail trong DTO
            String filename = this.fileService.storeFile(files, folder); // Thay thế hàm này với code của bạn để lưu
                                                                         // file
            // lưu vào đối tượng product trong DB => sẽ làm sau
            // lưu vào bảng product_images
            UserAvatar userAvatar = fileService.createUserAvatar(
                    existingUser.getId(),
                    UserAvatarDTO.builder()
                            .imageUrl(filename)
                            .build());
            // productImages.add(productImage);
            return ResponseEntity.ok().body(userAvatar);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("products/images/{imageName}")
    public ResponseEntity<?> viewProductImage(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/imagenotfound.jpg").toUri()));
                // return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/avatars/{imageName}")
    public ResponseEntity<?> viewAvatar(@PathVariable String imageName) {
        try {
            java.nio.file.Path imagePath = Paths.get("uploads/avatars/" + imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource(Paths.get("uploads/avatars/defaultavatar.png").toUri()));
                // return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // @PutMapping("avatars/{id}")
    // public ResponseEntity<?> putMethodName(@PathVariable Long userId,
    // @RequestParam("files") MultipartFile files) throws Exception {
    // User existingUser = userService.getUserById(userId);
    // if (files.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
    // return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
    // .body(localizationUtils
    // .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE));
    // }

    // String fileName = files.getOriginalFilename();
    // List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png",
    // "doc", "docx");
    // boolean isValidExtension = allowedExtensions.stream()
    // .anyMatch(item -> fileName.toLowerCase().endsWith("." + item));

    // if (!isValidExtension) {
    // throw new Exception("Invalid file extension. Only allow " +
    // allowedExtensions.toString());
    // }

    // String filename = this.fileService.storeFile(files, "avatars"); // Thay thế
    // hàm này với code của bạn để lưu file

    // this.fileService.updateUserAvatar(existingUser.getId(), filename);

    // return
    // ResponseEntity.ok().body(localizationUtils.getLocalizedMessage(MessageKeys.UPDATE_AVATAR_SUCCESSFULLY));
    // }

}
