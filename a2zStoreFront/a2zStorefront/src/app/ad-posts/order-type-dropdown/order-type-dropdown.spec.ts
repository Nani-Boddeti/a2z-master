import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderTypeDropdown } from './order-type-dropdown';

describe('OrderTypeDropdown', () => {
  let component: OrderTypeDropdown;
  let fixture: ComponentFixture<OrderTypeDropdown>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OrderTypeDropdown]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderTypeDropdown);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
