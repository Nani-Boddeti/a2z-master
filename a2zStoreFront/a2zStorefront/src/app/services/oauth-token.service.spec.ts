import { TestBed } from '@angular/core/testing';

import { OauthTokenService } from './oauth-token.service';

describe('OauthTokenService', () => {
  let service: OauthTokenService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OauthTokenService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
