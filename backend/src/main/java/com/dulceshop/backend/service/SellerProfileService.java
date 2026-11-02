package com.dulceshop.backend.service;

import com.dulceshop.backend.dto.SellerProfileRequest;
import com.dulceshop.backend.dto.SellerProfileResponse;
import com.dulceshop.backend.entity.Role;
import com.dulceshop.backend.entity.SellerProfile;
import com.dulceshop.backend.entity.User;
import com.dulceshop.backend.exception.BadRequestException;
import com.dulceshop.backend.exception.ProfileNotFoundException;
import com.dulceshop.backend.exception.SellerProfileAlreadyExistsException;
import com.dulceshop.backend.repository.SellerProfileRepository;
import com.dulceshop.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerProfileService {

    private final SellerProfileRepository sellerProfileRepository;
    private final UserRepository userRepository;

    public SellerProfileResponse create(String email, SellerProfileRequest request) {
        User user = findUserByEmail(email);

        if (user.getRole() == Role.SELLER) {
            throw new BadRequestException("Cet utilisateur est déjà vendeur");
        }

        if (sellerProfileRepository.existsByUserId(user.getId())) {
            throw new SellerProfileAlreadyExistsException(user.getId());
        }

        SellerProfile profile = new SellerProfile();
        profile.setUser(user);
        profile.setShopName(request.getShopName().trim());
        profile.setShopDescription(request.getShopDescription());
        profile.setApproved(false);

        return toResponse(sellerProfileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public SellerProfileResponse getMine(String email) {
        User user = findUserByEmail(email);

        SellerProfile profile = sellerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ProfileNotFoundException(user.getId()));

        return toResponse(profile);
    }

    @Transactional(readOnly = true)
    public SellerProfileResponse getById(Long id) {
        return toResponse(findProfile(id));
    }

    @Transactional(readOnly = true)
    public List<SellerProfileResponse> getAll(Boolean approved) {
        List<SellerProfile> profiles = approved == null
                ? sellerProfileRepository.findAll()
                : sellerProfileRepository.findByApproved(approved);

        return profiles.stream()
                .map(this::toResponse)
                .toList();
    }

    public SellerProfileResponse approve(Long id) {
        SellerProfile profile = findProfile(id);

        profile.setApproved(true);
        profile.getUser().setRole(Role.SELLER);

        return toResponse(profile);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Utilisateur introuvable : " + email
                ));
    }

    private SellerProfile findProfile(Long id) {
        return sellerProfileRepository.findById(id)
                .orElseThrow(() -> new ProfileNotFoundException(id));
    }

    private SellerProfileResponse toResponse(SellerProfile profile) {
        return new SellerProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getShopName(),
                profile.getShopDescription(),
                profile.isApproved()
        );
    }
        public SellerProfileResponse updateMine(String email, SellerProfileRequest request) {
        User user = findUserByEmail(email);

        SellerProfile profile = sellerProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ProfileNotFoundException(user.getId()));

        profile.setShopName(request.getShopName().trim());
        profile.setShopDescription(request.getShopDescription());

        return toResponse(profile);
    }
}