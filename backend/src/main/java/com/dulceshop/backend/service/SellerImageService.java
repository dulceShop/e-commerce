package com.dulceshop.backend.service;

import com.dulceshop.backend.dto.ProductImageRequest;
import com.dulceshop.backend.dto.ProductImageResponse;
import com.dulceshop.backend.entity.Product;
import com.dulceshop.backend.entity.ProductImage;
import com.dulceshop.backend.exception.ImageNotFoundException;
import com.dulceshop.backend.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerImageService {

    private final SellerProductService sellerProductService;
    private final ProductImageRepository productImageRepository;

    @Transactional
    public ProductImageResponse add(String email, boolean admin, Long productId,
                                    ProductImageRequest request) {
        Product product = sellerProductService.findOwned(email, admin, productId);

        boolean makeMain = Boolean.TRUE.equals(request.main()) || product.getImages().isEmpty();
        if (makeMain) {
            product.getImages().forEach(image -> image.setMain(false));
        }

        ProductImage image = new ProductImage();
        image.setUrl(request.url().trim());
        image.setMain(makeMain);
        product.addImage(image);
        productImageRepository.save(image);

        return ProductImageResponse.from(image);
    }

    @Transactional
    public List<ProductImageResponse> setMain(String email, boolean admin, Long productId, Long imageId) {
        Product product = sellerProductService.findOwned(email, admin, productId);
        findImage(product, imageId);

        product.getImages().forEach(image -> image.setMain(image.getId().equals(imageId)));
        return product.getImages().stream().map(ProductImageResponse::from).toList();
    }

    @Transactional
    public void delete(String email, boolean admin, Long productId, Long imageId) {
        Product product = sellerProductService.findOwned(email, admin, productId);
        ProductImage image = findImage(product, imageId);
        boolean wasMain = image.isMain();

        product.getImages().remove(image);

        if (wasMain) {
            product.getImages().stream().findFirst().ifPresent(first -> first.setMain(true));
        }
    }

    private ProductImage findImage(Product product, Long imageId) {
        return product.getImages().stream()
                .filter(image -> image.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new ImageNotFoundException(imageId));
    }
}