package com.example.demoappspa.Controller;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.demoappspa.AppointmentAdapterHome;
import com.example.demoappspa.Entity.Appointment;
import com.example.demoappspa.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nex3z.notificationbadge.NotificationBadge;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {
    View view;
    NotificationBadge NBNumber;
    TextView txtNameNextAppointment, txtTimeNextAppointment, txtNoteNextAppointment;
    Button btnNotification_home;
    List<Appointment> appointmentList;
    ListView lvListHomeAppointment;
    AppointmentAdapterHome appointmentAdapterHome;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        mapping();
        appointmentList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Log.d("calendar", "Calendar: " + calendar.toString());
        getAppointmentByDate(calendar);
        return view;
    }
    private void openNotification(){
        btnNotification_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationFragment notificationFragment = new NotificationFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out);  // popExit
                MainActivity.bottomNavigationView.setVisibility(View.INVISIBLE);
                fragmentTransaction.replace(R.id.frame_container, notificationFragment);
                fragmentTransaction.addToBackStack(HomeFragment.class.getSimpleName());//thêm Fragment vào stack để quay lại
                fragmentTransaction.commit();
            }
        });
    }
    private void setListAppointment(){
        appointmentAdapterHome = new AppointmentAdapterHome(getActivity(), appointmentList, R.layout.fragment_index_home_appointment);
        lvListHomeAppointment.setAdapter(appointmentAdapterHome);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setNotificationNumber(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String hour = String.valueOf(LocalTime.now().getHour());
        int hourNext_temp = LocalTime.now().getHour();
        if (LocalTime.now().getHour() < 23){
            hourNext_temp  += 1;
        } else{
            hourNext_temp = 0;
        }
        String hourNext = String.valueOf(hourNext_temp);
        if (LocalTime.now().getHour() < 10) {
            hour = "0" + LocalTime.now().getHour();
        }
        if (hourNext_temp <10){
            hourNext = "0" + hourNext;
        }
        String minute = String.valueOf(LocalTime.now().getMinute());
        if (LocalTime.now().getMinute() >=0 && LocalTime.now().getMinute()<10) minute = "0" + LocalTime.now().getMinute();
        String currentTime = hour + ":" +minute;
        String curentTimeNext = hourNext + ":" + minute;
        LocalTime localTime = LocalTime.parse(currentTime, dateTimeFormatter);
        LocalTime localTimeNext = LocalTime.parse(curentTimeNext, dateTimeFormatter);
        List<Appointment> listAppointmentNotification = new ArrayList<>();
        for(int i=0; i< appointmentList.size(); i++){
            if (LocalTime.parse(appointmentList.get(i).getTime(), dateTimeFormatter).compareTo(localTime) >=0 && LocalTime.parse(appointmentList.get(i).getTime(), dateTimeFormatter).compareTo(localTimeNext) <=0){
                listAppointmentNotification.add(appointmentList.get(i));
            }
        }
        Log.d("homeFragment", "List appointment notification:" + listAppointmentNotification.toString());
        if (listAppointmentNotification.isEmpty()){
            NBNumber.setNumber(0);
        }else
            NBNumber.setNumber(listAppointmentNotification.size());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setNextAppointment(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String hour = String.valueOf(LocalTime.now().getHour());
        if (LocalTime.now().getHour() < 10) hour = "0" + LocalTime.now().getHour();
            String minute = String.valueOf(LocalTime.now().getMinute());
        if (LocalTime.now().getMinute() >=0 && LocalTime.now().getMinute()<10) minute = "0" + LocalTime.now().getMinute();
        String currentTime = hour + ":" +minute;
        LocalTime localTime = LocalTime.parse(currentTime, dateTimeFormatter);
        String nextTime = "";
        for(int i=0; i< appointmentList.size(); i++){
            if (LocalTime.parse(appointmentList.get(i).getTime(), dateTimeFormatter).compareTo(localTime) >=0){
                nextTime = appointmentList.get(i).getTime();
                break;
            }
        }
        Log.d("homeFragment", "Next time: " + nextTime);
        for(int i=0; i< appointmentList.size(); i++){
            if (nextTime.equals(appointmentList.get(i).getTime())){
                txtNameNextAppointment.setText(appointmentList.get(i).getName());
                txtTimeNextAppointment.setText(appointmentList.get(i).getTime());
                txtNoteNextAppointment.setText(appointmentList.get(i).getNote());
                break;
            }
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sortTime(){
        List<LocalTime> localTimes = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        List<Appointment> newList = new ArrayList<>();
        for (Appointment appointment_temp : appointmentList){
            LocalTime localTime = LocalTime.parse(appointment_temp.getTime(), formatter);
            localTimes.add(localTime);
        }
        Collections.sort(localTimes);
        for (LocalTime localTime : localTimes){
            for (int i=0; i<appointmentList.size(); i++){
                if (appointmentList.get(i).getTime().equals(localTime.toString())){
                    newList.add(appointmentList.get(i));
                    appointmentList.remove(i);
                }
            }
        }
        appointmentList.clear();
        appointmentList.addAll(newList);
        Log.d("List", "List sort: " + appointmentList.toString());
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getAppointmentByDate(Calendar calendar) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // lưu ý, tháng bắt đầu từ 0
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String calendar_str = day + "/" + month + "/" + year;
        if (appointmentList.isEmpty() != true){
            appointmentList.clear();
        }
        MainActivity.db.collection("appointmentList")
                .whereEqualTo("author", MainActivity.user.getUID())
                .whereEqualTo("date", calendar_str)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots){
                            Appointment appointment = new Appointment(document.getId(), document.getString("name"), document.getString("time"),document.getString("date"), document.getString("note"),document.getBoolean("check"), document.getString("author") );
                            appointmentList.add(appointment);
                            Log.d("appointment", "Appointment by Date: " + appointment.toString());
                            Log.d("List", "List after: " + appointmentList.toString());
                        }
                        sortTime();
                        setNextAppointment();
                        setListAppointment();
                        setNotificationNumber();
                        openNotification();
                    }
                });

    }
    private void mapping() {
        NBNumber = view.findViewById(R.id.NBNumber);
        txtNameNextAppointment = view.findViewById(R.id.txtNameNextAppointment);
        txtTimeNextAppointment = view.findViewById(R.id.txtTimeNextAppointment);
        txtNoteNextAppointment = view.findViewById(R.id.txtNoteNextAppointment);
        lvListHomeAppointment = view.findViewById(R.id.lvListHomeAppointment);
        btnNotification_home = view.findViewById(R.id.btnNotification_home);
    }
}
