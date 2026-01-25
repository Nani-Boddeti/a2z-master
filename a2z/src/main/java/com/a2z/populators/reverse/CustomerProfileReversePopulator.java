package com.a2z.populators.reverse;

import com.a2z.dao.Customer;
import com.a2z.data.CustomerProfileUpdateData;
import com.a2z.populators.Populator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.ConversionException;

public class CustomerProfileReversePopulator implements Populator<CustomerProfileUpdateData , Customer> {
    @Override
    public void populate(CustomerProfileUpdateData customerProfileUpdateData, Customer customerEntity) throws ConversionException {
        if(StringUtils.isNotBlank(customerProfileUpdateData.getEmail())){
            customerEntity.setEmail(customerProfileUpdateData.getEmail());
        }
        if(StringUtils.isNotBlank(customerProfileUpdateData.getFirstName())){
            customerEntity.setFirstName(customerProfileUpdateData.getFirstName());
        }
        if(StringUtils.isNotBlank(customerProfileUpdateData.getLastName())){
            customerEntity.setLastName(customerProfileUpdateData.getLastName());
        }
        if(StringUtils.isNotBlank(customerProfileUpdateData.getPhoneNumber())){
            customerEntity.setPhoneNumber(customerProfileUpdateData.getPhoneNumber());

        }
    }
}
