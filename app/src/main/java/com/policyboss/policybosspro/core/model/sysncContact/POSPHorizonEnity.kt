package com.policyboss.policybosspro.core.model.sysncContact

import android.os.Parcel
import android.os.Parcelable

data class POSPHorizonEnity(
    val Aadhar: String?,
    val Account_Type: String?,
    val Agent_City: String?,
    val Already_Posp: String?,
    val Bank_Account_No: String?,
    val Bank_Branch: String?,
    val Bank_Name: String?,
    val Birthdate: String?,
    val Certification_Datetime: String?,
    val Channel: String?,
    val Created_On: String?,
    val Cust_Id: Int,

    val Document_Name: String?,
    val Document_Type: String?,
    val ERPID_CreatedDate: String?,
    val Education: String?,
    val Email_Id: String?,
    val Erp_Id: String?,
    val Experience: Int,
    val FOS_Code: String?,
    val Father_Name: String?,
    val Fba_Id: String?,
    val First_Name: String?,
    val Gender: String?,
    val Ifsc_Code: String?,
    val Income: Int,
    val IsFOS: String?,
    val Is_Active: Boolean,
    val Is_App_Installed: Int,
    val Is_Certified: Int,
    val Is_Contact_Sync: Int,
    val Is_Paid: Int,
    val Last_Name: String?,
    val Last_Status: String?,
    val Legal_case: String?,
    val Micr_Code: String?,
    val Middle_Name: String?,
    val Mobile_No: String?,
    val Modified_On: String?,
    val Name_as_in_Bank: String?,
    val Nominee_Aadhar: String?,
    val Nominee_Account_Type: String?,
    val Nominee_Bank_Account_Number: String?,
    val Nominee_Bank_Branch: String?,
    val Nominee_Bank_City: String?,
    val Nominee_Bank_Name: String?,
    val Nominee_First_Name: String?,
    val Nominee_Gender: String?,
    val Nominee_Ifsc_Code: String?,
    val Nominee_Last_Name: String?,
    val Nominee_Micr_Code: String?,
    val Nominee_Middle_Name: String?,
    val Nominee_Name_as_in_Bank: String?,
    val Nominee_Pan: String?,
    val Nominee_Relationship: String?,
    val POSP_DeActivatedDateAtIIB: String?,
    val POSP_DeActivatedtoIIB: String?,
    val POSP_UploadedtoIIB: String?,
    val POSP_UploadingDateAtIIB: String?,
    val Paid_On: String?,
    val Pan_No: String?,
    val Permanant_Add1: String?,
    val Permanant_Add2: String?,
    val Permanant_Add3: String?,
    val Permanant_City: String?,
    val Permanant_Landmark: String?,
    val Permanant_Pincode: Int,
    val Permanant_State: String?,
    val Posp_Category: String?,
    val Posp_Id: Int,
    val Posp_Onboarding_Photo: String?,
    val Present_Add1: String?,
    val Present_Add2: String?,
    val Present_Add3: String?,
    val Present_City: String?,
    val Present_Landmark: String?,
    val Present_Pincode: Int,
    val Present_State: String?,
    val Recruitment_Status: String?,
    val RegAmount: Int?,
    val Reporting_Agent_Name: String?,
    val Reporting_Agent_Uid: Int,
    val Reporting_Email_ID: String?,
    val Reporting_Mobile_Number: String?,
    val Service_Tax_Number: String?,
    val Sm_Posp_Id: String?,
    val Sm_Posp_Name: String?,
    val Sources: String?,
    val Ss_Id: Int,
    val Status_Remark: String?,
    val SubVertical: String?,
    val Telephone_No: String?,
    val TrainingEndDate: String?,
    val TrainingStartDate: String?,
    val Training_UserLog: String?,
    val Vertical: String?,
    val Vertical_Head: String?,
    val Vertical_Head_UID: Int,
    val _id: String?,
    val undefined: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Aadhar)
        parcel.writeString(Account_Type)
        parcel.writeString(Agent_City)
        parcel.writeString(Already_Posp)
        parcel.writeString(Bank_Account_No)
        parcel.writeString(Bank_Branch)
        parcel.writeString(Bank_Name)
        parcel.writeString(Birthdate)
        parcel.writeString(Certification_Datetime)
        parcel.writeString(Channel)
        parcel.writeString(Created_On)
        parcel.writeInt(Cust_Id)
        parcel.writeString(Document_Name)
        parcel.writeString(Document_Type)
        parcel.writeString(ERPID_CreatedDate)
        parcel.writeString(Education)
        parcel.writeString(Email_Id)
        parcel.writeString(Erp_Id)
        parcel.writeInt(Experience)
        parcel.writeString(FOS_Code)
        parcel.writeString(Father_Name)
        parcel.writeString(Fba_Id)
        parcel.writeString(First_Name)
        parcel.writeString(Gender)
        parcel.writeString(Ifsc_Code)
        parcel.writeInt(Income)
        parcel.writeString(IsFOS)
        parcel.writeByte(if (Is_Active) 1 else 0)
        parcel.writeInt(Is_App_Installed)
        parcel.writeInt(Is_Certified)
        parcel.writeInt(Is_Contact_Sync)
        parcel.writeInt(Is_Paid)
        parcel.writeString(Last_Name)
        parcel.writeString(Last_Status)
        parcel.writeString(Legal_case)
        parcel.writeString(Micr_Code)
        parcel.writeString(Middle_Name)
        parcel.writeString(Mobile_No)
        parcel.writeString(Modified_On)
        parcel.writeString(Name_as_in_Bank)
        parcel.writeString(Nominee_Aadhar)
        parcel.writeString(Nominee_Account_Type)
        parcel.writeString(Nominee_Bank_Account_Number)
        parcel.writeString(Nominee_Bank_Branch)
        parcel.writeString(Nominee_Bank_City)
        parcel.writeString(Nominee_Bank_Name)
        parcel.writeString(Nominee_First_Name)
        parcel.writeString(Nominee_Gender)
        parcel.writeString(Nominee_Ifsc_Code)
        parcel.writeString(Nominee_Last_Name)
        parcel.writeString(Nominee_Micr_Code)
        parcel.writeString(Nominee_Middle_Name)
        parcel.writeString(Nominee_Name_as_in_Bank)
        parcel.writeString(Nominee_Pan)
        parcel.writeString(Nominee_Relationship)
        parcel.writeString(POSP_DeActivatedDateAtIIB)
        parcel.writeString(POSP_DeActivatedtoIIB)
        parcel.writeString(POSP_UploadedtoIIB)
        parcel.writeString(POSP_UploadingDateAtIIB)
        parcel.writeString(Paid_On)
        parcel.writeString(Pan_No)
        parcel.writeString(Permanant_Add1)
        parcel.writeString(Permanant_Add2)
        parcel.writeString(Permanant_Add3)
        parcel.writeString(Permanant_City)
        parcel.writeString(Permanant_Landmark)
        parcel.writeInt(Permanant_Pincode)
        parcel.writeString(Permanant_State)
        parcel.writeString(Posp_Category)
        parcel.writeInt(Posp_Id)
        parcel.writeString(Posp_Onboarding_Photo)
        parcel.writeString(Present_Add1)
        parcel.writeString(Present_Add2)
        parcel.writeString(Present_Add3)
        parcel.writeString(Present_City)
        parcel.writeString(Present_Landmark)
        parcel.writeInt(Present_Pincode)
        parcel.writeString(Present_State)
        parcel.writeString(Recruitment_Status)
        parcel.writeInt(RegAmount?: 0)
        parcel.writeString(Reporting_Agent_Name)
        parcel.writeInt(Reporting_Agent_Uid)
        parcel.writeString(Reporting_Email_ID)
        parcel.writeString(Reporting_Mobile_Number)
        parcel.writeString(Service_Tax_Number)
        parcel.writeString(Sm_Posp_Id)
        parcel.writeString(Sm_Posp_Name)
        parcel.writeString(Sources)
        parcel.writeInt(Ss_Id)
        parcel.writeString(Status_Remark)
        parcel.writeString(SubVertical)
        parcel.writeString(Telephone_No)
        parcel.writeString(TrainingEndDate)
        parcel.writeString(TrainingStartDate)
        parcel.writeString(Training_UserLog)
        parcel.writeString(Vertical)
        parcel.writeString(Vertical_Head)
        parcel.writeInt(Vertical_Head_UID)
        parcel.writeString(_id)
        parcel.writeString(undefined)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<POSPHorizonEnity> {
        override fun createFromParcel(parcel: Parcel): POSPHorizonEnity {
            return POSPHorizonEnity(parcel)
        }

        override fun newArray(size: Int): Array<POSPHorizonEnity?> {
            return arrayOfNulls(size)
        }
    }
}