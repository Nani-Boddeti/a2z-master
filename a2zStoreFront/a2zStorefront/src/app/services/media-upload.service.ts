import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MediaUploadService {
  private mediaUploadUrl = '/media/upload?isMap=false';

  constructor(private http: HttpClient) { }

  /**
   * Upload media files as multipart formdata
   * @param files - Array of files to upload
   * @returns Observable with mediaContainer id
   */
  uploadMedia(files: File[]): Observable<any> {
    const formData = new FormData();
    
    // Add all files to formdata
    files.forEach((file) => {
      formData.append('files', file);
    });

    // Post as multipart/form-data (HttpClient sets correct headers automatically)
    return this.http.post(this.mediaUploadUrl, formData);
  }

  /**
   * Upload a single media file
   * @param file - File to upload
   * @returns Observable with mediaContainer id
   */
  uploadSingleMedia(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post(this.mediaUploadUrl, formData);
  }
}
