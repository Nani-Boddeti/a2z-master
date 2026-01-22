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
  constructor(private customerService : CustomerService,private router: Router) { }

  ngOnInit(): void {
   
  }
  ngAfterViewInit() {
     this.getProfileData();
  }
  getProfileData(): void {
     this.customerService.getProfileData().subscribe({
    next: (profileData) => {
      // Use emitted profile data
      this.userData = profileData;
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
}
}
