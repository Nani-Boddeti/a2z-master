import { Component, ElementRef, forwardRef, HostListener, OnInit, OnDestroy } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
import { OrderServiceService } from '../../services/order-service.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-order-type-dropdown',
  standalone: false,
  templateUrl: './order-type-dropdown.html',
  styleUrl: './order-type-dropdown.css',
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => OrderTypeDropdown),
    multi: true
  }]
})
export class OrderTypeDropdown implements ControlValueAccessor, OnInit, OnDestroy {
orderTypes = [];  
  filteredorderTypes = [];
  showDropdown = false;
selectedOrderTypeCode:string='Rental';
  searchTerm = '';
  private destroy$ = new Subject<void>();

constructor(private elementRef: ElementRef,private orderService: OrderServiceService) {}
  private onChange = (value: string) => {};
  private onTouched = () => {};

  ngOnInit() {
    console.log('🔄 OrderTypeDropdown.ngOnInit() called');
    // Add a small delay to prevent race conditions with other components
    setTimeout(() => {
      this.orderService.getOrderTypesList().pipe(
        takeUntil(this.destroy$)
      ).subscribe({
        next: (data) => {
          console.log('✅ Order types loaded:', data);
          this.orderTypes = data;
          this.filteredorderTypes = [...this.orderTypes];
        },
        error: (error) => {
          console.error('❌ Error loading order types:', error);
        }
      });
    }, 300);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  writeValue(value: string): void {
    if (value) {
      const orderType = this.orderTypes.find(type => type === value);
      this.selectedOrderTypeCode = orderType || 'Select Order Type';
    }
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    // Handle disabled state
  }

 toggleDropdown(event?: Event) {
    event?.stopPropagation(); // Prevent immediate close
    this.showDropdown = !this.showDropdown;
  }

  filterOrderTypes() {
    this.filteredorderTypes = this.orderTypes.filter(type =>
      type === this.searchTerm.toLowerCase()
    );
  }

  selectOrderType(orderType: any) {
    this.selectedOrderTypeCode = orderType;
    this.searchTerm = '';
    this.filteredorderTypes = [...this.orderTypes];
    this.showDropdown = false;
    this.onChange(orderType);  // ✅ Updates parent form
    this.onTouched();
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.showDropdown = false;
    }
  }
}