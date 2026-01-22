package com.a2z.events;

import com.a2z.dao.Customer;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


public class CustomerRegistrationEvent extends ApplicationEvent {
    Customer customer = (Customer) getSource();
    public CustomerRegistrationEvent(Customer source) {
        super(source);
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
