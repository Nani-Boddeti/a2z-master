package com.a2z.services.interfaces;

import com.a2z.data.WishlistData;

import java.util.List;

public interface WishlistService {
    void addToWishlist(Long id, String userName);

    WishlistData getWishlist(Long id, String userName);

    List<WishlistData> getAllWishlist(String userName);
}
