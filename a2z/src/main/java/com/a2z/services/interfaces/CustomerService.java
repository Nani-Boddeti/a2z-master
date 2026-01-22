package com.a2z.services.interfaces;

import com.a2z.dao.Customer;
import com.a2z.data.*;

import java.util.List;

public interface CustomerService {
    Customer authenticateCustomer(String username, String password);

    CustomerData saveCustomer(CustomerData customerData);

    void deleteCustomer(Long id);

    void disableCustomer(String id);

    Customer getUserByEmailORPhone(String phone, String email);

    CustomerData validateOTP(OTPFormData otpFormData);

    CustomerData getCustomerProfile(String userName);

    PagedAdPostResult retriveAllMyAds(String userName, Integer pageNo, Integer pageSize);

    PagedA2zOrderResult getAllMyOrders(String userName, Integer pageNo, Integer pageSize);

    OrderData viewOrder(Long id, String userName);

    PagedA2zApprovalResult getAllApprovalRequests(String userName, Integer pageNo, Integer pageSize);

    ApprovalRequestData getApprovalRequest(Long id, String userName);

    ApprovalRequestData submitApprovalRequest(ApprovalRequestPostData requestData, String userName);

    boolean isCustomerEligibleToPost(String userName);
}
