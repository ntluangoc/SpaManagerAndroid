package com.example.demoappspa.Controller;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.demoappspa.Entity.Appointment;
import com.example.demoappspa.NotificationAdapter;
import com.example.demoappspa.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {
    View view;
    ImageView imgReturnOfNotification;
    ListView lvNotification;
    List<Appointment> appointmentList;
    NotificationAdapter notificationAdapter;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        mapping();
        appointmentList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Log.d("calendar", "Calendar: " + calendar.toString());
        getAppointmentByDate(calendar);
        imgReturnOfNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReturnBeforeFragment();
            }
        });
        return view;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setNotificationNumber(){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String hour = String.valueOf(LocalTime.now().getHour());
        int hourNext_temp = LocalTime.now().getHour();
        if (LocalTime.now().getHour() < 23){
            hourNext_temp = LocalTime.now().getHour() + 1;
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
        appointmentList.clear();
        appointmentList.addAll(listAppointmentNotification);
        notificationAdapter = new NotificationAdapter(getActivity(), appointmentList, R.layout.fragment_index_notification);
        lvNotification.setAdapter(notificationAdapter);
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
                        setNotificationNumber();
                    }
                });

    }
    private void ReturnBeforeFragment(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        fragmentManager.popBackStack();
        fragmentTransaction.commit();
    }
    private void mapping() {
        imgReturnOfNotification = view.findViewById(R.id.imgReturnOfNotification);
        lvNotification = view.findViewById(R.id.lvNotification);
    }
}
