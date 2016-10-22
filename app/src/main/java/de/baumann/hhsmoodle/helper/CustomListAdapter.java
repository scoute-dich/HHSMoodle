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

public class CustomListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] itemTITLE;
    private final String[] itemURL;
    private final String[] itemDES;
    private final Integer[] imgid;

    public CustomListAdapter(Activity context, String[] itemTITLE, String[] itemURL, String[] itemDES, Integer[] imgid) {
        super(context, R.layout.list_item, itemTITLE);

        this.context=context;
        this.itemTITLE=itemTITLE;
        this.itemURL=itemURL;
        this.itemDES=itemDES;
        this.imgid=imgid;
    }

    @NonNull
    public View getView(int position, View rowView, @NonNull ViewGroup parent) {

        if (rowView == null) {
            LayoutInflater infInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = infInflater.inflate(R.layout.list_item, parent, false);
        }

        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        TextView textTITLE = (TextView) rowView.findViewById(R.id.textView_title);
        TextView textURL = (TextView) rowView.findViewById(R.id.textView_url);
        TextView textDES = (TextView) rowView.findViewById(R.id.textView_des);

        imageView.setImageResource(imgid[position]);
        textTITLE.setText(itemTITLE[position]);
        textURL.setText(itemURL[position]);
        textDES.setText(itemDES[position]);
        return rowView;
    }
}
