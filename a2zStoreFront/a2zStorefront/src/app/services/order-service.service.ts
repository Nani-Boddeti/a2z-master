import { NgModule, Injectable } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrderModel } from '../models/order.model';

@Injectable({
  providedIn: 'root',
})
export class OrderServiceService {
  private apiUrl = '/order/submit';
  constructor(private http: HttpClient) {}
  placeOrder(data: OrderModel): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post(this.apiUrl, data, { headers });
  }

  getMyAdOrders(): Observable<any> {
    return this.http.get('/myAccount/myAdOrders');
  }
}
