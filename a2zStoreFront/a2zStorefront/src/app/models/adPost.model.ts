import { AddressModel } from "./address.model";
import { CustomerModel } from "./customer.model";
import { PriceModel } from "./price.model";

export interface AdPostModel {
    id:number,
    description:string,
    price: PriceModel,
    customer : CustomerModel,
    productName:string,
    qty : number,
    address: AddressModel
  }