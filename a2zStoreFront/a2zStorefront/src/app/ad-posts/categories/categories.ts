import { Component, ElementRef, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { AdSearch } from '../../services/ad-search';

@Component({
  selector: 'app-categories',
  standalone: false,
  templateUrl: './categories.html',
  styleUrl: './categories.css'
})
export class Categories {
activeCategory: string = 'ALL';  // Default active
  constructor(
    private adSearch: AdSearch
  ) { }
@ViewChild('scrollRef')
 scrollRef!: ElementRef<HTMLDivElement>;

  @Input() categories: any[] = [];
  
  @Output() categoryChange = new EventEmitter<string>();

  isActiveCategory(categoryCode: string): boolean {
    return this.activeCategory === categoryCode;
  }

  selectCategory(categoryCode: string) {
    this.activeCategory = categoryCode;
    this.categoryChange.emit(categoryCode);
  }

  trackByCategory(index: number, category: any): string {
    return category.categoryCode;
  }

  @ViewChild('scrollWrapper') scrollWrapper!: ElementRef;
  
  // scrollLeft = 0;
  hasMoreToScroll = false;

  ngAfterViewChecked() {
    this.updateScrollState();
  }

  updateScrollState() {
    if (this.scrollRef?.nativeElement) {
      const el = this.scrollRef.nativeElement;
      // this.scrollLeft = el.scrollLeft;
      this.hasMoreToScroll = el.scrollWidth > el.clientWidth + 20;
    }
  }

  scrollLeft() {
    this.scrollRef.nativeElement.scrollBy({ 
      left: -500, 
      behavior: 'smooth' 
    });
  }

  scrollRight() {
    this.scrollRef.nativeElement.scrollBy({ 
      left: 500, 
      behavior: 'smooth' 
    });
  }

  // Add scroll listener manually
  onScroll() {
    setTimeout(() => this.updateScrollState(), 0);
  }

}
