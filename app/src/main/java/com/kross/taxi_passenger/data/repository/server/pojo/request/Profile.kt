package com.kross.taxi_passenger.data.repository.server.pojo.request

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Profile(@SerializedName("photo") var photo: String = "",
              @SerializedName("first_name") var firstName: String = "",
              @SerializedName("last_name") var lastName: String = "",
              @SerializedName("email") var email: String = "") : Parcelable {


    constructor(parcel: Parcel) : this(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readString())


    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(photo)
        dest?.writeString(firstName)
        dest?.writeString(lastName)
        dest?.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Profile> {
        override fun createFromParcel(parcel: Parcel): Profile {
            return Profile(parcel)
        }

        override fun newArray(size: Int): Array<Profile?> {
            return arrayOfNulls(size)
        }
    }
}
