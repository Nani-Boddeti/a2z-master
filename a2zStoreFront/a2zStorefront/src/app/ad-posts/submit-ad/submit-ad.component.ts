import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AdSubmissionService } from '../../services/ad-submission.service';
import { AuthStateService } from '../../services/auth-state.service';
import { Router } from '@angular/router';
import { AdSearch } from '../../services/ad-search';
import { CustomerService } from '../../services/customer-service';

@Component({
  selector: 'app-submit-ad',
  templateUrl: './submit-ad.component.html',
  styleUrl: './submit-ad.component.css'
})
export class SubmitAdComponent implements OnInit {
  submitAdForm!: FormGroup;
  selectedFiles: File[] = [];
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';
  userName: string = '';
  isLoadingLocation = false;
  isDragOver = false;
 selectedCategory: string = 'ALL';
  searchQuery: string = '';
categories: any[] = [];

  // ADD THESE METHODS
  onCategoryChange() {
    console.log('Category changed:', this.selectedCategory);
    // Trigger your existing filter/search logic
  }
  constructor(
    private formBuilder: FormBuilder,
    private adSubmissionService: AdSubmissionService,
    private authStateService: AuthStateService,
    private router: Router,
    private adSearch: AdSearch,
    private customerService : CustomerService
  ) {
    
  }

  ngOnInit(): void {
    // Get logged-in user's name
    this.authStateService.userName$.subscribe(userName => {
      this.userName = userName;
    });

    this.initializeForm();
    this.loadCategories();
    // Get location from browser
    this.getLocationFromBrowser();
    this.getProfileData();
  }

  getProfileData(): void {
     this.customerService.getProfileData().subscribe({
    next: (profileData) => {
      // Use emitted profile data
       this.submitAdForm.patchValue({ firstName: profileData?.firstName || '' });
        this.submitAdForm.patchValue({ lastName: profileData?.lastName || '' });
        this.submitAdForm.patchValue({ email: profileData?.email || '' });
        this.submitAdForm.patchValue({ line1: profileData?.defaultAddress?.line1 || '' });
        this.submitAdForm.patchValue({ line2: profileData?.defaultAddress?.line2 || '' });
        this.submitAdForm.patchValue({ apartment: profileData?.defaultAddress?.apartment || '' });
        this.submitAdForm.patchValue({ district: profileData?.defaultAddress?.district || '' });

    },
    error: (error) => {
      console.error('Error fetching user profile:', error);
      this.router.navigate(['/loginV3']);
    }
  });
  }

  myForm = this.formBuilder.group({
    name: [''],
    category: ['ALL']  // ✅ Works like any input!
  });

  onSubmit() {
    console.log(this.myForm.value);  // { name: '...', category: 'FURNITURE' }
  }

  loadCategories(){
this.adSearch.getListedCategories().subscribe((data: any) => {

      this.categories = data;

    });
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
        this.submitAdForm.patchValue({
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

  initializeForm(): void {
    this.submitAdForm = this.formBuilder.group({
      productName: ['', [Validators.required, Validators.minLength(3)]],
      category: [this.myForm?.value?.category, Validators.required],
      description: ['', [Validators.required, Validators.minLength(10)]],
      price: ['', [Validators.required, Validators.pattern('^[0-9]+(\\.[0-9]{1,2})?$')]],
      currency: ['INR', Validators.required],
      isActive: [true],
      // Address details
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      line1: ['', [Validators.required, Validators.minLength(5)]],
      line2: [''],
      apartment: [''],
      district: ['', Validators.required],
      isoCode: ['IND', Validators.required],
      region: ['Asia', Validators.required],
      latitude: ['', Validators.required],
      longitude: ['', Validators.required]
    });
  }

  onFileSelected(event: any): void {
    const files: FileList = event.target.files;
    this.selectedFiles = [];
    
    if (files && files.length > 0) {
      // Allow multiple files
      for (let i = 0; i < files.length; i++) {
        this.selectedFiles.push(files[i]);
      }
      console.log('Files selected:', this.selectedFiles.length);
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = true;
    console.log('Drag over');
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;
    console.log('Drag leave');
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;
    
    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.selectedFiles = [];
      for (let i = 0; i < files.length; i++) {
        this.selectedFiles.push(files[i]);
      }
      console.log('Files dropped:', this.selectedFiles.length);
    }
  }

  triggerFileInput(fileInput: HTMLInputElement): void {
    fileInput.click();
  }

  submitAd(): void {
    if (this.submitAdForm.invalid) {
      this.errorMessage = 'Please fill in all required fields correctly';
      return;
    }

    if (this.selectedFiles.length === 0) {
      this.errorMessage = 'Please select at least one media file';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    // Create address object
    const address = {
      firstName: this.submitAdForm.get('firstName')?.value,
      lastName: this.submitAdForm.get('lastName')?.value,
      email: this.submitAdForm.get('email')?.value,
      line1: this.submitAdForm.get('line1')?.value,
      line2: this.submitAdForm.get('line2')?.value,
      apartment: this.submitAdForm.get('apartment')?.value,
      district: this.submitAdForm.get('district')?.value,
      latitude: parseFloat(this.submitAdForm.get('latitude')?.value),
      longitude: parseFloat(this.submitAdForm.get('longitude')?.value),
      country: {
        isoCode: this.submitAdForm.get('isoCode')?.value,
        region: this.submitAdForm.get('region')?.value
      }
    };

    // Create ad submission payload
    const adPayload = this.adSubmissionService.createAdSubmissionPayload(
      this.submitAdForm.get('productName')?.value,
      this.submitAdForm.get('category')?.value,
      this.submitAdForm.get('description')?.value,
      parseFloat(this.submitAdForm.get('price')?.value),
      this.submitAdForm.get('currency')?.value,
      address,
      this.userName,
      this.submitAdForm.get('isActive')?.value
    );
    adPayload.categoryCode = this.myForm?.value?.category;
    console.log('Submitting ad with payload:', adPayload);

    // Submit ad with media files
    this.adSubmissionService.submitAdWithMedia(this.selectedFiles, adPayload).subscribe({
      next: (response) => {
        console.log('Ad submitted successfully:', response);
        this.successMessage = 'Ad posted successfully!';
        this.isSubmitting = false;
        
        // Reset form after successful submission
        this.submitAdForm.reset({ currency: 'INR', isActive: true, isoCode: 'IND', region: 'Asia' });
        this.selectedFiles = [];

        // Redirect to ad list after 2 seconds
        setTimeout(() => {
          this.router.navigate(['/ad-list']);
        }, 2000);
      },
      error: (error) => {
        console.log('Ad submission failed:');
        this.errorMessage = error?.error?.message || 'Failed to submit ad. Please try again.';
        this.isSubmitting = false;
        this.router.navigate(['/ad-posts']);
      }
    });
  }

  resetForm(): void {
    this.submitAdForm.reset({ currency: 'INR', isActive: true, isoCode: 'IND', region: 'Asia' });
    this.selectedFiles = [];
    this.errorMessage = '';
    this.successMessage = '';
  }
}
