package com.a2z.populators;

import com.a2z.dao.A2zCategory;
import com.a2z.dao.CategoryData;
import com.a2z.data.AdPostData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class CategoryPopulator implements Populator<A2zCategory, CategoryData>{

    @Autowired
    AdPostCategoryPopulator adPostCategoryPopulator;

    @Override
    public void populate(A2zCategory source, CategoryData target) {
        target.setCategoryCode(source.getCategoryCode());
        target.setIsVisible(source.getIsVisible());
        target.setCategoryName(source.getCategoryName());

        List<AdPostData> adPostDataList = new ArrayList<>();
        if(source.getAdPosts() != null) {
            for(var adPost : source.getAdPosts()) {
                AdPostData adPostData = new AdPostData();
                adPostCategoryPopulator.populate(adPost, adPostData);
                adPostDataList.add(adPostData);
            }
        }
        target.setAdPostDataList(adPostDataList);
    }
}
