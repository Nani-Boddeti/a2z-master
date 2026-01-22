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

     this.registrationService.getUserProfile().subscribe({
      next: (profileData) => {
        console.log('📡 EMITTING:', profileData);
        this.profileDataSubject.next(profileData);  // ✅ Emit to subscribers
      },
      error: (error) => {
        console.error('Error fetching user profile:', error);
        this.router.navigate(['/loginV3']);
        return EMPTY;  // Don't emit error downstream
      }
    })
return this.profileDataSubject.asObservable();

  // return this.registrationService.getUserProfile().pipe(
  //   tap(data => {
  //     console.log('📡 EMITTING:', data);
  //     this.profileDataSubject.next(data);  // ✅ Emit to subscribers
  //   }),
  //   catchError(error => {
  //     console.error('Error fetching user profile:', error);
  //     this.router.navigate(['/loginV3']);
  //     return EMPTY;  // Don't emit error downstream
  //   })
  // );
}
  getMyAdList(page:number,size:number){
  return this.http.get<JSON>("/myAccount/myAds?pageNo=" + page + "&pageSize=" + size);
 }
}
