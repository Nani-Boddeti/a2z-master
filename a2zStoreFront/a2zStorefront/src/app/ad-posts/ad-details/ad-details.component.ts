import { Component, Input } from '@angular/core';
import { AdPostUtilService } from '../ad-post-util.service';
import { OrderServiceService } from '../../services/order-service.service';
import { OrderModel } from '../../models/order.model';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AdModel } from '../../models/ad.model';
import { CustomerModel } from '../../models/customer.model';
import { AdPostModel } from '../../models/adPost.model';
import { OrderEntryModel } from '../../models/orderEntry.model';
import { AddressModel } from '../../models/address.model';
import { ActivatedRoute, Router } from '@angular/router';
import { LeafletModule } from '@asymmetrik/ngx-leaflet/lib/leaflet.module';
import { CommonModule } from '@angular/common';
import { AuthStateService } from '../../services/auth-state.service';
@Component({
  selector: 'app-ad-details',
  templateUrl: './ad-details.component.html',
  styleUrl: './ad-details.component.css'
 
})
export class AdDetailsComponent {
  placeOrderForm!: FormGroup;
  adId!: number;
  errorMessage: string = '';
  userName: string = '';
  constructor(private adPostUtilService:AdPostUtilService , 
    private orderService:OrderServiceService, 
    private formBuilder: FormBuilder,
  private route: ActivatedRoute,
private router: Router, private authStateService: AuthStateService){}

  selectedItem:any;
  isItemSelected: boolean = false;
  isBackToAllPosts:boolean= false; 
  isOrderPlaced : boolean = false;
  showError: boolean = false;
  currentIndex = 0;
  submitButtonName: string = 'Rent Now';
  ngOnInit() {
    this.authStateService.userName$.subscribe((userName) => {
      this.userName = userName;
    });
    this.adId = +this.route.snapshot.queryParams['adId'] || 0;
    if(this.adId) {
    this.viewAd(this.adId);}
    this.initializeForm();
  setInterval(() => {
      if (this.selectedItem?.mediaContainerData?.medias?.length > 1) {
        this.currentIndex = (this.currentIndex + 1) % this.selectedItem.mediaContainerData.medias.length;
      }
    }, 4000);
  }

  viewAd(newValue: number) {
  this.adPostUtilService.getAdById(newValue).subscribe((data: any) => {
    if(this.userName && data.customer.userName === this.userName) {
          data.isSelfAd = true;
        } else {
          data.isSelfAd = false;    
      }
    this.selectedItem = data;
      this.isItemSelected = true;
      this.submitButtonName = this.selectedItem.orderType === 'SELL' || this.selectedItem.orderType === 'GIVEAWAY' ? 'Buy Now' : 'Rent Now';
      this.router.navigate(['/map'], { queryParams: { latitude: data.latitude, longitude: data.longitude} });
  });
}
  backToList() {
    this.router.navigate(['/ad-list']);
  }
  // Initialize the form with nested structure
  initializeForm(): void {
    this.placeOrderForm = this.formBuilder.group({
      customer: this.formBuilder.group({
        userName: [''],
        phoneNumber: [''],
        email: [''],
        defaultCountry: this.formBuilder.group(this.createCountryFormGroup),
        defaultPaymentInfo:this.formBuilder.group(this.createPaymentInfoFormGroup)
      }),
      deliveryAddress: this.formBuilder.group(this.createAddressFormGroup()),
      paymentAddress: this.formBuilder.group(this.createAddressFormGroup()),
      paymentInfo:this.formBuilder.group(this.createPaymentInfoFormGroup),
      deliveryMode:[''],
      entries: this.formBuilder.array([this.createEntryFormGroup()])
    });
  }
  createAddressFormGroup(): FormGroup {
    return this.formBuilder.group({
      apartment: [''],
      country: this.formBuilder.group(this.createCountryFormGroup()),
      customer: [''],
      district: [''],
      email: [''],
      firstName: [''],
      lastName: [''],
      line1: [''],
      line2: ['']
    });
  }
  createPaymentInfoFormGroup(): FormGroup {
    return this.formBuilder.group({
      paymentCode: [''],
      paymentType: [''],
      paymentAddress: this.formBuilder.group(this.createAddressFormGroup())
    });
  }
  createCountryFormGroup(): FormGroup {
    return this.formBuilder.group({
      isoCode: [''],
      region: ['']
    });
  }
  createEntryFormGroup(): FormGroup {
    return this.formBuilder.group({
      adPost: this.formBuilder.group(this.createAdPostFormGroup()),
      qty: [Number],
      basePrice:this.formBuilder.group(this.createPriceFormGroup),
      discountedPrice:this.formBuilder.group(this.createPriceFormGroup),
      totalPrice:this.formBuilder.group(this.createPriceFormGroup)
    });
  }
  createAdPostFormGroup(): FormGroup {
    return this.formBuilder.group({
      adPost: [''],
      region: ['']
    });
  }
  createPriceFormGroup(): FormGroup {
    return this.formBuilder.group({
      id: [Number]
    });
  }
   closeError() {
    this.showError = false;
    //this.onClose.emit();
  }
  orderEntryList: OrderEntryModel[] = [];
  rentIt(data:AdPostModel){
    const customer : CustomerModel = data.customer;
    const ad = new AdModel(data.id,data.orderType);
    const price = data.price;
    const orderEntry = new OrderEntryModel(ad,1,price,price,price);    
    this.orderEntryList.push(orderEntry);
    const address : AddressModel = data.address;
    const orderModel = new OrderModel(customer,price,this.orderEntryList,address,"COD",address,null,data.orderType);
    this.orderService.placeOrder(orderModel).subscribe(response => {
      
      if(response.errorMessage){
        console.error('Order placement failed:', response.errorMessage);
        this.router.navigate(['/ad-details?adId='+this.adId]);
        this.showError = true;
        this.errorMessage = response.errorMessage;
      }else {
      this.isOrderPlaced = true;
      sessionStorage.setItem('lastOrder', JSON.stringify(response));
      this.router.navigate(['/order-confirm']);
      console.log('Response from the server:', response);
      }

    }, error => {
      console.error('Error:', error);
      this.router.navigate(['/loginV3']);
    });
  }

   openMap(lat : number, lng: number  ) {
                  const url = `https://www.google.com/maps/search/?api=1&query=${lat},${lng}`;
                  window.open(url, "_blank", "noopener");
                }
}


