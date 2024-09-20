package com.policyboss.policybosspro.core.response.syncContact.syncContactDetailsResponse;

import android.os.Parcel;
import android.os.Parcelable;

public class synctransactionDetailEntity implements Parcelable {


    /**
     * _id : 5ed8b4b61ce98b130412bbea
     * Transaction_Id : 113
     * Transaction_Status : Cancle
     * Modified_On : 2020-06-04T08:45:42.748Z
     * Created_On : 2020-06-04T08:45:42.748Z
     * Total_Premium : 1200216
     * Service_Tax : 216
     * Net_Premium : 1200
     * Plan : 30
     * Email : chandrapra_singh@yahoo.co.uk
     * Mobile : 8800320008
     * Name : RAJESH  JOSHI
     * Fba_ID : 459
     * Ss_Id : 1655
     * __v : 0
     * PayId : undefined
     */

    private String _id;
    private int Transaction_Id;
    private String Transaction_Status;
    private String Modified_On;
    private String Created_On;
    private int Total_Premium;
    private int Service_Tax;
    private int Net_Premium;
    private int Plan;
    private String Email;
    private String Mobile;
    private String Name;
    private int Fba_ID;
    private int Ss_Id;
    private int __v;
    private String PayId;

    protected synctransactionDetailEntity(Parcel in) {
        _id = in.readString();
        Transaction_Id = in.readInt();
        Transaction_Status = in.readString();
        Modified_On = in.readString();
        Created_On = in.readString();
        Total_Premium = in.readInt();
        Service_Tax = in.readInt();
        Net_Premium = in.readInt();
        Plan = in.readInt();
        Email = in.readString();
        Mobile = in.readString();
        Name = in.readString();
        Fba_ID = in.readInt();
        Ss_Id = in.readInt();
        __v = in.readInt();
        PayId = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeInt(Transaction_Id);
        dest.writeString(Transaction_Status);
        dest.writeString(Modified_On);
        dest.writeString(Created_On);
        dest.writeInt(Total_Premium);
        dest.writeInt(Service_Tax);
        dest.writeInt(Net_Premium);
        dest.writeInt(Plan);
        dest.writeString(Email);
        dest.writeString(Mobile);
        dest.writeString(Name);
        dest.writeInt(Fba_ID);
        dest.writeInt(Ss_Id);
        dest.writeInt(__v);
        dest.writeString(PayId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<synctransactionDetailEntity> CREATOR = new Creator<synctransactionDetailEntity>() {
        @Override
        public synctransactionDetailEntity createFromParcel(Parcel in) {
            return new synctransactionDetailEntity(in);
        }

        @Override
        public synctransactionDetailEntity[] newArray(int size) {
            return new synctransactionDetailEntity[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getTransaction_Id() {
        return Transaction_Id;
    }

    public void setTransaction_Id(int Transaction_Id) {
        this.Transaction_Id = Transaction_Id;
    }

    public String getTransaction_Status() {
        return Transaction_Status;
    }

    public void setTransaction_Status(String Transaction_Status) {
        this.Transaction_Status = Transaction_Status;
    }

    public String getModified_On() {
        return Modified_On;
    }

    public void setModified_On(String Modified_On) {
        this.Modified_On = Modified_On;
    }

    public String getCreated_On() {
        return Created_On;
    }

    public void setCreated_On(String Created_On) {
        this.Created_On = Created_On;
    }

    public int getTotal_Premium() {
        return Total_Premium;
    }

    public void setTotal_Premium(int Total_Premium) {
        this.Total_Premium = Total_Premium;
    }

    public int getService_Tax() {
        return Service_Tax;
    }

    public void setService_Tax(int Service_Tax) {
        this.Service_Tax = Service_Tax;
    }

    public int getNet_Premium() {
        return Net_Premium;
    }

    public void setNet_Premium(int Net_Premium) {
        this.Net_Premium = Net_Premium;
    }

    public int getPlan() {
        return Plan;
    }

    public void setPlan(int Plan) {
        this.Plan = Plan;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String Mobile) {
        this.Mobile = Mobile;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public int getFba_ID() {
        return Fba_ID;
    }

    public void setFba_ID(int Fba_ID) {
        this.Fba_ID = Fba_ID;
    }

    public int getSs_Id() {
        return Ss_Id;
    }

    public void setSs_Id(int Ss_Id) {
        this.Ss_Id = Ss_Id;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public String getPayId() {
        return PayId;
    }

    public void setPayId(String PayId) {
        this.PayId = PayId;
    }
}
