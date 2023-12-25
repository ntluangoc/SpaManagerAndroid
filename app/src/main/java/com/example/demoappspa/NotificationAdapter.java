package com.example.demoappspa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.demoappspa.Entity.Appointment;

import java.util.List;

public class NotificationAdapter extends BaseAdapter {
    Context context;
    List<Appointment> appointmentList;
    int layout;

    public NotificationAdapter(Context context, List<Appointment> appointmentList, int layout) {
        this.context = context;
        this.appointmentList = appointmentList;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return appointmentList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(layout, null);

        TextView txtContextNotification = view.findViewById(R.id.txtContextNotification);
        Appointment appointment = appointmentList.get(i);
        txtContextNotification.setText("You have an appointment with " + appointment.getName() + " at " + appointment.getTime());
        return view;
    }
}
