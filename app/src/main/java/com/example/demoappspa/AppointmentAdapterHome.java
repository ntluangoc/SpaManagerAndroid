package com.example.demoappspa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.demoappspa.Entity.Appointment;
import com.example.demoappspa.R;
import java.util.List;

public class AppointmentAdapterHome extends BaseAdapter {
    Context context;
    List<Appointment> list;
    int layout;

    public AppointmentAdapterHome(Context context, List<Appointment> list, int layout) {
        this.context = context;
        this.list = list;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return list.size();
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
        //
        TextView txtNameAppointment = view.findViewById(R.id.txtNameHomeAppointment);
        TextView txtTimeAppointment = view.findViewById(R.id.txtTimeHomeAppointment);
        ImageView imgCheckAppointment = view.findViewById(R.id.imgCheckAppointment);

        Appointment appointment = list.get(i);
        txtNameAppointment.setText(appointment.getName());
        txtTimeAppointment.setText(appointment.getTime());
        if (appointment.isCheck()){
            imgCheckAppointment.setImageResource(R.drawable.baseline_check_circle_white_24dp);
        } else imgCheckAppointment.setImageResource(R.drawable.baseline_check_circle_outline_white_24dp);
        return view;
    }


}
