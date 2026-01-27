import { Component, OnInit, OnDestroy } from '@angular/core';
import { OrderServiceService } from '../../services/order-service.service';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil, concatMap } from 'rxjs/operators';

@Component({
  selector: 'app-orders-list',
  templateUrl: './orders-list.component.html',
  styleUrl: './orders-list.component.css'
})
export class OrdersListComponent implements OnInit, OnDestroy {
 orderList:any;
   currentPage = 1;
   currentIndex = 0;
  totalPages = 10;
  totalItems = 100;
  itemsPerPage = 10;
  selectedStatus: string = '';
  orderStatusList:string[] = [];
  defaultStatus:string = 'ALL';
  private destroy$ = new Subject<void>();
 constructor(private orderservice : OrderServiceService, private router: Router) { }

  ngOnInit(): void {
    // Load status list first, then get orders
    this.orderservice.getOrderStatusList().pipe(
      concatMap((data) => {
        console.log('✅ Order status list loaded:', data);
        this.orderStatusList = data;
        this.orderStatusList.unshift(this.defaultStatus); // Add 'ALL' at the beginning
        // Now load orders after status list is loaded
        return this.orderservice.getMyAdOrders(this.currentPage, this.itemsPerPage);
      }),
      takeUntil(this.destroy$)
    ).subscribe({
      next: (data: any) => {
        console.log('✅ Orders loaded:', data);
        this.orderList = data.a2zOrders;
        this.totalPages = data.totalPages;
        this.currentPage = data.currentPage + 1;
      },
      error: (error: any) => {
        console.error('❌ Error fetching orders:', error);
        this.router.navigate(['/loginV3']);
      }
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  ngAfterViewInit() {
    // Lifecycle hooks have moved to ngOnInit for proper initialization order
  }
  getOrders(page: number): void {
    if(this.selectedStatus){
      return this.getOrdersByStatus(page, this.selectedStatus);
    }else {
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
    
  }
    getOrdersByStatus(page: number, status: string): void {
    this.orderservice.getMyAdOrdersByStatus(page, this.itemsPerPage, status).subscribe({
      next: (data: any) => {
        this.orderList = data.a2zOrders;
        this.totalPages = data.totalPages;
        this.currentPage = data.currentPage + 1;  
        this.selectedStatus = status;
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
  
  OnSubmitExtendOrder(orderId: number) {
    this.orderservice.extendOrder(orderId).subscribe({
      next: (response: any) => {
        console.log('Order extended successfully:', response);
        this.getOrders(this.currentPage); // Refresh the order list
      },
      error: (error: any) => {
        console.error('Error extending order:', error);
      }
    });
  }
  OnSubmitReturnOrder(orderId: number) {
    this.orderservice.returnOrder(orderId).subscribe({
      next: (response: any) => {
        console.log('Order returned successfully:', response);
        this.getOrders(this.currentPage); // Refresh the order list
      },
      error: (error: any) => {
        console.error('Error returning order:', error);
      }
    });
  }

     openMap(lat : number, lng: number  ) {
                  const url = `https://www.google.com/maps/search/?api=1&query=${lat},${lng}`;
                  window.open(url, "_blank", "noopener");
                }
  
}
