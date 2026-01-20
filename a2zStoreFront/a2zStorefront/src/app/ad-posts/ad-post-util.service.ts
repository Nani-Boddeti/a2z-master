import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdPostUtilService {

  constructor(private http: HttpClient) { }
  adsList : any;
 getAdList(){
  return this.http.get<JSON>("/search/all");
 }
 getAdById(id:number){
  return this.http.get<JSON>(`/ad/view/${id}`);
 }

 postAd(){
  
 }
 private callClickSource = new Subject<number>();

  callClick$ = this.callClickSource.asObservable();

  triggerClick(id:number) {
    this.callClickSource.next(id);
  }
  
}
