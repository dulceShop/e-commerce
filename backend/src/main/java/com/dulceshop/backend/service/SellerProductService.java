package com.dulceshop.backend.service;
import com.dulceshop.backend.dto.PageResponse;
import com.dulceshop.backend.dto.ProductRequest;
import com.dulceshop.backend.dto.ProductResponse;
import com.dulceshop.backend.entity.Category;
import com.dulceshop.backend.entity.Product;
import com.dulceshop.backend.entity.User;
import com.dulceshop.backend.exception.CategoryNotFoundException;
import com.dulceshop.backend.exception.ProductNotFoundException;
import com.dulceshop.backend.repository.CategoryRepository;
import com.dulceshop.backend.repository.ProductRepository;
import com.dulceshop.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.dulceshop.backend.exception.ProductNotFoundException;
@Service
@RequiredArgsConstructor
public class SellerProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
        private final StockAlertService stockAlertService;

    @Transactional
    public ProductResponse create(String sellerEmail, ProductRequest request) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + sellerEmail));

        Product product = new Product();
        product.setSeller(seller);
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        if (request.lowStockThreshold() != null) {
            product.setLowStockThreshold(request.lowStockThreshold());
        }
        if (request.active() != null) {
            product.setActive(request.active());
        }
                Product saved = productRepository.save(product);
        stockAlertService.checkStock(saved);
        return ProductResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> findMine(String sellerEmail, int page, int size) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + sellerEmail));

        var pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50),
                Sort.by(Sort.Direction.DESC, "createdAt"));

        return PageResponse.from(
                productRepository.findBySellerId(seller.getId(), pageable).map(ProductResponse::from));
    }
        @Transactional
    public ProductResponse update(String email, boolean admin, Long id, ProductRequest request) {
        Product product = findOwned(email, admin, id);

        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        if (request.lowStockThreshold() != null) {
            product.setLowStockThreshold(request.lowStockThreshold());
        }
        if (request.active() != null) {
            product.setActive(request.active());
        }
        product.setCategory(findCategory(request.categoryId()));
stockAlertService.checkStock(product);
        return ProductResponse.from(product);
    }
    @Transactional
    public ProductResponse updateStock(String email, boolean admin, Long id, int stockQuantity) {
        Product product = findOwned(email, admin, id);
        product.setStockQuantity(stockQuantity);
        stockAlertService.checkStock(product);
        return ProductResponse.from(product);
    }
    @Transactional
    public void deactivate(String email, boolean admin, Long id) {
        findOwned(email, admin, id).setActive(false);
    }

    private Product findOwned(String email, boolean admin, Long id) {
        if (admin) {
            return productRepository.findById(id)
                    .orElseThrow(() -> new ProductNotFoundException(id));
        }
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + email));
        return productRepository.findByIdAndSellerId(id, seller.getId())
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private Category findCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }
}