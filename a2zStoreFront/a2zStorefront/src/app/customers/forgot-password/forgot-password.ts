import { Component, Input } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { Subject } from 'rxjs/internal/Subject';
import { CustomerService } from '../../services/customer-service';
import { ActivatedRoute, Router } from '@angular/router';
import { takeUntil } from 'rxjs/internal/operators/takeUntil';
import { RegistrationService } from '../../login-register/registration.service';

@Component({
  selector: 'app-forgot-password',
  standalone: false,
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  @Input()
  customerData: any = null;
  isSubmitting = false;
  errorMessage: string = '';
  successMessage: string = '';
  token: string = '';
  private destroy$ = new Subject<void>();

  userName: string = '';
  forgotPasswordForm!: FormGroup;
  ngOnInit(): void {
    this.initializeForm();
    this.route.queryParams.subscribe(params => {
      if (params['token']) {
        this.token = params['token'];
        console.log('Token received:', this.token);
      }
      console.log('Query params:', params);
    });
    console.log('ForgotPassword component initialized');
  }

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private customerService: CustomerService,
    private router: Router,
    private registrationService: RegistrationService,
  ) {}
  initializeForm(): void {
    this.forgotPasswordForm = this.formBuilder.group({
      // Address details
      password: [
        '',
        {
          validators: [Validators.required],
          updateOn: 'change',
        },
      ],
      confirmPassword: [
        '',
        {
          validators: [Validators.required],
          updateOn: 'change',
        },
      ],
    },{ validators: passwordMatchValidator() } );
  }
  get f() {
    return this.forgotPasswordForm.controls;
  }

  //form Submit
  onsubmitPasswordReset(): void {
    console.log('onsubmitPasswordReset called');
    console.log('Form valid:', this.forgotPasswordForm.valid);
    console.log('Form errors:', this.forgotPasswordForm.errors);
    console.log('Token:', this.token);
    
    if (!this.token) {
      console.error('No token found. Cannot submit password reset.');
      this.errorMessage = 'Invalid request. Token is missing.';
      return;
    }

    const updatePasswordPayload = {
      newPassword: this.forgotPasswordForm.get('password')?.value,
      token: this.token,
    };
    console.log('Payload:', updatePasswordPayload);
    
    this.isSubmitting = true;
    this.registrationService.submitForgotPasswordFrom(updatePasswordPayload)
      .subscribe({
        next: (response) => {
          console.log('Profile updated successfully:', response);
          this.successMessage = 'Profile updated successfully!';
          this.isSubmitting = false;
           
          // Redirect to ad list after 2 seconds
          setTimeout(() => {
            this.router.navigate(['/loginV3']);
          }, 3000);
        },
        error: (error) => {
          console.log('Profile update failed:', error);
          this.isSubmitting = false;
          this.errorMessage = error?.error?.message || 'An error occurred while submitting the form.';
          setTimeout(() => {
            this.router.navigate(['/loginV3']);
          }, 3000);
        },
      });
  }
  
get password() { return this.forgotPasswordForm.get('password'); }
get confirmPassword() { return this.forgotPasswordForm.get('confirmPassword'); }





}
export function passwordMatchValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;
    
    if (!password || !confirmPassword) return null;

    
    return password === confirmPassword ? null : { passwordMismatch: true };
  };
}