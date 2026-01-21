import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-pagination',
  standalone: false,
  templateUrl: './pagination.html',
  styleUrl: './pagination.css'
})
export class Pagination {
@Input() currentPage: number = 1;
  @Input() totalPages: number = 10;
  @Input() totalItems: number = 100;
  @Input() itemsPerPage: number = 10;
  @Output() pageChange = new EventEmitter<number>();

  get visiblePages(): (number | string)[] {
    const pages: (number | string)[] = [];
    const delta = 2;

    // Page 1
    pages.push(1);
    
    // Ellipsis before current range
    if (this.currentPage > 3) {
      pages.push('...');
    }
    
    // Current page range
    for (let i = Math.max(2, this.currentPage - delta); 
         i <= Math.min(this.totalPages - 1, this.currentPage + delta); 
         i++) {
      pages.push(i);
    }
    
    // Ellipsis after + last page
    if (this.currentPage < this.totalPages - 2) {
      pages.push('...', this.totalPages);
    } else if (this.totalPages > 1) {
      pages.push(this.totalPages);
    }
    
    return pages;
  }

  isNumberPage(page: number | string): boolean {
    return typeof page === 'number';
  }

  isEllipsis(page: number | string): boolean {
    return page === '...';
  }

  isActivePage(page: number | string): boolean {
    return this.isNumberPage(page) && page === this.currentPage;
  }

  goToPage(page: number | string): void {
    if (this.isNumberPage(page)) {
      this.pageChange.emit(Number(page));
    }
  }

  goToPrevious(): void {
    if (this.currentPage > 1) {
      this.pageChange.emit(this.currentPage - 1);
    }
  }

  goToNext(): void {
    if (this.currentPage < this.totalPages) {
      this.pageChange.emit(this.currentPage + 1);
    }
  }

  getPageInfo(): string {
    const start = (this.currentPage - 1) * this.itemsPerPage + 1;
    const end = Math.min(this.currentPage * this.itemsPerPage, this.totalItems);
    return `${start}-${end} of ${this.totalItems}`;
  }

  trackByFn(index: number, page: any): any {
    return page;
  }
}
