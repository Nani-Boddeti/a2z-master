import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AdListComponent } from './ad-list/ad-list.component';
import { AdDetailsComponent } from './ad-details/ad-details.component';
import { SubmitAdComponent } from './submit-ad/submit-ad.component';
import { OrdersModule } from '../orders/orders.module';

@NgModule({
  declarations: [
    AdListComponent,
    AdDetailsComponent,
    SubmitAdComponent
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    OrdersModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule
  ],
  exports:[AdListComponent]
})
export class AdPostsModule { }
