package com.dsk.gizi_final;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.CheckBox;

/**
 * Created by suhyun on 2018-08-06.
 */

public class Toilet implements Parcelable {
    private Drawable img;
    private String Toiletname;
    private String Toiletline;
    private boolean isSelected;

    public static final Parcelable.Creator<Toilet> CREATOR = new Parcelable.Creator<Toilet>() {
        public Toilet createFromParcel(Parcel in) {
            return new Toilet(in);
        }

        public Toilet[] newArray(int size) {
            return new Toilet[size];
        }
    };
    public Toilet(Parcel in) {
        this.Toiletname = in.readString();
        this.Toiletline = in.readString();
        this.isSelected = in.readByte() != 0;
    }

    public Toilet() {

    }


    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Toiletname);
        dest.writeString(this.Toiletline);
        dest.writeByte((byte) (this.isSelected ? 1 : 0));
    }
    public Toilet(String name, String line) {
        this.Toiletname = name;
        this.Toiletline = line; // true is male, false is woman
        this.isSelected = false; // not selected when create
    }

    public Drawable getImg() {
        return img;
    }

    public void setImg(Drawable img) {
        this.img = img;
    }

    public String getToiletname() {
        return Toiletname;
    }

    public void setToiletname(String toiletname) {
        Toiletname = toiletname;
    }

    public String getToiletline() {
        return Toiletline;
    }

    public void setToiletline(String toiletline) {
        Toiletline = toiletline;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }



}