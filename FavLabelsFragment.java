package com.engineerkoghar.engineerkoghar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by janardan on 3/18/17.
 */

public class FavLabelsFragment extends Fragment {
    Activity referenceActivity;
    View parentHolder;
    FragmentActivity listener;
    ListView listView;
    ArrayAdapter<String> adapter;
    Button button;
    String favLabels;

    public FavLabelsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.listener = (FragmentActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        referenceActivity = getActivity();
        parentHolder = inflater.inflate(R.layout.fav_labels_fragment, container, false);

        SharedPreferences postData = referenceActivity.getSharedPreferences(PostsFragment.PREFS_NAME, MODE_PRIVATE);
        favLabels = postData.getString("favLabelsID", "");

        listView = (ListView) parentHolder.findViewById(R.id.labelsList);
        button = (Button) parentHolder.findViewById(R.id.saveButton);

        String[] labels = getResources().getStringArray(R.array.labels_array);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_multiple_choice, labels);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(adapter);

        List<String> items = new ArrayList<String>(Arrays.asList(favLabels.split(",")));
        if (!(items.isEmpty()) && !items.get(0).equals("")) {
            for (int i = 0; i < items.size(); i++) {
                listView.setItemChecked(Integer.parseInt(items.get(i)), true);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                ArrayList<String> selectedItems = new ArrayList<String>();
                String selectedItemIds = "";
                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position = checked.keyAt(i);
                    // Add label if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i)) {
                        selectedItems.add(adapter.getItem(position));
                        selectedItemIds += position + ",";
                    }
                }

                //String[] outputStrArr = new String[selectedItems.size()];
                String selectedStr = "";

                for (int i = 0; i < selectedItems.size(); i++) {
                    //outputStrArr[i] = selectedItems.get(i);
                    selectedStr += "/" + selectedItems.get(i);
                }

                // Create a bundle object
                //Bundle b = new Bundle();
                //b.putStringArray("selectedItems", outputStrArr);

                Intent intentMain = new Intent(getContext(), HomePageActivity.class);
                HomePageActivity.currFrag = "Favourites";
                SharedPreferences postData = referenceActivity.getSharedPreferences(PostsFragment.PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = postData.edit();
                editor.putString("favLabels", selectedStr);
                editor.putString("favLabelsID", selectedItemIds);
                editor.apply();         // Apply the edits!
                startActivity(intentMain);
                getActivity().finish();
                if(getActivity().getIntent().getStringExtra("callingActivity").equals("SettingsActivity")){
                    SettingsActivity.settingsActivity.finish();
                }
            }
        });

        return parentHolder;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
