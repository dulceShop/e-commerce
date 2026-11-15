package com.dulceshop.backend.repository;

import com.dulceshop.backend.entity.Category;
import com.dulceshop.backend.entity.Product;
import com.dulceshop.backend.entity.Role;
import com.dulceshop.backend.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest(showSql = false)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void search_filtersProductsAndExcludesInactiveProducts() {
        Category category = persistCategory("Vetements");
        User seller = persistSeller();

        Product expected = persistProduct(
                "T-shirt blanc",
                new BigDecimal("20.00"),
                true,
                category,
                seller
        );

        persistProduct(
                "Pantalon noir",
                new BigDecimal("45.00"),
                true,
                category,
                seller
        );

        persistProduct(
                "T-shirt archive",
                new BigDecimal("15.00"),
                false,
                category,
                seller
        );

        entityManager.flush();

        var result = productRepository.search(
                category.getId(),
                new BigDecimal("10.00"),
                new BigDecimal("30.00"),
                "t-shirt",
                PageRequest.of(0, 10)
        );

        assertEquals(1, result.getTotalElements());
        assertEquals(expected.getId(), result.getContent().getFirst().getId());
    }

    @Test
    void search_appliesPaginationAndSort() {
        Category category = persistCategory("Maison");
        User seller = persistSeller();

        persistProduct("Produit A", new BigDecimal("10.00"), true, category, seller);
        Product expected = persistProduct(
                "Produit B",
                new BigDecimal("20.00"),
                true,
                category,
                seller
        );
        persistProduct("Produit C", new BigDecimal("30.00"), true, category, seller);

        entityManager.flush();

        var result = productRepository.search(
                null,
                null,
                null,
                null,
                PageRequest.of(
                        1,
                        1,
                        Sort.by(Sort.Direction.DESC, "price")
                )
        );

        assertEquals(3, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(expected.getId(), result.getContent().getFirst().getId());
    }

    @Test
    void findByIdAndActiveTrue_doesNotReturnInactiveProduct() {
        Category category = persistCategory("Informatique");
        User seller = persistSeller();

        Product inactiveProduct = persistProduct(
                "Produit inactif",
                new BigDecimal("50.00"),
                false,
                category,
                seller
        );

        entityManager.flush();

        assertTrue(
                productRepository.findByIdAndActiveTrue(inactiveProduct.getId())
                        .isEmpty()
        );
    }

    private Category persistCategory(String name) {
        return entityManager.persist(new Category(name, "Description"));
    }

    private User persistSeller() {
        User seller = new User();
        seller.setFirstName("Seller");
        seller.setLastName("Test");
        seller.setEmail("seller-" + System.nanoTime() + "@example.com");
        seller.setPasswordHash("encoded-password");
        seller.setRole(Role.SELLER);
        seller.setActive(true);

        return entityManager.persist(seller);
    }

    private Product persistProduct(
            String name,
            BigDecimal price,
            boolean active,
            Category category,
            User seller
    ) {
        Product product = new Product();
        product.setName(name);
        product.setDescription("Description");
        product.setPrice(price);
        product.setStockQuantity(10);
        product.setLowStockThreshold(2);
        product.setActive(active);
        product.setCategory(category);
        product.setSeller(seller);

        return entityManager.persist(product);
    }
}