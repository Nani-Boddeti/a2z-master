import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Pagination } from './pagination';



@NgModule({
  declarations: [
    Pagination
  ],
  imports: [
    CommonModule
  ],
  exports: [
    Pagination
  ]
})
export class PaginationModule { }
