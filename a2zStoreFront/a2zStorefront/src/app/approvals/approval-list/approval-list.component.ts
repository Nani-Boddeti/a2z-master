import { Component, OnInit } from '@angular/core';
import { AdService } from '../../services/ad.service';
import { AdPostUtilService } from '../../ad-posts/ad-post-util.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApprovalRequestSubmitModel } from '../../models/approvalRequestSubmit.model';

@Component({
  selector: 'app-approval-list',
  templateUrl: './approval-list.component.html',
  styleUrls: ['./approval-list.component.css'],
})
export class ApprovalListComponent implements OnInit {
  approvalRequestsList: any;
approvalRequestModel:ApprovalRequestSubmitModel=new ApprovalRequestSubmitModel(0,'');
  constructor(private adService: AdService,private adPostUtilService: AdPostUtilService) {
  }

  ngOnInit(): void {}
  ngAfterViewInit() {
    this.approvalRequests();
  }
  approvalRequests(): void {
    this.adService.getApprovalRequests().subscribe((data: any) => {
      console.log('User Profile Data:', data);
      this.approvalRequestsList = data;
    });
  }

   viewAd(newValue: number) {
    this.adPostUtilService.triggerClick(newValue);

  }
  approveRequest(requestId: number): void {
    this.approvalRequestModel.id = requestId;
    this.approvalRequestModel.status = 'APPROVED';
    this.adService.submitApprovalRequest(JSON.stringify(this.approvalRequestModel)).subscribe(
      (response) => {
        console.log('Approval successful:', response);
        // Refresh the approval requests list after approval
        this.approvalRequests();
      },
      (error) => {
        console.error('Error approving request:', error);
      }
    );
    }
    
  

  rejectRequest(requestId: number): void {
    this.approvalRequestModel.id = requestId;
    this.approvalRequestModel.status = 'REJECTED';

    this.adService.submitApprovalRequest(JSON.stringify(this.approvalRequestModel)).subscribe(
      (response) => {
        console.log('Rejection successful:', response);
        // Refresh the approval requests list after rejection
        this.approvalRequests();
      },
      (error) => {
        console.error('Error rejecting request:', error);
      }
    );
  } 
}
