package com.orm;

import android.os.Parcel;
import android.os.Parcelable;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by yuki_yoshida on 15/01/25.
 */
@Getter @Setter public class Address extends SugarRecord<Address> implements Parcelable {

  private String name = "";

  private String phoneNo;

  private int age;

  @Override public int describeContents() {
    return 0;
  }

  @Override public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(this.name);
    dest.writeString(this.phoneNo);
    dest.writeInt(this.age);
    dest.writeString(this.tableName);
    dest.writeValue(this.id);
  }

  public Address() {
  }

  private Address(Parcel in) {
    this.name = in.readString();
    this.phoneNo = in.readString();
    this.age = in.readInt();
    this.tableName = in.readString();
    this.id = (Long) in.readValue(Long.class.getClassLoader());
  }

  public static final Parcelable.Creator<Address> CREATOR = new Parcelable.Creator<Address>() {
    public Address createFromParcel(Parcel source) {
      return new Address(source);
    }

    public Address[] newArray(int size) {
      return new Address[size];
    }
  };
}
