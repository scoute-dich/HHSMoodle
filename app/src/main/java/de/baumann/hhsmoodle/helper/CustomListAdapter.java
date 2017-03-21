package de.baumann.hhsmoodle.helper;


import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.baumann.hhsmoodle.R;


public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;

    private final ArrayList<String> itemTITLE;
    private final ArrayList<String> itemCOUNT;

    @SuppressWarnings("UnusedParameters")
    public CustomListAdapter(Activity context, ArrayList<String> itemTITLE, ArrayList<String> itemCOUNT) {
        super(context, R.layout.list_item_count, itemTITLE);

        this.context=context;
        this.itemTITLE=itemTITLE;
        this.itemCOUNT=itemCOUNT;
    }

    @NonNull
    public View getView(int position, View rowView, @NonNull ViewGroup parent) {

        if (rowView == null) {
            LayoutInflater infInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = infInflater.inflate(R.layout.list_item_count, parent, false);
        }

        TextView textTITLE = (TextView) rowView.findViewById(R.id.count_title);
        TextView textDES = (TextView) rowView.findViewById(R.id.count_count);

        textTITLE.setText(itemTITLE.get(position));
        textDES.setText(itemCOUNT.get(position));

        return rowView;
    }
}
