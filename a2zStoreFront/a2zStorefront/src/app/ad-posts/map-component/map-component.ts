// map-component.ts
import { Component, AfterViewInit, ElementRef, ViewChild, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import * as L from 'leaflet';

@Component({
  selector: 'app-map-component',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div style="width: 100%; max-width: 900px; margin: 20px auto; padding: 20px; background: white;">
      <div #mapContainer style="height: 500px; width: 100%; border: 2px solid #333; position: relative;"></div>
      <div style="text-align: center; padding: 15px; background: #f8f9fa; margin-top: 10px; border-radius: 0 0 8px 8px;">
        📍 <strong>Lat: 17.3850°N</strong> | <strong>Lng: 78.4867°E</strong>
      </div>
    </div>
  `
})
export class MapComponent implements AfterViewInit, OnDestroy {
  @ViewChild('mapContainer', { static: false }) mapContainer!: ElementRef;
  private map!: L.Map;
  private mapId = 'hyderabad-map-' + Math.random().toString(36).substr(2, 9);

  ngAfterViewInit() {
    // CRITICAL: Wait for container to render
    setTimeout(() => {
      this.initMap();
    }, 0);
  }

  private initMap() {
    const container = this.mapContainer.nativeElement as HTMLElement;
    
    // Set explicit ID to avoid conflicts
    container.id = this.mapId;
    
    // Initialize map with FIXED Hyderabad coordinates
    this.map = L.map(this.mapId).setView([17.3850, 78.4867], 13);
    
    // Add tiles
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors',
      maxZoom: 19
    }).addTo(this.map);
    
    // Add marker
    L.marker([17.3850, 78.4867])
      .addTo(this.map)
      .bindPopup('Hyderabad, Telangana')
      .openPopup();
    
    // FORCE TILE FIX - Multiple calls
    setTimeout(() => this.map.invalidateSize(), 100);
    setTimeout(() => this.map.invalidateSize(), 500);
  }

  ngOnDestroy() {
    if (this.map) {
      this.map.remove();
    }
  }
}
