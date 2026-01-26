import { Injectable } from '@angular/core';
import { EMPTY } from 'rxjs/internal/observable/empty';
import { catchError } from 'rxjs/internal/operators/catchError';
import { tap } from 'rxjs/internal/operators/tap';
import { Observable } from 'rxjs/internal/Observable';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { RegistrationService } from '../login-register/registration.service';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  
  private profileDataSubject = new BehaviorSubject<any>(null);
public profileData$ = this.profileDataSubject.asObservable();
  constructor(private registrationService : RegistrationService, private router: Router, private http: HttpClient) { }

  getProfileData(): Observable<any> {
    console.log('🔄 CustomerService.getProfileData() called');
    return this.registrationService.getUserProfile().pipe(
      tap((profileData) => {
        console.log('✅ Profile data from API:', profileData);
        this.profileDataSubject.next(profileData);
      }),
      catchError((error) => {
        console.error('❌ Error fetching user profile:', error);
        console.error('Status:', error?.status);
        console.error('Message:', error?.message);
        console.error('Response body:', error?.error);
        this.router.navigate(['/loginV3']);
        return EMPTY;
      })
    );
  }
  updateProfileData(profileData: any): Observable<any> {
    console.log('🔄 CustomerService.updateProfileData() called');
    return this.registrationService.updateProfile(profileData).pipe(
      tap((updatedProfileData) => {
        console.log('✅ Updated profile data from API:', updatedProfileData);
        this.profileDataSubject.next(updatedProfileData);
      }),
      catchError((error) => {
        console.error('❌ Error fetching user profile:', error);
        console.error('Status:', error?.status);
        console.error('Message:', error?.message);
        console.error('Response body:', error?.error);
        this.router.navigate(['/profile']);
        return EMPTY;
      })
    );
  }

   submitForgotPasswordFrom(data: any): Observable<any> {
    console.log('🔄 CustomerService.updateProfileData() called');
    return this.registrationService.submitForgotPasswordFrom(data);
  }

  getMyAdList(page:number,size:number){
  return this.http.get<any>("/api/myAccount/myAds?pageNo=" + page + "&pageSize=" + size);
 }
}
