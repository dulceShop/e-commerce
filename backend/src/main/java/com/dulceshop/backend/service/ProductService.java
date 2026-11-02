
package com.dulceshop.backend.service;

import com.dulceshop.backend.dto.PageResponse;
import com.dulceshop.backend.dto.ProductResponse;
import com.dulceshop.backend.entity.Product;
import com.dulceshop.backend.entity.SellerProfile;
import com.dulceshop.backend.exception.BadRequestException;
import com.dulceshop.backend.exception.ProductNotFoundException;
import com.dulceshop.backend.repository.ProductRepository;
import com.dulceshop.backend.repository.SellerProfileRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Set<String> SORTABLE_FIELDS =
            Set.of("name", "price", "createdAt");

    private static final int MAX_PAGE_SIZE = 50;

    private final SellerProfileRepository sellerProfileRepository;
    private final ProductRepository productRepository;

    // Rechercher les produits avec pagination et filtres
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> search(
            String q,
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sort) {

        // Vérifier les prix
        if (minPrice != null && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {
            throw new BadRequestException(
                    "minPrice must not be greater than maxPrice");
        }

        // Vérifier la pagination
        if (page < 0 || size < 1) {
            throw new BadRequestException(
                    "page must be >= 0 and size must be >= 1");
        }

        // Préparer le texte de recherche
        String query = (q == null || q.isBlank())
                ? null
                : q.trim();

        // Configurer la pagination et le tri
        Pageable pageable = PageRequest.of(
                page,
                Math.min(size, MAX_PAGE_SIZE),
                parseSort(sort)
        );

        // Rechercher les produits
        var result = productRepository.search(
                categoryId,
                minPrice,
                maxPrice,
                query,
                pageable
        );

        // Récupérer les noms des boutiques
        Map<Long, String> shopNames =
                shopNames(result.getContent());

        // Convertir les produits en DTO
        return PageResponse.from(
                result.map(product ->
                        toResponse(product, shopNames))
        );
    }

    // Récupérer un produit par son identifiant
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {

        // Récupérer le produit ou lancer une exception
        Product product = productRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(id));

        // Récupérer le nom de la boutique
        Map<Long, String> shopNames =
                shopNames(List.of(product));

        // Retourner le produit sous forme de DTO
        return toResponse(product, shopNames);
    }

    // Récupérer les noms des boutiques des vendeurs
    private Map<Long, String> shopNames(
            Collection<Product> products) {

        // Récupérer les identifiants des vendeurs
        Set<Long> sellerIds = products.stream()
                .map(product -> product.getSeller().getId())
                .collect(Collectors.toSet());

        if (sellerIds.isEmpty()) {
            return Map.of();
        }

        // Associer chaque vendeur au nom de sa boutique
        return sellerProfileRepository
                .findByUserIdIn(sellerIds)
                .stream()
                .collect(Collectors.toMap(
                        profile -> profile.getUser().getId(),
                        SellerProfile::getShopName
                ));
    }

    // Transformer une entité Product en ProductResponse
    private ProductResponse toResponse(
            Product product,
            Map<Long, String> shopNames) {

        // Utiliser le nom de la boutique s'il existe
        String name = shopNames.getOrDefault(
                product.getSeller().getId(),
                product.getSeller().getFirstName()
        );

        return ProductResponse.from(product, name);
    }

    // Gérer le tri des produits
    private Sort parseSort(String sort) {

        // Tri par défaut : produits les plus récents
        if (sort == null || sort.isBlank()) {
            return Sort.by(
                    Sort.Direction.DESC,
                    "createdAt"
            );
        }

        String[] parts = sort.split(",");
        String field = parts[0].trim();

        // Vérifier le champ de tri
        if (!SORTABLE_FIELDS.contains(field)) {
            throw new BadRequestException(
                    "Cannot sort by '" + field
                            + "'. Allowed: " + SORTABLE_FIELDS);
        }

        Sort.Direction direction = Sort.Direction.ASC;

        // Vérifier la direction du tri
        if (parts.length > 1) {
            direction = switch (
                    parts[1].trim().toLowerCase()) {

                case "asc" -> Sort.Direction.ASC;
                case "desc" -> Sort.Direction.DESC;

                default -> throw new BadRequestException(
                        "Sort direction must be asc or desc");
            };
        }

        return Sort.by(direction, field);
    }
}
