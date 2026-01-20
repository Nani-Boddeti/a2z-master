import { AdModel } from "./ad.model";
import { PriceModel } from "./price.model";

export class OrderEntryModel {
   constructor( public adPost:AdModel,
    public qty:number,
    public basePrice:PriceModel,
    public discountedPrice:PriceModel,
    public totalPrice:PriceModel){}
  }