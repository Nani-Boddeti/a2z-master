import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { AbstractControl, FormBuilder, FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

import { RegistrationService } from './registration.service';
import { UserModel } from '../models/user.model';
import { CountryModel } from '../models/country.model';
import { AuthStateService } from '../services/auth-state.service';
import { OauthTokenService } from '../services/oauth-token.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-login-register',
  templateUrl: './login-register.component.html',
  styleUrl: './login-register.component.css',
})
export class LoginRegisterComponent implements OnInit {
  registrationForm: FormGroup;
  loginForm:FormGroup;
  passwordResetForm: FormGroup;
  isRegistered: boolean = false;
  isHome: boolean = true;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';
   isLoadingLocation = false;
showForgotPasswordForm = false;
registrationSuccessMessage = '';
registrationErrorMessage = '';
  
  constructor(
    private formBuilder: FormBuilder,
    private registrationService: RegistrationService,
    private authStateService: AuthStateService,
    private oauthTokenService: OauthTokenService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.registrationForm = this.formBuilder.group({
      userName: ['', [Validators.required, Validators.minLength(2)]],
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      line1: ['', [Validators.required, Validators.minLength(5)]],
      line2: ['', [Validators.required, Validators.minLength(5)]],
      apartment: ['', [Validators.required, Validators.minLength(5)]],
      district: ['', [Validators.required, Validators.minLength(3)]],
      latitude: ['',[Validators.required]],
      longitude: ['',[Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
      phoneNumber: [
        '',
        [Validators.required, Validators.pattern('^[0-9]{10}$')],
      ],
      //address: ['', [Validators.required, Validators.minLength(15)]],
      //isoCode: ['', [Validators.required, Validators.minLength(2)]],
      acceptTerms: [false, Validators.requiredTrue],
    },{ validators: passwordMatchValidator() });
    this.loginForm = this.formBuilder.group({
      userName: ['', [Validators.required, Validators.minLength(2)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
    this.passwordResetForm = this.formBuilder.group({
      userName: ['', [Validators.required, Validators.minLength(2)]]
    });
  }
  title: String = 'Hello test login component';
  country: CountryModel = {
    isoCode: '',
    region: '',
  };
  user!: UserModel;
  /*  loginPage :HTMLElement = this.oauth.getLoginPage(); */
  public isLoggedIn = false;
  public showLoginForm = true;

  ngAfterViewInit() {
     //this.login();
  }
  login() {
    // Generate PKCE code_verifier and code_challenge
    this.oauthTokenService.generatePKCEAsync();
  }

  loginV3(){
    this.showLoginForm = true;
    this.isHome = false;
    this.showForgotPasswordForm = false;
  }

  ngOnInit(): void {
    // Check if user is already logged in
    this.isLoggedIn = this.authStateService.getLoggedInStatus();
    this.initializeUser();
    
    // If user is logged in, show home page
    if (this.isLoggedIn) {
      this.isHome = true;
    }else {
      this.loginV3();
    }
    this.getLocationFromBrowser();
    this.route.queryParams.subscribe(params => {
      if (params['isRegistrationSuccess']=== 'true') {
        this.loginV3();
        this.registrationSuccessMessage = "Registration successful! Please log in.";
         setTimeout(() => {
          this.registrationSuccessMessage = '';
        }, 4000);
      }
      else if (params['isRegistrationSuccess']=== 'false') {
        this.loginV3();
        this.registrationErrorMessage = "There could be an issue with password reset. Please try again later.";
          setTimeout(() => {
          this.registrationErrorMessage = '';
        },4000);
      }
      if (params['isPasswordResetSuccess']=== 'true') {
        this.showForgotPasswordForm = false;
        this.loginV3();
        this.registrationSuccessMessage = "If the username exists, a password reset link has been sent to the associated email.";
         setTimeout(() => {
          this.registrationSuccessMessage = '';
        }, 4000); 
      } else if (params['isPasswordResetSuccess']=== 'false') {
        this.showForgotPasswordForm = false;
        this.loginV3();
        this.registrationErrorMessage = "There could be an issue with password reset. Please try again later.";
        setTimeout(() => {
          this.registrationErrorMessage = '';
        },4000);
      }
    });

  }

  /**
   * Exchange authorization code for access token
   * Sends code and code_verifier to backend
   */
  
  initializeUser() {
    this.user = {
      userName: '',
      firstName: '',
      lastName: '',
      email: '',
      phoneNumber: '',
      password: '',
      confirmPassword: '',
      defaultCountry: {
        isoCode: '',
        region: '',
      },defaultAddress: {
        apartment: '',
        country: this.country,
        customer: '',
        district: '',
        email: '',
        firstName: '',
        lastName: '',
        line1: '',
        line2: '',
        latitude: 0,
        longitude: 0,
        defaultAddress:true
      } 
    };
  }
  logout() {
    this.registrationService.logout().subscribe({
      next: (response) => {
        console.log('Logout successful:', response);
        this.isLoggedIn = false;
        this.isHome = true;
        this.showLoginForm = false;
        // Clear stored token
        localStorage.removeItem('authToken');
        // Reset forms
        this.loginForm.reset();
        this.registrationForm.reset();
        // Clear messages
        this.errorMessage = '';
        this.successMessage = '';
      },
      error: (error) => {
        console.error('Logout error:', error);
        // Even if logout fails on backend, clear local state
        this.isLoggedIn = false;
        this.isHome = true;
        this.showLoginForm = false;
        localStorage.removeItem('authToken');
        this.loginForm.reset();
        this.registrationForm.reset();
      }
    });
  }
  register() {
    this.isRegistered = false;
    this.isHome = false;
    this.showLoginForm = false;
    this.showForgotPasswordForm = false;
  }

  goHome() {
    this.isHome = true;
    this.showLoginForm = false;
    this.loginForm.reset();
    this.errorMessage = '';
    this.successMessage = '';
  }

  get f() {
    return this.registrationForm.controls;
  }

  get lf() {
    return this.loginForm.controls;
  }

  getLocationFromBrowser(): void {
    if (!navigator.geolocation) {
      this.errorMessage = 'Geolocation is not supported by your browser';
      return;
    }

    this.isLoadingLocation = true;
    console.log('Requesting geolocation permission...');

    navigator.geolocation.getCurrentPosition(
      (position) => {
        const lat = position.coords.latitude;
        const lon = position.coords.longitude;
        
        console.log('Location obtained:', { latitude: lat, longitude: lon });
        
        // Set latitude and longitude in form
        this.registrationForm.patchValue({
          latitude: lat.toFixed(6),
          longitude: lon.toFixed(6)
        });
        
        this.isLoadingLocation = false;
        this.successMessage = 'Location detected successfully!';
        
        // Clear success message after 3 seconds
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      (error) => {
        this.isLoadingLocation = false;
        
        switch(error.code) {
          case error.PERMISSION_DENIED:
            this.errorMessage = 'Location permission denied. Please enable location access in your browser settings.';
            break;
          case error.POSITION_UNAVAILABLE:
            this.errorMessage = 'Location information is unavailable.';
            break;
          case error.TIMEOUT:
            this.errorMessage = 'The request to get location timed out.';
            break;
          default:
            this.errorMessage = 'Failed to get your location. You can enter it manually.';
        }
        
        console.error('Geolocation error:', error);
      }
    );
  }
  onSubmit() {
    if (this.registrationForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    if (this.registrationForm.valid) {
      // Map form values to user model
      this.user = {
        userName: this.registrationForm.value.userName,
        firstName: this.registrationForm.value.firstName,
        lastName: this.registrationForm.value.lastName,
        email: this.registrationForm.value.email,
        phoneNumber: this.registrationForm.value.phoneNumber,
        password: this.registrationForm.value.password,
        confirmPassword: this.registrationForm.value.confirmPassword,
        defaultCountry: {
          isoCode: 'IN',
          region: '',
        },defaultAddress: {
          apartment: this.registrationForm.value.apartment,
          country: {
          isoCode: 'IN',
          region: '',
        },  
          customer: '',
          district: this.registrationForm.value.district,
          email: this.registrationForm.value.email,
          firstName: this.registrationForm.value.firstName,
          lastName: this.registrationForm.value.lastName,
          line1: this.registrationForm.value.line1,
          line2: this.registrationForm.value.line2,
          latitude: this.registrationForm.value.latitude,
          longitude: this.registrationForm.value.longitude,
          defaultAddress: true,
        }
      };
      this.registrationService.signUp(this.user).subscribe({
        next: (response) => {
          this.successMessage = 'Registration successful!';
          this.isRegistered = true;
          this.isHome = true;
          // Reset form
          //this.registrationForm.reset();
          this.isSubmitting = false;
          this.successMessage = 'Registration successful! Please log in.';
          this.loginV3();
          this.registrationForm.reset();
          this.reloadLoginPage('isRegistrationSuccess',true );
          //this.router.navigate(['/loginV3']);
        },
        error: (error) => {
          this.errorMessage =
            error.error?.message || 'Registration failed. Please try again.';
          console.error('Registration error:', error);
          this.isSubmitting = false;
          
        }
      });
    }
  }

   onLoginV3Submit() {
    if (this.loginForm.valid){
 const formData = new FormData();
     formData.append('username', this.loginForm.value.userName);
     formData.append('password', this.loginForm.value.password);
     this.registrationService.login(formData).subscribe({
       next: (response: any ) => {if(response.success===true){
        this.login();
       }},
      error: (error) => {
          //console.error('Login failed')
          this.errorMessage = 'Login failed. Please check your credentials and try again.';
           setTimeout(() => {
          this.errorMessage = '';
        }, 3000);
        }
    });
    }
    

    // POST to Spring's processing URL (triggers UsernamePasswordAuthenticationFilter)
    
  }

  onClickForgotPassword() {
    this.showForgotPasswordForm = true;
  }
  onSubmitPasswordReset(): void {
    const userNameValue = this.passwordResetForm.value.userName;
    this.registrationService.getForgotPasswordFromEmailLink(userNameValue).subscribe({
      next: (response) => {
        this.showForgotPasswordForm = false;
   // this.router.navigate(['/loginV3']);
   this.reloadLoginPage('isPasswordResetSuccess',true );
   this.successMessage = 'If the username exists, a password reset link has been sent to the associated email.';

      },
      error: (error) => {
        this.showForgotPasswordForm = false;
   // this.router.navigate(['/loginV3']);
   this.reloadLoginPage('isPasswordResetSuccess',true );
   this.successMessage = 'If the username exists, a password reset link has been sent to the associated email.';

      }
    });
      }
reloadLoginPage(paramName: string = 'isRegistrationSuccess',data:boolean=false){ 
  this.router.navigateByUrl('/', { skipLocationChange: true })
    .then(() => this.router.navigate(['/loginV3'], { queryParams: {   [paramName]: data } }));
}
get password() { return this.passwordResetForm.get('password'); }
get confirmPassword() { return this.passwordResetForm.get('confirmPassword'); }
}
export function passwordMatchValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;
    
    if (!password || !confirmPassword) return null;
    
    return password === confirmPassword ? null : { passwordMismatch: true };
  };
}