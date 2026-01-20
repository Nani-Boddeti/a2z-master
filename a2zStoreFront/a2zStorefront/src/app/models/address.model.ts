import { CountryModel } from "./country.model";

export class AddressModel {
    constructor( public apartment:string,
    public country:CountryModel,
    public customer:string,
    public district:string,
    public email:string,
    public firstName:string,
    public lastName:string,
    public line1:string,
    public line2:string,
    public latitude : number,
    public longitude : number,
  public defaultAddress:boolean){}
  }