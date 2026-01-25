package com.a2z.services.interfaces;

import com.a2z.dao.Customer;
import com.a2z.data.*;
import com.a2z.services.impl.ForgotPasswordTokenGeneratorService.ResetToken;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer authenticateCustomer(String username, String password);

    CustomerData saveCustomer(CustomerData customerData);


    void deleteCustomer(String userName);

    void disableCustomer(String id);

    Optional<Customer> getUserByUserNameORPhone(String userName);

    CustomerData validateOTP(OTPFormData otpFormData);

    CustomerData getCustomerProfile(String userName);


    CustomerData updateCustomer(CustomerProfileUpdateData customerProfileUpdateData);

    PagedAdPostResult retriveAllMyAds(String userName, Integer pageNo, Integer pageSize);

    PagedA2zOrderResult getAllMyOrdersInStatus(String userName, Integer pageNo, Integer pageSize, String orderStatus);

    PagedA2zOrderResult getAllMyOrders(String userName, Integer pageNo, Integer pageSize);

    OrderData viewOrder(Long id, String userName);

    PagedA2zApprovalResult getAllApprovalRequests(String userName, Integer pageNo, Integer pageSize);

    ApprovalRequestData getApprovalRequest(Long id, String userName);

    ApprovalRequestData submitApprovalRequest(ApprovalRequestPostData requestData, String userName);

    boolean isCustomerEligibleToPost(String userName);

    void sendForgotPasswordLink(String email);

    ResetToken verifyUserForForgotPasswordLink(String key);

    Boolean updatePassword(String newPassword, String userName);
}
