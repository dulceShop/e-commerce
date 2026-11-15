package com.dulceshop.backend.service;

import com.dulceshop.backend.entity.Role;
import com.dulceshop.backend.entity.SellerProfile;
import com.dulceshop.backend.entity.User;
import com.dulceshop.backend.exception.ProfileNotFoundException;
import com.dulceshop.backend.repository.SellerProfileRepository;
import com.dulceshop.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerProfileServiceTest {

    @Mock
    private SellerProfileRepository sellerProfileRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SellerProfileService sellerProfileService;

    @Test
    void approve_marksProfileApprovedAndChangesUserRoleToSeller() {
        User user = new User();
        user.setId(2L);
        user.setRole(Role.CUSTOMER);

        SellerProfile profile = new SellerProfile();
        profile.setId(5L);
        profile.setUser(user);
        profile.setShopName("Boutique Test");
        profile.setShopDescription("Description");
        profile.setApproved(false);

        when(sellerProfileRepository.findById(5L))
                .thenReturn(Optional.of(profile));

        var response = sellerProfileService.approve(5L);

        assertTrue(profile.isApproved());
        assertEquals(Role.SELLER, user.getRole());
        assertTrue(response.isApproved());
        assertEquals(2L, response.getUserId());
    }

    @Test
    void approve_throwsException_whenProfileDoesNotExist() {
        when(sellerProfileRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ProfileNotFoundException.class,
                () -> sellerProfileService.approve(99L)
        );
    }
}