import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { AdPostUtilService } from '../ad-post-util.service';

@Component({
  selector: 'app-ad-list',
  templateUrl: './ad-list.component.html',
  styleUrl: './ad-list.component.css'
})
export class AdListComponent {
 title:string="am New";
 constructor(private adPostUtilService:AdPostUtilService){}
 adList:any = this.adPostUtilService.getAdList();
}
