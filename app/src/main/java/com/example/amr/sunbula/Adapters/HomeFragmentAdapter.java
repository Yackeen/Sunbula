package com.example.amr.sunbula.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.amr.sunbula.R;

import java.util.List;

/**
 * Created by Amr on 22/07/2017.
 */

public class HomeFragmentAdapter extends ArrayAdapter<String> {

    private Context activity;
    private List<String> friendList;
    int resource;

    public HomeFragmentAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.resource = resource;
        this.friendList = objects;
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public String getItem(int position) {
        return friendList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderHome holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(resource, parent, false);
            holder = new ViewHolderHome(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderHome) convertView.getTag();
        }

        holder.friendName.setText(getItem(position));

        return convertView;
    }

    private class ViewHolderHome {
        private TextView friendName;

        public ViewHolderHome(View v) {
            friendName = (TextView) v.findViewById(R.id.text_item_in_home);
        }
    }
}