import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderDetailComponent } from './order-detail/order-detail.component';
import { OrdersListComponent } from './orders-list/orders-list.component';
import { OrderConfirmComponent } from './order-confirm/order-confirm.component';
import { CustomerOrdersComponent } from './customer-orders/customer-orders.component';



@NgModule({
  declarations: [
    OrderDetailComponent,
    OrdersListComponent,
    OrderConfirmComponent,
    CustomerOrdersComponent
  ],
  exports: [OrderDetailComponent,
    OrdersListComponent,
    OrderConfirmComponent,
    CustomerOrdersComponent],
  imports: [
    CommonModule
  ]
})
export class OrdersModule { }
