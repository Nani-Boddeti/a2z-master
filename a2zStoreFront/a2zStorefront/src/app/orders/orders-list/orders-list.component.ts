import { Component } from '@angular/core';
import { OrderServiceService } from '../../services/order-service.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-orders-list',
  templateUrl: './orders-list.component.html',
  styleUrl: './orders-list.component.css'
})
export class OrdersListComponent {
 orderList:any;
   currentPage = 1;
  totalPages = 10;
  totalItems = 100;
  itemsPerPage = 10;
 constructor(private orderservice : OrderServiceService, private router: Router) { }

  ngAfterViewInit() {
     this.getOrders(this.currentPage);
  }
  getOrders(page: number): void {
    this.orderservice.getMyAdOrders(page, this.itemsPerPage).subscribe({
      next: (data: any) => {
        this.orderList = data.a2zOrders;
        this.totalPages = data.totalPages;
        this.currentPage = data.currentPage + 1;
      },
      error: (error: any) => {
        console.error('Error fetching orders:', error);
        this.router.navigate(['/loginV3']);
      }
    });
  }
changePageSize(newSize: number) {
    this.itemsPerPage = newSize;
    this.getOrders(1); // Reload items with new page size, starting from page 1
  }
  

}
