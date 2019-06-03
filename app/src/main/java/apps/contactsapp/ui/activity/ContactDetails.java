package apps.contactsapp.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import apps.contactsapp.R;
import apps.contactsapp.data.model.Contact;
import apps.contactsapp.utils.Helper;

public class ContactDetails extends AppCompatActivity {
    Contact mData;
    private static final String TAG = "ContactDetails";
    ImageView mProfile;
    TextView tvName,tvEMail,tvNumber,tvId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);
        mData = (Contact) getIntent().getExtras().get("c");
        Log.e(TAG, "onCreate: " +mData.toString() );
        tvName=findViewById(R.id.tv_Name);
        tvEMail=findViewById(R.id.tv_email);
        tvNumber=findViewById(R.id.tv_mobile);
        tvId=findViewById(R.id.tv_contact_id);
        mProfile=findViewById(R.id.img_user_profile);
        setUI(mData);

    }

    private void setUI(Contact mData) {
       if(!TextUtils.isEmpty(mData.displayName)){
           tvName.setText(mData.displayName);
       }
        if(!TextUtils.isEmpty(Helper.convertText(mData.emails))){
            tvEMail.setText(Helper.convertText(mData.emails));
        }
        if(!TextUtils.isEmpty(Helper.convertText(mData.phoneNumbers))){
            tvNumber.setText(Helper.convertText(mData.phoneNumbers));
        }
        tvId.setText(String.valueOf(mData.id));
        mProfile.setImageURI(mData.photo);
    }


}
