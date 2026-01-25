import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProfileComponent } from './profile/profile.component';
import { MyAdsComponent } from './my-ads/my-ads.component';
import { PaginationModule } from '../pagination/pagination-module';
import { ProfileUpdate } from './profile-update/profile-update';
import { ReactiveFormsModule } from '@angular/forms';



@NgModule({
  declarations: [ProfileComponent, MyAdsComponent, ProfileUpdate],
  imports: [
    CommonModule,
    PaginationModule,
    ReactiveFormsModule 
  ]
})
export class CustomersModule { }
