import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { AdListComponent } from './ad-list/ad-list.component';



@NgModule({
  declarations: [
    AdListComponent
  ],
  imports: [
    CommonModule,
    HttpClientModule
  ],
  exports:[AdListComponent]
})
export class AdPostsModule { }
