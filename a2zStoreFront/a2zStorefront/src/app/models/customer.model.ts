import { CountryModel } from "./country.model";
import { PaymentInfoModel } from "./paymentinfo.model";

export class CustomerModel {
    constructor(public userName:string,
    public email:string,
    public phoneNumber:string,
    public defaultPaymentInfo:PaymentInfoModel,
    public defaultCountry:CountryModel){}
  }