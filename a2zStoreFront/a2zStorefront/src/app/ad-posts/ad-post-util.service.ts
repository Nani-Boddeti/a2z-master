import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdPostUtilService {

  constructor(private http: HttpClient) { }
  adsList : any;

 getAdById(id:number){
  return this.http.get<any>(`/api/ad/view/${id}`);
 }

 postAd(){
  
 }
 private callClickSource = new Subject<number>();

  callClick$ = this.callClickSource.asObservable();

  triggerClick(id:number) {
    this.callClickSource.next(id);
  }
  
}
