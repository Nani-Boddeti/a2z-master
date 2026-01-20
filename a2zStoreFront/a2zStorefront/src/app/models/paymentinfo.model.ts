import { AddressModel } from "./address.model";

export class PaymentInfoModel {
   constructor( public paymentCode:string,
    public paymentType:string,
    public paymentAddress:AddressModel){}
  }