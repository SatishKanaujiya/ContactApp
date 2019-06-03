package apps.contactsapp.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import apps.contactsapp.R;
import apps.contactsapp.data.model.Contact;
import apps.contactsapp.data.remote.ContactFetcher;
import apps.contactsapp.data.remote.ContactListener;
import apps.contactsapp.ui.adapters.ContactAdapters;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    Context xContext=this;
    private Handler xHandler;
    private static final int iRequestCode = 13;
    public static final int iGranted = PackageManager.PERMISSION_GRANTED;
    RecyclerView mRecyclerView;
    ArrayList<Contact> mContactList;
    ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        p();
        mContactList=new ArrayList<>();
        mRecyclerView=findViewById(R.id.rv_contacts);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(xContext));
        mDialog=new ProgressDialog(xContext);
        mDialog.setMessage("Please wait");
        mDialog.show();

    }


    private Runnable r = new Runnable() {
        @Override
        public void run() {
            getContacts();


        }
    };

//Fetch contacts and set adapter
    private void getContacts() {
        ContactFetcher.getContacts(this, new ContactListener<Contact>() {
            @Override
            public void onNext(Contact contact) {
                mContactList.add(contact);

            }

            @Override
            public void onError(Throwable error) {
                Log.e(TAG, "onError: ", error);
            }

            @Override
            public void onComplete() {
                mDialog.dismiss();
                mRecyclerView.setAdapter(new ContactAdapters(xContext,mContactList));
            }
        });
    }

    //ask for permission

    private void p() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> pl = new ArrayList<>();
            if (cp("android.permission.READ_CONTACTS")) pl.add("android.permission.READ_CONTACTS");
            String[] ps = new String[pl.size()];
            for (int i = 0; i < pl.size(); i++) ps[i] = pl.get(i);
            if (pl.size() == 0) h();
            else ActivityCompat.requestPermissions((Activity) xContext, ps, iRequestCode);
        }
//        else h();
    }

    private boolean cp(String p) {
        return ContextCompat.checkSelfPermission(xContext, p) != iGranted;
    }
    private void h() {
        xHandler = new Handler();
        xHandler.postDelayed(r, 1500);
    }

    @Override
    public void onRequestPermissionsResult(int rc, @NonNull String[] p, @NonNull int[] gr) {
        super.onRequestPermissionsResult(rc, p, gr);
        if (rc == iRequestCode) {
            int c = 0;
            for (int r : gr) if (r == iGranted) c++;
            if (gr.length == c) h();
            else pa();
        }
    }

    private void pa() {
        new AlertDialog.Builder(xContext).setCancelable(false).setTitle("Permission Needed")
                .setMessage("Permission needed to show contacts")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface di, int i) {
                        p();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface di, int i) {
                        e();
                    }
                }).show();
    }


    private void e() {
        finish();
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        p();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
   p();
    }
}
