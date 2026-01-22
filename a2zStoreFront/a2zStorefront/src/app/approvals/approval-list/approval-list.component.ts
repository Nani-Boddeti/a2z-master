import { Component, OnInit } from '@angular/core';
import { AdService } from '../../services/ad.service';
import { AdPostUtilService } from '../../ad-posts/ad-post-util.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApprovalRequestSubmitModel } from '../../models/approvalRequestSubmit.model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-approval-list',
  templateUrl: './approval-list.component.html',
  styleUrls: ['./approval-list.component.css'],
})
export class ApprovalListComponent implements OnInit {
  approvalRequestsList: any;
    currentIndex = 0;
       currentPage = 1;
  totalPages = 10;
  totalItems = 100;
  itemsPerPage = 10;
approvalRequestModel:ApprovalRequestSubmitModel=new ApprovalRequestSubmitModel(0,'');
  constructor(private adService: AdService,private adPostUtilService: AdPostUtilService, private router: Router) {
  }

  ngOnInit(): void {}
  ngAfterViewInit() {
    this.approvalRequests();
    //  setInterval(() => {
    //   if (this.selectedItem?.mediaContainerData?.medias?.length > 1) {
    //     this.currentIndex = (this.currentIndex + 1) % this.selectedItem.mediaContainerData.medias.length;
    //   }
    // }, 4000);
  }
  approvalRequests(page: number = 1): void {
    this.adService.getApprovalRequests(page, this.itemsPerPage).subscribe((data: any) => {
      console.log('User Profile Data:', data);
      this.approvalRequestsList = data.approvalRequestDataList;
      this.totalPages = data.totalPages;
      this.currentPage = data.currentPage + 1; 
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
        this.router.navigate(['/loginV3']);
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
        this.router.navigate(['/loginV3']);
      }
    );
  } 
  changePageSize(newSize: number) {
    this.itemsPerPage = newSize;
    this.approvalRequests(1); // Reload items with new page size, starting from page 1
  }
}
