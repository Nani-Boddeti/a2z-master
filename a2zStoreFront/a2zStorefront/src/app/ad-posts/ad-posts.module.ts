import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AdListComponent } from './ad-list/ad-list.component';
import { AdDetailsComponent } from './ad-details/ad-details.component';
import { SubmitAdComponent } from './submit-ad/submit-ad.component';
import { OrdersModule } from '../orders/orders.module';
import { PaginationModule } from '../pagination/pagination-module';
import { SearchResults } from './search-results/search-results';
import { Categories } from './categories/categories';
import { CategoryDropdown } from './category-dropdown/category-dropdown';

@NgModule({
  declarations: [
    AdListComponent,
    AdDetailsComponent,
    SubmitAdComponent,
    SearchResults,
    Categories,
    CategoryDropdown
  ],
  imports: [
    CommonModule,
    HttpClientModule,
    OrdersModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule,
    PaginationModule
  ],
  exports:[AdListComponent]
})
export class AdPostsModule { }
