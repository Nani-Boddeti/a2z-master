import { Component } from '@angular/core';
import { OrderServiceService } from '../../services/order-service.service';

@Component({
  selector: 'app-orders-list',
  templateUrl: './orders-list.component.html',
  styleUrl: './orders-list.component.css'
})
export class OrdersListComponent {
 orderList:any;
 constructor(private orderservice : OrderServiceService) { }

  ngAfterViewInit() {
     this.getOrders();
  }
  getOrders(): void {
    this.orderservice.getMyAdOrders().subscribe((data:any)=>{
  
  console.log('User Profile Data:', data);
  this.orderList = data;
}); 
}
}
