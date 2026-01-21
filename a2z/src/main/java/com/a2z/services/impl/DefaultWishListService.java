package com.a2z.services.impl;

import com.a2z.dao.A2zWishlist;
import com.a2z.dao.AdPost;
import com.a2z.dao.Customer;
import com.a2z.data.WishlistData;
import com.a2z.persistence.A2zAdPostRepository;
import com.a2z.persistence.PODCustomerRepository;
import com.a2z.persistence.RootRepository;
import com.a2z.populators.WishlistPopulator;
import com.a2z.services.interfaces.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultWishListService implements WishlistService {
    @Autowired
    RootRepository rootRepository;
    @Autowired
    private PODCustomerRepository customerRepo;
    @Autowired
    private A2zAdPostRepository adPostRepository;

    @Autowired
    private WishlistPopulator wishlistPopulator;

    @Override
    public void addToWishlist(Long id, String userName) {
        Optional<AdPost> adOpt = adPostRepository.findById(id);
        Optional<Customer> customerOpt = customerRepo.findById(userName);
        if(adOpt.isPresent() && customerOpt.isPresent()) {
            AdPost ad = adOpt.get();
            Customer customer = customerOpt.get();
            A2zWishlist wishlist = new A2zWishlist();
            wishlist.setCustomer(customer);
            List<AdPost> newAdList = new ArrayList<>();
            newAdList.addAll(wishlist.getAds());
            newAdList.add(ad);
            wishlist.setAds(newAdList);
            rootRepository.save(wishlist);
        }
    }
    @Override
    public WishlistData getWishlist(Long id, String userName) {
        Optional<Customer> customerOpt = customerRepo.findById(userName);
        WishlistData wishlistData = new WishlistData();
        if(customerOpt.isPresent()) {
            Optional<A2zWishlist> wishlistOpt = rootRepository.getWishlistDetails(id,customerOpt.get());
            if(wishlistOpt.isPresent() && customerOpt.isPresent()) {
                A2zWishlist wishList = wishlistOpt.get();
                wishlistPopulator.populate(wishList, wishlistData);
            }
        }
        return wishlistData;


    }
    @Override
    public List<WishlistData> getAllWishlist(String userName) {
        Optional<Customer> customerOpt = customerRepo.findById(userName);
        List<WishlistData> listWishlistData = new ArrayList<WishlistData>();
        if(customerOpt.isPresent()) {
            List<A2zWishlist> wishlistList = rootRepository.getWishlistForCustomer(customerOpt.get());
            wishlistList.stream().forEach(wishlist->{
                WishlistData wishlistData = new WishlistData();
                wishlistPopulator.populate(wishlist, wishlistData);
                listWishlistData.add(wishlistData);
            });
        }
        return listWishlistData;
    }
}
