package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.dtos.TopSellingProductDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidParamException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.OrderDetailRepository;
import com.project.shopapp.repositories.ProductImageRepository;
import com.project.shopapp.repositories.ProductRepository;
import com.project.shopapp.responses.ProductResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existingCategory = categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find category with id: " + productDTO.getCategoryId()));

        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .category(existingCategory)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long productId) throws Exception {
        return productRepository.findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find product with id = " + productId));
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword, Long categoryId,
            PageRequest pageRequest) {
        Page<Product> productPage;
        productPage = productRepository.searchProducts(categoryId, keyword, pageRequest);
        return productPage.map(ProductResponse::fromProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(long id, ProductDTO productDTO) throws Exception {
        Product existingProduct = getProductById(id);
        if (existingProduct != null) {
            Category existingCategory = categoryRepository
                    .findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException(
                            "Cannot find category with id: " + productDTO.getCategoryId()));
            existingProduct.setName(productDTO.getName());
            existingProduct.setCategory(existingCategory);
            existingProduct.setPrice(productDTO.getPrice());
            existingProduct.setDescription(productDTO.getDescription());
            existingProduct.setThumbnail(productDTO.getThumbnail());
            return productRepository.save(existingProduct);
        }
        return null;
    }

    @Override
    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }

    // @Override
    // <<<<<<< HEAD
    // =======
    // public ProductImage createProductImage(
    // Long productId,
    // ProductImageDTO productImageDTO) throws Exception {
    // Product existingProduct = productRepository
    // .findById(productId)
    // .orElseThrow(() -> new DataNotFoundException(
    // "Cannot find product with id: " + productImageDTO.getProductId()));
    // ProductImage newProductImage = ProductImage.builder()
    // .product(existingProduct)
    // .imageUrl(productImageDTO.getImageUrl())
    // .build();
    // int size = productImageRepository.findByProductId(productId).size();
    // if (size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
    // throw new InvalidParamException(
    // "Number of images must be <= "
    // + ProductImage.MAXIMUM_IMAGES_PER_PRODUCT);
    // }
    // return productImageRepository.save(newProductImage);
    // }

    @Override
    public Page<ProductResponse> getProductByCategoryId(Long categoryId, PageRequest pageRequest) {
        Page<Product> productPage = productRepository.findByCategory_Id(categoryId, pageRequest);
        return productPage.map(ProductResponse::fromProduct);
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findProductsByIds(productIds);
    }

    public List<TopSellingProductDTO> getTopSellingProductsInWeek() {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(DayOfWeek.MONDAY); // Lấy ngày đầu tuần (thứ 2)
        LocalDate endOfWeek = now.with(DayOfWeek.SUNDAY); // Lấy ngày cuối tuần (Chủ nhật)

        // Gọi truy vấn từ repository
        List<Object[]> result = orderDetailRepository.findTopSellingProducts(startOfWeek, endOfWeek);

        // Chuyển đổi kết quả Object[] thành DTO để trả về
        List<TopSellingProductDTO> topSellingProducts;
        topSellingProducts = result.stream()
                .map(row -> new TopSellingProductDTO(
                        (Long) row[0], // productId
                        (Long) row[1] // totalSales
                ))
                .limit(5)
                .collect(Collectors.toList());

        return topSellingProducts;
    }
}
