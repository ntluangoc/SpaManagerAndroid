package com.example.demoappspa;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.demoappspa.Controller.EditAppointmentFragment;
import com.example.demoappspa.Controller.MainActivity;
import com.example.demoappspa.Controller.ScheduleFragment;
import com.example.demoappspa.Entity.Appointment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class AppointmentAdapter extends BaseAdapter {
    Context context;
    List<Appointment> list;
    int layout;

    public AppointmentAdapter(Context context, List<Appointment> list, int layout) {
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
        TextView txtNameAppointment = view.findViewById(R.id.txtNameAppointment);
        TextView txtTimeAppointment = view.findViewById(R.id.txtTimeAppointment);
        CheckBox checkAppointment = view.findViewById(R.id.checkAppointment);

        Appointment appointment = list.get(i);
        txtNameAppointment.setText(appointment.getName());
        txtTimeAppointment.setText(appointment.getTime());
        checkAppointment.setChecked(appointment.isCheck());
        checkAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCheckChanged(appointment, checkAppointment);
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditAppointment(i);
            }
        });
        return view;
    }
    private void openEditAppointment(int i) {
        Appointment appointment = list.get(i);
        Bundle bundle = new Bundle();
        bundle.putSerializable("appointmentDetail", appointment);
        EditAppointmentFragment editAppointmentFragment = new EditAppointmentFragment();
        editAppointmentFragment.setArguments(bundle);
        FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out);  // popExit
        MainActivity.bottomNavigationView.setVisibility(View.INVISIBLE);
        fragmentTransaction.replace(R.id.frame_container, editAppointmentFragment);
        fragmentTransaction.addToBackStack(ScheduleFragment.class.getSimpleName());//thêm Fragment vào stack để quay lại
        fragmentTransaction.commit();
    }
    private void setCheckChanged(Appointment appointment, CheckBox checkAppointment){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Confirm?");
        if (appointment.isCheck() == true){
            builder.setMessage("Do you want to disable check-in this appointment?");
        } else {
            builder.setMessage("Do you want to check-in this appointment?");
        }

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (appointment.isCheck()){
                    appointment.setCheck(false);
                }else {
                    appointment.setCheck(true);
                }
                checkAppointment.setChecked(appointment.isCheck());
                MainActivity.db.collection("appointmentList").document(appointment.getId())
                        .update("check", appointment.isCheck())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("appointment", "Check-in appointment successfully");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("appointment", "Error appointment Check-in", e);
                            }
                        });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkAppointment.setChecked(appointment.isCheck());
                dialog.cancel();
            }
        });
        builder.show();
    }
}
