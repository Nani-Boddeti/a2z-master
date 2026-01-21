import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/internal/operators/map';

@Injectable({
  providedIn: 'root'
})
export class LocationService {
   errorMessage = '';
    successMessage = '';
    isLoadingLocation = false;
constructor(private http: HttpClient) { }

  getLocationFromBrowser(): Observable<{latitude: number, longitude: number}>  {
    return new Observable(observer => {
      if (navigator.geolocation) {
        this.isLoadingLocation = true;
    console.log('Requesting geolocation permission...');

    navigator.geolocation.getCurrentPosition(
      (position) => {
        observer.next({
              latitude: position.coords.latitude,
              longitude: position.coords.longitude
            });
            observer.complete();
        this.successMessage = 'Location detected successfully!';
                // Clear success message after 3 seconds
        setTimeout(() => {
          this.successMessage = '';
        }, 3000);
      },
      (error) => {
        //this.isLoadingLocation = false;
        switch(error.code) {
          case error.PERMISSION_DENIED:
            this.errorMessage = 'Location permission denied. Please enable location access in your browser settings.';
            break;
          case error.POSITION_UNAVAILABLE:
            this.errorMessage = 'Location information is unavailable.';
            break;
          case error.TIMEOUT:
            this.errorMessage = 'The request to get location timed out.';
            break;
          default:
            this.errorMessage = 'Failed to get your location. You can enter it manually.';
        }
        
        console.error('Geolocation error:', error);
      }
    );
      } else {
        observer.error('Geolocation not supported');
      }
    });
    
    
  }

  getCurrentPosition(): Observable<{latitude: number, longitude: number}> {
    return new Observable(observer => {
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (position) => {
            observer.next({
              latitude: position.coords.latitude,
              longitude: position.coords.longitude
            });
            observer.complete();
          },
          (error) => observer.error(error)
        );
      } else {
        observer.error('Geolocation not supported');
      }
    });
  }

  getCoordsByCity(city: string): Observable<{latitude: number, longitude: number}> {
  return this.http.get<any>(`https://api.example.com/coords/${city}`)
    .pipe(
        map(response => ({
        latitude: response.latitude,
        longitude: response.longitude
      }))
    );
}

}
