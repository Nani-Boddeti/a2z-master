import { TestBed } from '@angular/core/testing';

import { AdHelperService } from './ad-helper.service';

describe('AdHelperService', () => {
  let service: AdHelperService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdHelperService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
