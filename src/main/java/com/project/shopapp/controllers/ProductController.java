package com.project.shopapp.controllers;

import com.github.javafaker.Faker;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.*;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.IProductService;
import com.project.shopapp.services.ProductService;
import com.project.shopapp.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.prefix}/products")
@RequiredArgsConstructor
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final IProductService iproductService;
    private final ProductService productService;
    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    // POST http://localhost:8088/v1/api/products
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
            BindingResult result) {
        try {
            if (result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product newProduct = iproductService.createProduct(productDTO);
            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // <<<<<<< HEAD
    // =======
    // @PostMapping(value = "uploads/{id}", consumes =
    // MediaType.MULTIPART_FORM_DATA_VALUE)
    // // POST http://localhost:8088/v1/api/products
    // public ResponseEntity<?> uploadImages(
    // @PathVariable("id") Long productId,
    // @RequestParam("files") List<MultipartFile> files) {
    // try {
    // Product existingProduct = iproductService.getProductById(productId);
    // files = files == null ? new ArrayList<MultipartFile>() : files;
    // if (files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
    // return ResponseEntity.badRequest().body(localizationUtils
    // .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_MAX_5));
    // }
    // List<ProductImage> productImages = new ArrayList<>();
    // for (MultipartFile file : files) {
    // if (file.getSize() == 0) {
    // continue;
    // }
    // // Kiểm tra kích thước file và định dạng
    // if (file.getSize() > 10 * 1024 * 1024) { // Kích thước > 10MB
    // return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
    // .body(localizationUtils
    // .getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE));
    // }
    // String contentType = file.getContentType();
    // if (contentType == null || !contentType.startsWith("image/")) {
    // return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    // .body(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_MUST_BE_IMAGE));
    // }
    // // Lưu file và cập nhật thumbnail trong DTO
    // String filename = storeFile(file); // Thay thế hàm này với code của bạn để
    // lưu file
    // // lưu vào đối tượng product trong DB => sẽ làm sau
    // // lưu vào bảng product_images
    // ProductImage productImage = iproductService.createProductImage(
    // existingProduct.getId(),
    // ProductImageDTO.builder()
    // .imageUrl(filename)
    // .build());
    // productImages.add(productImage);
    // }
    // return ResponseEntity.ok().body(productImages);
    // } catch (Exception e) {
    // return ResponseEntity.badRequest().body(e.getMessage());
    // }
    // }

    // @GetMapping("/images/{imageName}")
    // public ResponseEntity<?> viewImage(@PathVariable String imageName) {
    // try {
    // java.nio.file.Path imagePath = Paths.get("uploads/" + imageName);
    // UrlResource resource = new UrlResource(imagePath.toUri());

    // if (resource.exists()) {
    // return ResponseEntity.ok()
    // .contentType(MediaType.IMAGE_JPEG)
    // .body(resource);
    // } else {
    // return ResponseEntity.ok()
    // .contentType(MediaType.IMAGE_JPEG)
    // .body(new UrlResource(Paths.get("uploads/notfound.jpg").toUri()));
    // // return ResponseEntity.notFound().build();
    // }
    // } catch (Exception e) {
    // return ResponseEntity.notFound().build();
    // }
    // }

    // private String storeFile(MultipartFile file) throws IOException {
    // if (!isImagesFile(file) || file.getOriginalFilename() == null) {
    // throw new IOException("Invalid image format");
    // }
    // String filename =
    // StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
    // // Thêm UUID vào trước tên file để đảm bảo tên file là duy nhất
    // String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
    // // Đường dẫn đến thư mục mà bạn muốn lưu file
    // java.nio.file.Path uploadDir = Paths.get("uploads");
    // // Kiểm tra và tạo thư mục nếu nó không tồn tại
    // if (!Files.exists(uploadDir)) {
    // Files.createDirectories(uploadDir);
    // }
    // // Đường dẫn đầy đủ đến file
    // java.nio.file.Path destination = Paths.get(uploadDir.toString(),
    // uniqueFilename);
    // // Sao chép file vào thư mục đích
    // Files.copy(file.getInputStream(), destination,
    // StandardCopyOption.REPLACE_EXISTING);
    // return uniqueFilename;
    // }

    // >>>>>>> main
    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            // @RequestParam("page") int page,
            @RequestParam(defaultValue = "10") int limit

    // @RequestParam("limit") int limit
    ) {
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                Sort.by("id").ascending());
        logger.info(String.format("keyword = %s, category_id = %d, page = %d, limit = %d",
                keyword, categoryId, page, limit));
        Page<ProductResponse> productPage = iproductService.getAllProducts(keyword, categoryId, pageRequest);
        int totalProduct = (int) productPage.getTotalElements(); // Tổng số sản phẩm
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse
                .builder()
                .products(products)
                .totalPages(totalPages)
                .totalProduct(totalProduct)
                .build());
    }

    private boolean isImagesFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    // http://localhost:8088/api/v1/products/6
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable("id") Long productId) {
        try {
            Product existingProduct = iproductService.getProductById(productId);
            return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsByIds(@RequestParam("ids") String ids) {
        try {
            // Tách chuỗi ids thành một mảng các số nguyên
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Product> products = iproductService.findProductsByIds(productIds);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ProductListResponse> getProductByCategoryId(
            @PathVariable("categoryId") Long categoryId,
            @RequestParam("page") int page,
            @RequestParam("limit") int limit) {
        try {
            PageRequest pageRequest = PageRequest.of(
                    page, limit,
                    Sort.by("id").ascending());
            Page<ProductResponse> productPage = iproductService.getProductByCategoryId(categoryId, pageRequest);
            return ResponseEntity.ok(ProductListResponse
                    .builder()
                    .products(productPage.getContent())
                    .totalPages(productPage.getTotalPages())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable long id) {
        try {
            iproductService.deleteProduct(id);
            return ResponseEntity.ok(String.format("Product with id = %d deleted successfully", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO) {
        try {
            Product updatedProduct = iproductService.updateProduct(id, productDTO);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/top-selling")
    public ResponseEntity<List<TopSellingProductDTO>> getTopSellingProductsInWeek() {
        try {
            List<TopSellingProductDTO> topSellingProducts = productService.getTopSellingProductsInWeek();
            return ResponseEntity.ok(topSellingProducts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // @PostMapping("/generateFakeProducts")
    private ResponseEntity<String> generateFakeProducts() {
        Faker faker = new Faker();
        for (int i = 0; i < 1_000_000; i++) {
            String productName = faker.commerce().productName();
            if (iproductService.existsByName(productName)) {
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float) faker.number().numberBetween(10, 90_000_000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long) faker.number().numberBetween(1, 4))
                    .build();
            try {
                iproductService.createProduct(productDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake Products created successfully");
    }
}
