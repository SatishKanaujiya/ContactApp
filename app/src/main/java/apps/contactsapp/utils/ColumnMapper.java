package apps.contactsapp.utils;

import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import apps.contactsapp.data.model.Contact;

public class ColumnMapper {


    private ColumnMapper() {
    }

  public   static void mapInVisibleGroup(Cursor cursor, Contact contact, int columnIndex) {
        contact.inVisibleGroup = cursor.getInt(columnIndex);
    }

    public static void mapDisplayName(Cursor cursor, Contact contact, int columnIndex) {
        String displayName = cursor.getString(columnIndex);
        if (displayName != null && !displayName.isEmpty()) {
            contact.displayName = displayName;
        }
    }

    public static void mapEmail(Cursor cursor, Contact contact, int columnIndex) {
        String email = cursor.getString(columnIndex);
        if (Helper.verifyEmail(contact.emails, email)) {
            contact.emails.add(email);
        }
    }

    public static void mapPhoneNumber(Cursor cursor, Contact contact, int columnIndex) {
        String phoneNumber = cursor.getString(columnIndex);
        if (!TextUtils.isEmpty(phoneNumber)) {
            phoneNumber = Helper.getFormattedNumber(phoneNumber);
            if (Helper.verifyPhone(contact.phoneNumbers, phoneNumber)) {
                contact.phoneNumbers.add(phoneNumber);
            }
        }
    }

    public static void mapPhoto(Cursor cursor, Contact contact, int columnIndex) {
        String uri = cursor.getString(columnIndex);
        if (uri != null && !uri.isEmpty()) {
            contact.photo = Uri.parse(uri);
        }
    }

    public  static void mapStarred(Cursor cursor, Contact contact, int columnIndex) {
        contact.starred = cursor.getInt(columnIndex) != 0;
    }

    public static void mapThumbnail(Cursor cursor, Contact contact, int columnIndex) {
        String uri = cursor.getString(columnIndex);
        if (uri != null && !uri.isEmpty()) {
            contact.thumbnail = Uri.parse(uri);
        }
    }
}
