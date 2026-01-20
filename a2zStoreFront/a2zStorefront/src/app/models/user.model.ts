import { AddressModel } from "./address.model";
import { CountryModel } from "./country.model";

export class UserModel {
   constructor(public userName:string,
    public firstName: string,
    public lastName: string,
    public email: string,
    public password: string,
    public confirmPassword:string,
    public phoneNumber:string,
    public defaultCountry:CountryModel,
  public defaultAddress:AddressModel){}
  }
  