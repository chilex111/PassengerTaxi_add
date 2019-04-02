package com.kross.taxi_passenger.data.repository.server.pojo.request

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OwnerBody(@Expose
                @SerializedName("profile") var profile: Profile? = null,
                @Expose
                @SerializedName("car") var car: Car? = null) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readParcelable(Profile::class.java.classLoader),
            parcel.readParcelable(Car::class.java.classLoader))


    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeParcelable(profile, flags)
        parcel?.writeParcelable(car, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OwnerBody> {
        override fun createFromParcel(parcel: Parcel): OwnerBody {
            return OwnerBody(parcel)
        }

        override fun newArray(size: Int): Array<OwnerBody?> {
            return arrayOfNulls(size)
        }
    }
}
