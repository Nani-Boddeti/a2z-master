import { Component, OnInit } from '@angular/core';
import { RegistrationService } from '../../login-register/registration.service';
import { registerLocaleData } from '@angular/common';
import { Router } from '@angular/router';
import { CustomerService } from '../../services/customer-service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
userData :any;
userGroupName: string = '';
successMessage = '';
  errorMessage = '';
updateProfileVisible: boolean = false;
  constructor(private customerService : CustomerService,private router: Router) { }

  ngOnInit(): void {
   this.updateProfileVisible = false;
  }
  ngAfterViewInit() {
     this.getProfileData();
  }
  getProfileData(): void {
     this.customerService.getProfileData().subscribe({
    next: (profileData) => {
      // Use emitted profile data
      this.userData = profileData;
      if(profileData.userGroupNames)
      this.userGroupName = profileData.userGroupNames[0];
    },
    error: (error) => {
      console.error('Error fetching user profile:', error);
      this.router.navigate(['/loginV3']);
    }
  });
  }
updateProfile(): void {
  // Implement profile update logic here
  console.log('Update profile clicked');
  this.updateProfileVisible = true;
  // this.router.navigate(['/profile-update'], { 
  //   state: { customerData: this } 
  // });
}
  // ✅ Receive updated data from child
  onProfileUpdated(event: any) {
    console.log('Profile updated:', event.data);
    
    if (event.success) {
      // Update local data
      this.scrollToTop();
      this.successMessage = 'Profile updated successfully.';
     this.updateProfileVisible = false;
      this.userData = event.data;
    }
  }
  scrollToTop(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}
