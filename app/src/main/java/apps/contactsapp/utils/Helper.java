package apps.contactsapp.utils;

import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Patterns;

import java.util.ArrayList;

import apps.contactsapp.data.model.Contact;

public class Helper {



    public static boolean verifyAndAddContact(Contact contact) {
        return (contact.emails != null && !contact.emails.isEmpty() ||
                contact.phoneNumbers != null && !contact.phoneNumbers.isEmpty())
                && !TextUtils.isEmpty(contact.displayName);
    }

    public static String getFormattedNumber(String number) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            number = PhoneNumberUtils.formatNumber(number, "IN").replaceAll("\\s+", "");
        } else {
            number = PhoneNumberUtils.formatNumber(number).replaceAll("\\s+", "");
        }
        return number;
    }

    public static boolean verifyEmail(ArrayList<String> emails, String email) {
        return !TextUtils.isEmpty(email) &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                isUniqueEmail(emails, email);
    }

    public static boolean isUniqueEmail(ArrayList<String> emails, String email) {
        if (emails != null) {
            for (int i = 0; i < emails.size(); i++) {
                if (email.equals(emails.get(i)))
                    return false;
            }
            return true;
        } else return true;
    }

    public static boolean verifyPhone(ArrayList<String> phoneNumbers, String phone) {
        return !TextUtils.isEmpty(phone) &&
                Patterns.PHONE.matcher(phone).matches() &&
                isUniquePhoneNumber(phoneNumbers, phone);
    }

    public static boolean isUniquePhoneNumber(ArrayList<String> phoneNumbers, String phone) {
        if (phoneNumbers != null) {
            for (int i = 0; i < phoneNumbers.size(); i++) {
                if (matchPhoneNumber(phoneNumbers.get(i), phone, null)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean matchPhoneNumber(String listPhone, String singlePhone, String countryCode) {
        if (countryCode == null) countryCode = "";
        if (listPhone.equals(singlePhone)) return true;
        listPhone = listPhone.replace(countryCode, "");
        if (listPhone.equals(singlePhone)) return true;
        if (listPhone.length() <= singlePhone.length()) {
            singlePhone = singlePhone.substring(singlePhone.length() - listPhone.length());
            return singlePhone.equals(listPhone);
        } else {
            listPhone = listPhone.substring(listPhone.length() - singlePhone.length());
            return singlePhone.equals(listPhone);
        }
    }

    public static String convertText(ArrayList<String> mAl){
        String listString = "";

        for (String s : mAl)
        {
            listString += s + "\t";
        }

return listString;
    }
}
