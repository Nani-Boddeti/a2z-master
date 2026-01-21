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

    List<AdPostData> retriveAllMyAds(String userName);

    List<OrderData> getAllMyOrders(String userName);

    OrderData viewOrder(Long id, String userName);

    List<ApprovalRequestData> getAllApprovalRequests(String userName);

    ApprovalRequestData getApprovalRequest(Long id, String userName);

    ApprovalRequestData submitApprovalRequest(ApprovalRequestPostData requestData, String userName);

    boolean isCustomerEligibleToPost(String userName);
}
