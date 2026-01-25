package com.a2z.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerProfileUpdateData extends RootData {


    @Pattern(regexp = "^([a-zA-z0-9])*(?=(.*[!@#$%^&*()\\\\-__+.]))?" , message="{validation.username.regex}")
    @Size(min = 4, message = "{validation.userName.size.too_short}")
    @Size(max = 15, message = "{validation.userName.size.too_long}")
    private String userName;

    @Email(message = "${validation.email}")
    private String email;
    @Pattern(regexp = "^[a-zA-Z0-9[ ]*]*")
    @Size(max = 15, message = "{validation.firstName.size.too_long}")
    private String firstName;

    private AddressData defaultAddress;

    @Pattern(regexp = "^[a-zA-Z0-9[ ]*]*")
    @Size(max = 15, message = "{validation.lastName.size.too_long}")
    private String lastName;

    @Pattern(regexp = "^[a-zA-Z0-9[ ]*]*" , message = "{validation.title}")
    @Size(max = 10, message = "{validation.title.size.too_long}")
    private String title;

    private PaymentInfoData defaultPaymentInfo;

    @Pattern(regexp = "^[0-9]*" , message="{validation.phone}")
    @Size(max = 12, message = "{validation.phoneNumber.size.too_long}")
    private String phoneNumber;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public PaymentInfoData getDefaultPaymentInfo() {
        return defaultPaymentInfo;
    }

    public void setDefaultPaymentInfo(PaymentInfoData defaultPaymentInfo) {
        this.defaultPaymentInfo = defaultPaymentInfo;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AddressData getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(AddressData defaultAddress) {
        this.defaultAddress = defaultAddress;
    }
}
