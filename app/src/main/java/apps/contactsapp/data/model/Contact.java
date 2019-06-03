package apps.contactsapp.data.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Contact implements Parcelable, Comparable<Contact> {

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel source) {
            return new Contact(source);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };
    public final long id;
    public int inVisibleGroup;
    public String displayName;
    public boolean starred;
    public Uri photo;
    public Uri thumbnail;
    public ArrayList<String> emails = new ArrayList<>();
    public ArrayList<String> phoneNumbers = new ArrayList<>();
    public Contact(long id) {
        this.id = id;
    }

    protected Contact(Parcel in) {
        this.id = in.readLong();
        this.inVisibleGroup = in.readInt();
        this.displayName = in.readString();
        this.starred = in.readByte() != 0;
        this.photo = in.readParcelable(Uri.class.getClassLoader());
        this.thumbnail = in.readParcelable(Uri.class.getClassLoader());
        this.emails = in.createStringArrayList();
        this.phoneNumbers = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Contact contact = (Contact) o;
        return id == contact.id;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeInt(this.inVisibleGroup);
        dest.writeString(this.displayName);
        dest.writeByte(this.starred ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.photo, flags);
        dest.writeParcelable(this.thumbnail, flags);
        dest.writeStringList(this.emails);
        dest.writeStringList(this.phoneNumbers);
    }

    @Override
    public int compareTo(Contact o) {
        return this.displayName.compareTo(o.displayName);
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id=" + id +
                ", inVisibleGroup=" + inVisibleGroup +
                ", displayName='" + displayName + '\'' +
                ", starred=" + starred +
                ", photo=" + photo +
                ", thumbnail=" + thumbnail +
                ", emails=" + emails +
                ", phoneNumbers=" + phoneNumbers +
                '}';
    }
}
