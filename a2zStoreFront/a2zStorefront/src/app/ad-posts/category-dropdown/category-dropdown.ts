import { Component, ElementRef, forwardRef, HostListener } from '@angular/core';
import { CATEGORIESDROPDOWNLIST } from '../../models/CategoryDropDownList';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';
@Component({
  selector: 'app-category-dropdown',
  standalone: false,
  templateUrl: './category-dropdown.html',
  styleUrl: './category-dropdown.css',
  providers: [{
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => CategoryDropdown),
    multi: true
  }]
})
export class CategoryDropdown implements ControlValueAccessor{
categories = CATEGORIESDROPDOWNLIST;  // Your categories array
  filteredCategories = [...CATEGORIESDROPDOWNLIST];
  showDropdown = false;
  selectedCategoryName = 'Select Category';
  selectedCategoryCode: string = 'ALL';
  searchTerm = '';
constructor(private elementRef: ElementRef) {}
  private onChange = (value: string) => {};
  private onTouched = () => {};

  writeValue(value: string): void {
    if (value) {
      const category = this.categories.find(cat => cat.categoryCode === value);
      this.selectedCategoryName = category?.categoryName || 'Select Category';
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

  filterCategories() {
    this.filteredCategories = this.categories.filter(cat =>
      cat.categoryName.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  selectCategory(category: any) {
    this.selectedCategoryCode = category.categoryCode;
    this.selectedCategoryName = category.categoryName;
    this.searchTerm = '';
    this.filteredCategories = [...this.categories];
    this.showDropdown = false;
    this.onChange(category.categoryCode);  // ✅ Updates parent form
    this.onTouched();
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.showDropdown = false;
    }
  }
}