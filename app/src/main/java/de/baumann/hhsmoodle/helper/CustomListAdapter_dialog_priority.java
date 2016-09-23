package de.baumann.hhsmoodle.helper;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.baumann.hhsmoodle.R;

public class CustomListAdapter_dialog_priority extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemTITLE;
    private final Integer[] imgid;

    public CustomListAdapter_dialog_priority(Activity context, String[] itemTITLE, String[] itemPRI, Integer[] imgid) {
        super(context, R.layout.list_item, itemTITLE);

        this.context=context;
        this.itemTITLE=itemTITLE;
        this.imgid=imgid;
    }

    public View getView(int position,View rowView,ViewGroup parent) {

        if (rowView == null) {
            LayoutInflater infInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = infInflater.inflate(R.layout.list_item_dialog, parent, false);
        }

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView textTITLE = (TextView) rowView.findViewById(R.id.textView_title);

        imageView.setImageResource(imgid[position]);
        textTITLE.setText(itemTITLE[position]);
        return rowView;
    }
}
