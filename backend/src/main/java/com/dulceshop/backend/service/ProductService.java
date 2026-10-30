package com.dulceshop.backend.service;

import com.dulceshop.backend.dto.PageResponse;
import com.dulceshop.backend.dto.ProductResponse;
import com.dulceshop.backend.exception.BadRequestException;
import com.dulceshop.backend.exception.ProductNotFoundException;
import com.dulceshop.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Set<String> SORTABLE_FIELDS = Set.of("name", "price", "createdAt");
    private static final int MAX_PAGE_SIZE = 50;

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> search(String q, Long categoryId,
                                                BigDecimal minPrice, BigDecimal maxPrice,
                                                int page, int size, String sort) {
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new BadRequestException("minPrice must not be greater than maxPrice");
        }
        if (page < 0 || size < 1) {
            throw new BadRequestException("page must be >= 0 and size must be >= 1");
        }

        String query = (q == null || q.isBlank()) ? null : q.trim();
        Pageable pageable = PageRequest.of(page, Math.min(size, MAX_PAGE_SIZE), parseSort(sort));

        return PageResponse.from(
                productRepository.search(categoryId, minPrice, maxPrice, query, pageable)
                        .map(ProductResponse::from));
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        }
        String[] parts = sort.split(",");
        String field = parts[0].trim();
        if (!SORTABLE_FIELDS.contains(field)) {
            throw new BadRequestException("Cannot sort by '" + field + "'. Allowed: " + SORTABLE_FIELDS);
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1) {
            direction = switch (parts[1].trim().toLowerCase()) {
                case "asc" -> Sort.Direction.ASC;
                case "desc" -> Sort.Direction.DESC;
                default -> throw new BadRequestException("Sort direction must be asc or desc");
            };
        }
        return Sort.by(direction, field);
    }
}