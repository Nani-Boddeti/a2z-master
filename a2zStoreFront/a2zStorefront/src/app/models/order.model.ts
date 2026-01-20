import { AddressModel } from "./address.model";
import { CustomerModel } from "./customer.model";
import { OrderEntryModel } from "./orderEntry.model";
import { PaymentInfoModel } from "./paymentinfo.model";
import { PriceModel } from "./price.model";

export class OrderModel {
  constructor( public customer:CustomerModel,
  public price:PriceModel,
  public entries: OrderEntryModel[],
  public deliveryAddress:AddressModel| null,
  public deliveryMode:string| null,
  public paymentAddress: AddressModel| null,
  public paymentInfo: PaymentInfoModel| null){}

  
  }