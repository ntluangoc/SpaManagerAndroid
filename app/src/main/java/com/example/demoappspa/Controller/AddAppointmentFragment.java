package com.example.demoappspa.Controller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.demoappspa.Entity.Appointment;
import com.example.demoappspa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddAppointmentFragment extends Fragment {
    View view;
    ImageView imgReturnArrowAddSchedule;
    EditText edtAddNameAppointment,edtAddNote;
    TextView txtAddTimeAppointment, txtAddDateAppointment;
    Button btnAddTimeAppointment, btnAddDateAppointment, btnSaveAppointment;
    MaterialTimePicker timePicker;
    MaterialDatePicker datePicker;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_schedule, container, false);
        mapping();
        timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                                                                        .setHour(18).setMinute(02)
                                                                        .setTitleText("Choosing time ").build();
        datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Choosing date").build();
        btnAddTimeAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_TIME_PICKER");
            }
        });
        timePicker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTimeText();
            }
        });
        btnAddDateAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.show(getActivity().getSupportFragmentManager(),"MATERIAL_DATE_PICKER");
            }
        });
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                setDateText();
            }
        });
        imgReturnArrowAddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnSchedule();
            }
        });
        btnSaveAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    saveAppointment();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        return view;
    }
    private void setTimeText(){
        String minutes = String.valueOf(timePicker.getMinute());
        if (Integer.parseInt(minutes)>=0 && Integer.parseInt(minutes)<10){
            minutes = "0" + minutes;
        }
        String hour = String.valueOf(timePicker.getHour());
        if (Integer.parseInt(hour)>=0 && Integer.parseInt(hour)<10){
            hour = "0" + hour;
        }
        txtAddTimeAppointment.setText( hour +":"+ minutes);
    }
    private void setDateText() {
        String date_str[] = datePicker.getHeaderText().split(" ");
        String day = date_str[0];
        String month_temp[] = date_str[2].split(",");
        String month = month_temp[0];
        String year = date_str[3];
        txtAddDateAppointment.setText(day + "/" + month + "/" + year);
    }

    private void saveAppointment() throws ParseException {
        Appointment appointment = new Appointment();
        appointment.setName(edtAddNameAppointment.getText().toString().trim());
        appointment.setTime(txtAddTimeAppointment.getText().toString());
        appointment.setDate(txtAddDateAppointment.getText().toString());
        appointment.setNote(edtAddNote.getText().toString().trim());
        appointment.setCheck(false);
        appointment.setAuthor(MainActivity.user.getUID());
        MainActivity.db.collection("appointmentList").add(appointment)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("appointment", "Add appointment successfully");
                            Toast.makeText(getActivity(), "Add appointment successfully", Toast.LENGTH_SHORT).show();
                            returnSchedule();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("appointment", "Add appointment faild: " + e.getMessage().toString());
                        }
                    });
    }

    private void returnSchedule() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        fragmentManager.popBackStack();
        fragmentTransaction.commit();
    }

    private void mapping() {
        edtAddNameAppointment = view.findViewById(R.id.edtAddNameAppointment);
        imgReturnArrowAddSchedule = view.findViewById(R.id.imgReturnArrowAddSchedule);
        txtAddTimeAppointment = view.findViewById(R.id.txtAddTimeAppointment);
        btnAddTimeAppointment = view.findViewById(R.id.btnAddTimeAppointment);
        txtAddDateAppointment = view.findViewById(R.id.txtAddDateAppointment);
        btnAddDateAppointment = view.findViewById(R.id.btnAddDateAppointment);
        edtAddNote = view.findViewById(R.id.edtAddNote);
        btnSaveAppointment = view.findViewById(R.id.btnSaveAppointment);
    }
}
