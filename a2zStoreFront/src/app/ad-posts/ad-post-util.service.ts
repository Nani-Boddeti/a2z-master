import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdPostUtilService {

  constructor(private http: HttpClient) { }
  adsList : any;
 getAdList(){
   this.http.get<JSON>("/search/all").subscribe(ads=>this.adsList=ads);
  return this.adsList;
 }

}
