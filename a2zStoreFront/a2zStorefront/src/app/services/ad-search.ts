import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AdSearch {
 
  constructor(private http: HttpClient) { }

    getAdList(page:number,size:number){
  return this.http.get<JSON>("/search/all?pageNo=" + page + "&pageSize=" + size);
 }

   searchAdList(q:string,page:number,size:number,latitude:number,longitude:number){
  return this.http.get<JSON>("/search/?query=" + q + "&pageNo=" + page + "&pageSize=" + size + "&latitude=" + latitude + "&longitude=" + longitude);
 }
  searchAdListWithCategoryCode(q:string,page:number,size:number){
  return this.http.get<JSON>("/search/category/" + q + "&?pageNo=" + page + "&pageSize=" + size);
 }

 getListedCategories(){
  return this.http.get<JSON>("/c/all");
 }
}
