package apps.contactsapp.data.remote;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.LongSparseArray;

import apps.contactsapp.R;
import apps.contactsapp.data.model.Contact;
import apps.contactsapp.utils.ColumnMapper;
import apps.contactsapp.utils.Helper;
import apps.contactsapp.utils.PermissionUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ContactFetcher {
    private static final String[] PROJECTION = {
            ContactsContract.Data.CONTACT_ID,
            ContactsContract.Data.DISPLAY_NAME_PRIMARY,
            ContactsContract.Data.STARRED,
            ContactsContract.Data.PHOTO_URI,
            ContactsContract.Data.PHOTO_THUMBNAIL_URI,
            ContactsContract.Data.DATA1,
            ContactsContract.Data.MIMETYPE,
            ContactsContract.Data.IN_VISIBLE_GROUP
    };
    private static final String TAG = ContactFetcher.class.getCanonicalName();
    private static ContactListener<Contact> contactListener;

    private final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME_PRIMARY;

    private final String FILTER = DISPLAY_NAME + " NOT LIKE '%@%'";

    private ContentResolver resolver;
    private static CompositeDisposable compositeDisposable;

    private ContactFetcher(@NonNull Context context) {
        resolver = context.getContentResolver();
    }

    public static void getContacts(Activity activity, ContactListener<Contact> contactListener) {
        ContactFetcher.contactListener = contactListener;
        if (PermissionUtil.checkPermission(activity, PermissionUtil.Permissions.READ_CONTACTS)) {
            fetch(activity);
        } else {
            PermissionUtil.getPermission(activity, PermissionUtil.Permissions.READ_CONTACTS,
                    PermissionUtil.PermissionCode.READ_CONTACTS,
                    activity.getString(R.string.read_contact_permission_title),
                    null);
        }
    }


    private static void fetch(@NonNull final Context context) {
        Observable.create((ObservableOnSubscribe<Contact>)
                emitter -> new ContactFetcher(context).fetch(emitter))
                .filter(contact -> !TextUtils.isEmpty(contact.displayName))
                .sorted()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<Contact>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        getCompositeDisposable().add(d);
                    }

                    @Override
                    public void onNext(Contact contact) {
                        if (contactListener != null) contactListener.onNext(contact);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (contactListener != null) contactListener.onError(e);
                    }

                    @Override
                    public void onComplete() {
                        if (contactListener != null) contactListener.onComplete();
                    }
                });
    }

    private Cursor createCursor() {
        return resolver.query(
                ContactsContract.Data.CONTENT_URI,
                PROJECTION,
                FILTER,
                null,
                ContactsContract.Data.CONTACT_ID
        );
    }

    private void fetch(ObservableEmitter<Contact> subscriber) {
        LongSparseArray<Contact> contacts = new LongSparseArray<>();
        Cursor cursor = createCursor();
        cursor.moveToFirst();
        int idxId = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
        int idxInVisibleGroup = cursor.getColumnIndex(ContactsContract.Data.IN_VISIBLE_GROUP);
        int idxDisplayNamePrimary = cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME_PRIMARY);
        int idxStarred = cursor.getColumnIndex(ContactsContract.Data.STARRED);
        int idxPhoto = cursor.getColumnIndex(ContactsContract.Data.PHOTO_URI);
        int idxThumbnail = cursor.getColumnIndex(ContactsContract.Data.PHOTO_THUMBNAIL_URI);
        int idxMimeType = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
        int idxData1 = cursor.getColumnIndex(ContactsContract.Data.DATA1);
        while (!cursor.isAfterLast()) {
            long id = cursor.getLong(idxId);
            Contact contact = contacts.get(id, null);
            if (contact == null) {
                contact = new Contact(id);
                ColumnMapper.mapInVisibleGroup(cursor, contact, idxInVisibleGroup);
                ColumnMapper.mapDisplayName(cursor, contact, idxDisplayNamePrimary);
                ColumnMapper.mapStarred(cursor, contact, idxStarred);
                ColumnMapper.mapPhoto(cursor, contact, idxPhoto);
                ColumnMapper.mapThumbnail(cursor, contact, idxThumbnail);
                contacts.put(id, contact);
            }

            // mapping of phone number or email address
            String mimetype = cursor.getString(idxMimeType);
            switch (mimetype) {
                case ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE: {
                    ColumnMapper.mapEmail(cursor, contact, idxData1);
                    break;
                }
                case ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE: {
                    ColumnMapper.mapPhoneNumber(cursor, contact, idxData1);
                    break;
                }
            }

            cursor.moveToNext();
        }
        cursor.close();
        for (int i = 0; i < contacts.size(); i++) {
            Contact contact = contacts.valueAt(i);
            if (Helper.verifyAndAddContact(contact))
                subscriber.onNext(contact);
        }
        subscriber.onComplete();
    }

    private static CompositeDisposable getCompositeDisposable() {
        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }
        if (compositeDisposable.isDisposed()) compositeDisposable = new CompositeDisposable();
        return compositeDisposable;
    }

    public static void stop() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
