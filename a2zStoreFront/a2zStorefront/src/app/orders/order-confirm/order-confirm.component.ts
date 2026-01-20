import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OrderModel } from '../../models/order.model';

@Component({
  selector: 'app-order-confirm',
  templateUrl: './order-confirm.component.html',
  styleUrl: './order-confirm.component.css'
})
export class OrderConfirmComponent implements OnInit {
  orderConfirmed: boolean = false;
  orderNumber: string = '';
  estimatedDelivery: string = '';
  orderDate: string = '';
  orderDetails: any = {};

  constructor(private router: Router) {}

  ngOnInit(): void {
    // Get order data from router state or sessionStorage
    const navigation = this.router.getCurrentNavigation();
    const orderData = sessionStorage.getItem('lastOrder');
    
    if (orderData) {
      this.orderDetails = typeof orderData === 'string' ? JSON.parse(orderData) : orderData;
      this.generateConfirmation();
    } else {
      // Redirect if no order data is available
      this.router.navigate(['/ad-list']);
    }
  }

  generateConfirmation(): void {
    // Generate order number
    this.orderNumber = this.orderDetails.id;
    
    // Set order date
    this.orderDate = new Date().toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
    
    // Calculate estimated delivery (7-10 business days)
    const deliveryDate = new Date();
    deliveryDate.setDate(deliveryDate.getDate() + 7);
    this.estimatedDelivery = deliveryDate.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
    
    this.orderConfirmed = true;
  }

  shopMore(): void {
    this.router.navigate(['/ad-list']);
  }

  viewOrders(): void {
    this.router.navigate(['/my-orders']);
  }
}
