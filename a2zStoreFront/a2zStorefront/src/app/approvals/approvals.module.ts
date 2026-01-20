import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ApprovalsRoutingModule } from './approvals-routing.module';
import { ApprovalListComponent } from './approval-list/approval-list.component';


@NgModule({
  declarations: [
    ApprovalListComponent
  ],
  imports: [
    CommonModule,
    ApprovalsRoutingModule
  ]
})
export class ApprovalsModule { }
