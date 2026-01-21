import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { switchMap, map } from 'rxjs/operators';
import { MediaUploadService } from './media-upload.service';
import { AdService } from './ad.service';

@Injectable({
  providedIn: 'root'
})
export class AdSubmissionService {

  constructor(
    private mediaUploadService: MediaUploadService,
    private adService: AdService,
    private http: HttpClient
  ) { }

  /**
   * Complete workflow: Upload media files, then post the ad
   * @param mediaFiles - Array of files to upload
   * @param adData - Ad data object
   * @returns Observable with the posted ad response
   */
  submitAdWithMedia(mediaFiles: File[], adData: any): Observable<any> {
    // First upload the media
    return this.mediaUploadService.uploadMedia(mediaFiles).pipe(
      switchMap((mediaResponse: any) => {
        // Get the media container id from the response
        const mediaContainerId = mediaResponse.code || mediaResponse.id || mediaResponse.mediaContainerId;
        
        // Update ad data with the media container id
        adData.mediaContainerData = {
          code: mediaContainerId
        };

        // Now post the ad with the media id
        return this.adService.postAd(adData);
      })
    );
  }

  /**
   * Submit a single media ad
   * @param mediaFile - Single file to upload
   * @param adData - Ad data object
   * @returns Observable with the posted ad response
   */
  submitAdWithSingleMedia(mediaFile: File, adData: any): Observable<any> {
    return this.mediaUploadService.uploadSingleMedia(mediaFile).pipe(
      switchMap((mediaResponse: any) => {
        const mediaContainerId = mediaResponse.code || mediaResponse.id || mediaResponse.mediaContainerId;
        
        adData.mediaContainerData = {
          code: mediaContainerId
        };

        return this.adService.postAd(adData);
      })
    );
  }

  /**
   * Create complete ad submission object
   */
  createAdSubmissionPayload(
    productName: string,
    categoryCode: string,
    description: string,
    price: number,
    currency: string,
    address: any,
    userName: string,
    isActive: boolean = true
  ): any {
    return {
      description,
      mediaContainerData: {
        code: '' // Will be filled after media upload
      },
      isActive,
      productName,
      customer: {
        userName,
        defaultAddress:{
        latitude: address.latitude || 0,
        longitude: address.longitude || 0,
        firstName: address.firstName || '',
        lastName: address.lastName || '',
        line1: address.line1 || '',
        line2: address.line2 || '',
        apartment: address.apartment || '',
        country: {
          isoCode: address.country?.isoCode || 'IND',
          region: address.country?.region || 'Asia'
        },
        district: address.district || '',
        email: address.email || '',
        customer: userName
      }
      },
      price: {
        currency,
        amount: price.toFixed(2)
      },
      sourceAddress: {
        latitude: address.latitude || 0,
        longitude: address.longitude || 0,
        firstName: address.firstName || '',
        lastName: address.lastName || '',
        line1: address.line1 || '',
        line2: address.line2 || '',
        apartment: address.apartment || '',
        country: {
          isoCode: address.country?.isoCode || 'IND',
          region: address.country?.region || 'Asia'
        },
        district: address.district || '',
        email: address.email || '',
        customer: userName
      }
    };
  }
}
