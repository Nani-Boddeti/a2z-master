import { NgModule, Injectable } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrderModel } from '../models/order.model';

@Injectable({
  providedIn: 'root',
})
export class OrderServiceService {
  private apiUrl = '/api/order/submit';
  constructor(private http: HttpClient) {}
  placeOrder(data: OrderModel): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post(this.apiUrl, data, { headers });
  }

  getMyAdOrders(page: number, size: number): Observable<any> {
    return this.http.get(`/api/myAccount/myAdOrders?page=${page}&size=${size}`);
  }
  
  getOrderTypesList(): Observable<any> {
    return this.http.get('/api/order/allTypes');
  }
  getOrderStatusList(): Observable<any> {
    return this.http.get('/api/order/allOrderStatuses');
  }

  extendOrder(orderId: number): Observable<any> {
    return this.http.get(`/api/order/return`, {params: { orderId: orderId ,isReturn:false,isExtend:true}});
  }
   returnOrder(orderId: number): Observable<any> {
    return this.http.get(`/api/order/return`, {params: { orderId: orderId ,isReturn:true,isExtend:false}});
  }

  getMyAdOrdersByStatus(page: number, size: number, status: string): Observable<any> {
    return this.http.get(`/api/myAccount/myAdOrders/status?status=${status}&page=${page}&size=${size}`);
  }
}
