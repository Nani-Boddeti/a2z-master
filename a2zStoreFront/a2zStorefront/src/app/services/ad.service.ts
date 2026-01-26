import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdService {
  private adPostUrl = '/api/ad/post';

  constructor(private http: HttpClient) { }

  /**
   * Post an advertisement
   * @param adData - Advertisement data with mediaContainerData containing the media id
   * @returns Observable with the created ad response
   */
  postAd(adData: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post(this.adPostUrl, adData, { headers });
  }

  /**
   * Build ad data object in the correct format for the API
   */
  buildAdData(
    productName: string,
    description: string,
    price: number,
    currency: string,
    mediaContainerId: string,
    address: any,
    userName: string,
    isActive: boolean = true
  ): any {
    return {
      description,
      mediaContainerData: {
        code: mediaContainerId
      },
      isActive,
      productName,
      customer: {
        userName
      },
      price: {
        currency,
        amount: price.toFixed(2)
      },
      sourceAddress: {
        latitude: address.latitude,
        longitude: address.longitude,
        firstName: address.firstName,
        lastName: address.lastName,
        line1: address.line1,
        line2: address.line2 || '',
        apartment: address.apartment || '',
        country: {
          isoCode: address.country?.isoCode || 'IND',
          region: address.country?.region || 'Asia'
        },
        district: address.district || '',
        email: address.email,
        customer: userName
      }
    };
  }

  getApprovalRequests(page: number, size: number): Observable<any> {
    return this.http.get(`/api/myAccount/approvalRequests?page=${page}&size=${size}`);
  }
  submitApprovalRequest(approvalRequestForm: any): Observable<any> {
    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post('/api/myAccount/approvalRequests/update', approvalRequestForm, { headers });
  }

 
}
