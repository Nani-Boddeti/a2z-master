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
@Component({
  selector: 'app-ad-details',
  templateUrl: './ad-details.component.html',
  styleUrl: './ad-details.component.css'
})
export class AdDetailsComponent {
  placeOrderForm!: FormGroup;
  adId!: number;
  constructor(private adPostUtilService:AdPostUtilService , 
    private orderService:OrderServiceService, 
    private formBuilder: FormBuilder,
  private route: ActivatedRoute,
private router: Router, ){}

  selectedItem:any;
  isItemSelected: boolean = false;
  isBackToAllPosts:boolean= false; 
  isOrderPlaced : boolean = false;
  ngOnInit() {
    this.adId = +this.route.snapshot.queryParams['adId'] || 0;
    if(this.adId) {
    this.viewAd(this.adId);}
    this.initializeForm();
  }

  viewAd(newValue: number) {
  this.adPostUtilService.getAdById(newValue).subscribe((data: any) => {
    this.selectedItem = data;
      this.isItemSelected = true;
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
  orderEntryList: OrderEntryModel[] = [];
  rentIt(data:AdPostModel){
    const customer : CustomerModel = data.customer;
    const ad = new AdModel(data.id);
    const price = data.price;
    const orderEntry = new OrderEntryModel(ad,1,price,price,price);    
    this.orderEntryList.push(orderEntry);
    const address : AddressModel = data.address;
    const orderModel = new OrderModel(customer,price,this.orderEntryList,address,"COD",address,null);
    this.orderService.placeOrder(orderModel).subscribe(response => {
      this.isOrderPlaced = true;
      sessionStorage.setItem('lastOrder', JSON.stringify(response));
      this.router.navigate(['/order-confirm']);
      console.log('Response from the server:', response);
    }, error => {
      console.error('Error:', error);
    });
  }
}


