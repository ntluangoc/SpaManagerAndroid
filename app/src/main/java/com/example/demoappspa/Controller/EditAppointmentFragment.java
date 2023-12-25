package com.example.demoappspa.Controller;

import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.demoappspa.Entity.Appointment;
import com.example.demoappspa.Entity.Product;
import com.example.demoappspa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.DocumentReference;

import java.text.ParseException;

public class EditAppointmentFragment extends Fragment {
    View view;
    ImageView imgReturnArrowEditSchedule;
    EditText edtEditNameAppointment,edtEditNote;
    TextView txtEditTimeAppointment, txtEditDateAppointment;
    Button btnEditTimeAppointment, btnEditDateAppointment, btnEditAppointment, btnDeleteAppointment;
    MaterialTimePicker timePicker;
    MaterialDatePicker datePicker;
    FragmentManager fragmentManager;
    Appointment appointmentDetail;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_edit_schedule, container, false);
        mapping();
        fragmentManager = getActivity().getSupportFragmentManager();
        Bundle bundle = getArguments();
        appointmentDetail = (Appointment) bundle.getSerializable("appointmentDetail");
        Log.d("Appointment", "Appointment Edit: " + appointmentDetail.toString());
        setAppointmentDetail();
        timePicker = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(18).setMinute(02)
                .setTitleText("Choosing time ").build();
        datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Choosing date").build();
        btnEditTimeAppointment.setOnClickListener(new View.OnClickListener() {
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
        btnEditDateAppointment.setOnClickListener(new View.OnClickListener() {
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
        imgReturnArrowEditSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnSchedule();
            }
        });
        btnEditAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveAppointment(appointmentDetail);
            }
        });
        btnDeleteAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAppointment();
            }
        });
        return view;
    }

    private void deleteAppointment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Confirm?");
        builder.setMessage("Do you want to delete this appointment?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.db.collection("appointmentList").document(appointmentDetail.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("AAA", "DocumentSnapshot successfully deleted!");
                                Toast.makeText(getActivity(), "Delete product complete", Toast.LENGTH_SHORT).show();
                                returnSchedule();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("AAA", "Error deleting document", e);
                            }
                        });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void saveAppointment(Appointment appointment) {
        appointment.setName(edtEditNameAppointment.getText().toString().trim());
        appointment.setTime(txtEditTimeAppointment.getText().toString());
        appointment.setDate(txtEditDateAppointment.getText().toString());
        appointment.setNote(edtEditNote.getText().toString().trim());
        MainActivity.db.collection("appointmentList").document(appointment.getId())
                .set(appointment)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getActivity(), "Edit appointment succesfully", Toast.LENGTH_SHORT).show();
                        returnSchedule();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void setAppointmentDetail(){
        edtEditNameAppointment.setText(appointmentDetail.getName());
        txtEditTimeAppointment.setText(appointmentDetail.getTime());
        txtEditDateAppointment.setText(appointmentDetail.getDate());
        edtEditNote.setText(appointmentDetail.getNote());
    }
    private void returnSchedule() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        fragmentManager.popBackStack();
        fragmentTransaction.commit();
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
        txtEditTimeAppointment.setText( hour +":"+ minutes);
    }
    private void setDateText() {
        String date_str[] = datePicker.getHeaderText().split(" ");
        String day = date_str[0];
        String month_temp[] = date_str[2].split(",");
        String month = month_temp[0];
        String year = date_str[3];
        txtEditDateAppointment.setText(day + "/" + month + "/" + year);
    }
    private void mapping() {
        imgReturnArrowEditSchedule = view.findViewById(R.id.imgReturnArrowEditSchedule);
        edtEditNameAppointment = view.findViewById(R.id.edtEditNameAppointment);
        edtEditNote = view.findViewById(R.id.edtEditNote);
        txtEditTimeAppointment = view.findViewById(R.id.txtEditTimeAppointment);
        txtEditDateAppointment = view.findViewById(R.id.txtEditDateAppointment);
        btnEditTimeAppointment = view.findViewById(R.id.btnEditTimeAppointment);
        btnEditDateAppointment = view.findViewById(R.id.btnEditDateAppointment);
        btnEditAppointment = view.findViewById(R.id.btnEditAppointment);
        btnDeleteAppointment = view.findViewById(R.id.btnDeleteAppointment);

    }
}
