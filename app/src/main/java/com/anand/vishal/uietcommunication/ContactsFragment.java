package com.anand.vishal.uietcommunication;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends ListFragment implements AdapterView.OnItemClickListener {


    String[] number ={"2570636","7388643788","9839105362","9307071522",
            "9936574974","9451973786","9838978461","8574479106"};


    public ContactsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.contacts,/* android.R.layout.simple_list_item_1*/ R.layout.contacts_list);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number[position]));
        startActivity(callIntent);

    }
}
