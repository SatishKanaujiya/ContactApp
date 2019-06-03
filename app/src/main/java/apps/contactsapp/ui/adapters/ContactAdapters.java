package apps.contactsapp.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import apps.contactsapp.R;
import apps.contactsapp.data.model.Contact;
import apps.contactsapp.ui.activity.ContactDetails;
import apps.contactsapp.utils.Helper;

/**
 * Created by Satish on 03/06/2019 3:24 PM.
 */
public class ContactAdapters extends RecyclerView.Adapter<ContactAdapters.ViewHolder> {

    private static final String TAG = "ContactAdapters";
    private ArrayList<Contact> l = new ArrayList<>();
    Context xContext;



    public ContactAdapters(Context xContext, ArrayList<Contact> l) {
        this.xContext=xContext;
        this.l=l;
    }

    @Override
    public ContactAdapters.ViewHolder onCreateViewHolder(ViewGroup p, int t) {
        return new ViewHolder(LayoutInflater.from(p.getContext()).inflate(R.layout.row_contact, p, false));
    }

    @Override
    public void onBindViewHolder(ContactAdapters.ViewHolder h, int i) {
        h.bind(l.get(i),i);
    }

    @Override
    public int getItemCount() {
        return l.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvNumber;
        CardView mCardDetails ;


        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_Name);
            tvNumber = v.findViewById(R.id.tv_Mobile);
            mCardDetails = v.findViewById(R.id.card_details);



        }

        void bind(final Contact m, int i) {

            tvName.setText(m.displayName);
            tvNumber.setText(Helper.convertText(m.phoneNumbers));
            mCardDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    xContext.startActivity(new Intent(xContext,ContactDetails.class).putExtra("c",m));
                }
            });
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


}