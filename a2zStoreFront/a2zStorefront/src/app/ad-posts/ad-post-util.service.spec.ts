import { TestBed } from '@angular/core/testing';

import { AdPostUtilService } from './ad-post-util.service';

describe('AdPostUtilService', () => {
  let service: AdPostUtilService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdPostUtilService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
