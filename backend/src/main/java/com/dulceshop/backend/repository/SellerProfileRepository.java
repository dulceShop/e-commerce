package com.dulceshop.backend.repository;

import com.dulceshop.backend.entity.SellerProfile;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, Long> {

    Optional<SellerProfile> findByUserId(Long userId);

    boolean existsByUserId(Long userId);

    List<SellerProfile> findByApproved(boolean approved);

    @EntityGraph(attributePaths = "user")
    List<SellerProfile> findByUserIdIn(Set<Long> userIds);
}