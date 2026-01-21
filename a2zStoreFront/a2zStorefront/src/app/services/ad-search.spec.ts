import { TestBed } from '@angular/core/testing';

import { AdSearch } from './ad-search';

describe('AdSearch', () => {
  let service: AdSearch;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AdSearch);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
