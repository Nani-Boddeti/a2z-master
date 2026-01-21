import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SubmitAdComponent } from './ad-posts/submit-ad/submit-ad.component';
import { AdListComponent } from './ad-posts/ad-list/ad-list.component';
import { ProfileComponent } from './customers/profile/profile.component';
import { OrdersListComponent } from './orders/orders-list/orders-list.component';
import { LoginRegisterComponent } from './login-register/login-register.component';
import { AdDetailsComponent } from './ad-posts/ad-details/ad-details.component';
import { OrderConfirmComponent } from './orders/order-confirm/order-confirm.component';
import { SearchResults } from './ad-posts/search-results/search-results';


const routes: Routes = [
  { path: 'ad-list', component: AdListComponent },
  { path: 'ad-details', component: AdDetailsComponent },
  { path: 'ad-posts', component: SubmitAdComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'my-orders', component: OrdersListComponent },
  { path: 'approvals', loadChildren: () => import('./approvals/approvals.module').then(m => m.ApprovalsModule) },
  { path: 'loginV3', component: LoginRegisterComponent},
  {path:'order-confirm',component:OrderConfirmComponent},
  { path: 'search-results', component: SearchResults },
  
  // {path: '', redirectTo: '/ad-list', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
