import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { takeUntil } from 'rxjs/internal/operators/takeUntil';
import { Subject } from 'rxjs/internal/Subject';
import { CustomerService } from '../../services/customer-service';
@Component({
  selector: 'app-profile-update',
  standalone: false,
  templateUrl: './profile-update.html',
  styleUrl: './profile-update.css'
})
export class ProfileUpdate {
  @Output() profileUpdated = new EventEmitter<any>();  // ✅ Send data back
  @Input() 
  customerData: any = null;  
   private destroy$ = new Subject<void>();
    isSubmitting = false;
    errorMessage: string = '';
    successMessage: string = '';
    userName: string = '';
updateProfileForm!: FormGroup;
   ngOnInit(): void {
    this.initializeForm();
         if (this.customerData && this.updateProfileForm) {                  
              this.updateProfileForm.patchValue({
              firstName: this.customerData?.firstName || '',
              lastName: this.customerData?.lastName || '',
              email: this.customerData?.email || '',
              phoneNumber: this.customerData?.phoneNumber|| ''
            });
            }
    
           


  }

    constructor(private formBuilder: FormBuilder,private route: ActivatedRoute,
      private customerService:CustomerService,private router: Router) {}
  initializeForm(): void {
      this.updateProfileForm = this.formBuilder.group({
         
        // Address details
        firstName: [
          '',
          {
            validators: [Validators.required],
            updateOn: 'change',
          },
        ],
  
        lastName: [
          '',
          {
            validators: [Validators.required],
            updateOn: 'change',
          },
        ],
  
        email: [
         '',
          {
            validators: [Validators.required, Validators.email],
            updateOn: 'change',
          },
        ],
  
        phoneNumber: [
          '',
          {
            validators: [Validators.required, Validators.minLength(5)],
            updateOn: 'change',
          },
        ],
      });
    }

    updateProfile(): void {
    this.updateProfileForm.markAllAsTouched();
    if (this.updateProfileForm.invalid) {
      this.errorMessage = 'Please fill in all required fields correctly';
      return;
    }

    
    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    // Create address object
    const profileUpdateData = {
      userName: this.customerData?.userName || '',
      firstName: this.updateProfileForm.get('firstName')?.value,
      lastName: this.updateProfileForm.get('lastName')?.value,
      email: this.updateProfileForm.get('email')?.value,
      phoneNumber: this.updateProfileForm.get('phoneNumber')?.value,
    };


    // Submit ad with media files
    this.customerService
      .updateProfileData(profileUpdateData)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          console.log('Profile updated successfully:', response);
          this.successMessage = 'Profile updated successfully!';
          this.isSubmitting = false;
           this.profileUpdated.emit({
            success: true,
            data: this.updateProfileForm.value,
            response: response
          });
          // Redirect to ad list after 2 seconds
          setTimeout(() => {
            this.router.navigate(['/profile']);
          }, 3000);
        },
        error: (error) => {
          console.log('Profile update failed:', error);
          this.isSubmitting = false;

          // Check for validation violations from backend
          if (
            error?.error?.violations &&
            Array.isArray(error.error.violations)
          ) {
            const violationMessages = error.error.violations
              .map((v: any) => `${this.capitalizeFirst(v.fieldName)}`)
              .join('\n');
            this.errorMessage = `Validation errors in fields :\n${violationMessages}`;
          } else {
            this.errorMessage =
              error?.error?.message || 'Failed to update profile. Please try again.';
          }

          // Scroll to top to show error message
          this.scrollToTop();

          // Only redirect if there are no validation errors (allow user to fix and retry)
          if (
            !error?.error?.violations ||
            error.error.violations.length === 0
          ) {
            setTimeout(() => {
              this.router.navigate(['/profile']);
            }, 3000);
          }
        },
      });
  }
   scrollToTop(): void {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
  capitalizeFirst(str: string): string {
    return str.charAt(0).toUpperCase() + str.slice(1);
  }
}
