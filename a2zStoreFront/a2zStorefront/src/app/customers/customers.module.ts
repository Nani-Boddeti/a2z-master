import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileComponent } from './profile/profile.component';
import { MyAdsComponent } from './my-ads/my-ads.component';
import { PaginationModule } from '../pagination/pagination-module';



@NgModule({
  declarations: [ProfileComponent, MyAdsComponent],
  imports: [
    CommonModule,
    PaginationModule
  ]
})
export class CustomersModule { }
