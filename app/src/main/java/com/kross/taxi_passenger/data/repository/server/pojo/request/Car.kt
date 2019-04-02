package com.kross.taxi_passenger.data.repository.server.pojo.request

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Car(@SerializedName("make") var make: String = "",
          @SerializedName("model") var model: String = "",
          @SerializedName("license_plate_number") var licensePlateNumber: String = "",
          @SerializedName("car_photo") var carPhoto: String = "",
          @SerializedName("proof_of_ownership") var proofOfOwnership: String = "",
          @SerializedName("insurance") var insurance: String = "") : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(make)
        dest?.writeString(model)
        dest?.writeString(licensePlateNumber)
        dest?.writeString(carPhoto)
        dest?.writeString(proofOfOwnership)
        dest?.writeString(insurance)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Car> {
        override fun createFromParcel(parcel: Parcel): Car {
            return Car(parcel)
        }

        override fun newArray(size: Int): Array<Car?> {
            return arrayOfNulls(size)
        }
    }
}