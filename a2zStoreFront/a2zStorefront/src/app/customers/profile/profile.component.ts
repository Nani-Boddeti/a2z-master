import { Component, OnInit } from '@angular/core';
import { RegistrationService } from '../../login-register/registration.service';
import { registerLocaleData } from '@angular/common';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
userData :any;
  constructor(private registrationService : RegistrationService) { }

  ngOnInit(): void {
   
  }
  ngAfterViewInit() {
     this.getProfileData();
  }
  getProfileData(): void {
    this.registrationService.getUserProfile().subscribe((data:any)=>{
  console.log('User Profile Data:', data);
  this.userData = data;
}); 
  }
updateProfile(): void {
  // Implement profile update logic here
  console.log('Update profile clicked');  
}
}
