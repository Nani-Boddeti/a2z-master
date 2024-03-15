import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AdPostsModule } from './ad-posts/ad-posts.module';
import { CustomersModule } from './customers/customers.module';
import { OrdersModule } from './orders/orders.module';
import { LoginRegisterComponent } from './login-register/login-register.component';
import { CookieService } from 'ngx-cookie-service';

@NgModule({
  declarations: [
    AppComponent,
    LoginRegisterComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    AdPostsModule,
    CustomersModule,
    OrdersModule
  ],
  providers: [CookieService],
  bootstrap: [AppComponent]
})
export class AppModule { }
