package com.example.dondetoy;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.sql.ClientInfoStatus;
import java.util.List;

public class ListLocAdapter extends BaseAdapter {
    private Context context;
    private List<Location> tmpLocations;
    private LayoutInflater inflter;

    public ListLocAdapter(Context applicationContext, List<Location> locations) {
        this.context = applicationContext;
        this.tmpLocations = locations;
        this.inflter = (LayoutInflater.from(applicationContext));
    }

    public List<Location> getTmpLocations() {
        return tmpLocations;
    }
    public void setTmpLocations(List<Location> tmpLocations) {
        this.tmpLocations = tmpLocations;
    }


    @Override
    public int getCount() {
        return this.tmpLocations.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflter.inflate(R.layout.lisview_item, null);

        Location tmpLoc = tmpLocations.get(position);

        TextView tv_lat = convertView.findViewById(R.id.tv_latitud);
        TextView tv_long = convertView.findViewById(R.id.tv_longitud);

        tv_lat.setText(String.valueOf(tmpLoc.getLatitude()));
        tv_long.setText(String.valueOf(tmpLoc.getLongitude()));

        return convertView;
    }
}
