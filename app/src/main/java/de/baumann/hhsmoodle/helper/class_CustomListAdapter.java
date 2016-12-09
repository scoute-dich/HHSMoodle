/*
    This file is part of the HHS Moodle WebApp.

    HHS Moodle WebApp is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    HHS Moodle WebApp is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with the Diaspora Native WebApp.

    If not, see <http://www.gnu.org/licenses/>.
 */

package de.baumann.hhsmoodle.helper;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.baumann.hhsmoodle.R;

public class class_CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemTITLE;
    private final String[] itemDES;
    private final Integer[] imgid;

    public class_CustomListAdapter(Activity context, String[] itemTITLE, @SuppressWarnings("UnusedParameters") String[] itemURL, String[] itemDES, Integer[] imgid) {
        super(context, R.layout.list_item_notes, itemTITLE);

        this.context=context;
        this.itemTITLE=itemTITLE;
        this.itemDES=itemDES;
        this.imgid=imgid;
    }

    @NonNull
    public View getView(int position, View rowView, @NonNull ViewGroup parent) {

        if (rowView == null) {
            LayoutInflater infInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = infInflater.inflate(R.layout.list_item_notes, parent, false);
        }

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon_notes);
        TextView textTITLE = (TextView) rowView.findViewById(R.id.textView_title_notes);
        TextView textDES = (TextView) rowView.findViewById(R.id.textView_des_notes);

        imageView.setImageResource(imgid[position]);
        textTITLE.setText(itemTITLE[position]);
        textDES.setText(itemDES[position]);
        return rowView;
    }
}
