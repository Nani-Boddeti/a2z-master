import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RegistrationService } from './registration.service';
import { UserModel } from '../models/user.model';
import { CountryModel } from '../models/country.model';
import { AuthStateService } from '../services/auth-state.service';
import { OauthTokenService } from '../services/oauth-token.service';

@Component({
  selector: 'app-login-register',
  templateUrl: './login-register.component.html',
  styleUrl: './login-register.component.css',
})
export class LoginRegisterComponent implements OnInit {
  registrationForm: FormGroup;
  loginForm:FormGroup;
  isRegistered: boolean = false;
  isHome: boolean = true;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';
  

  
  constructor(
    private formBuilder: FormBuilder,
    private registrationService: RegistrationService,
    private authStateService: AuthStateService,
    private oauthTokenService: OauthTokenService,
  ) {
    this.registrationForm = this.formBuilder.group({
      userName: ['', [Validators.required, Validators.minLength(2)]],
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
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
    });
    this.loginForm = this.formBuilder.group({
      username: ['', [Validators.required, Validators.minLength(2)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
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
  public showLoginForm = false;

  ngAfterViewInit() {
     //this.login();
  }
  login() {
    // Generate PKCE code_verifier and code_challenge
    this.oauthTokenService.generatePKCEAsync();
  }

  

  ngOnInit(): void {
    // Check if user is already logged in
    this.isLoggedIn = this.authStateService.getLoggedInStatus();
    this.initializeUser();
    
    // If user is logged in, show home page
    if (this.isLoggedIn) {
      this.isHome = true;
    }
    
    
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
      },
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
        },
      };
      this.registrationService.signUp(this.user).subscribe({
        next: (response) => {
          this.successMessage = 'Registration successful!';
          this.isRegistered = true;
          this.isHome = true;
          // Reset form
          this.registrationForm.reset();
          this.isSubmitting = false;
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

  

}
