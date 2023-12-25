package com.example.demoappspa.Controller;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.example.demoappspa.AppointmentAdapter;
import com.example.demoappspa.Entity.Appointment;
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

public class ScheduleFragment extends Fragment{
    View view;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    CalendarView calendarView;
    Button btnAddAppointment;
    ListView lvListAppointment;
    List<Appointment> appointmentList;
    AppointmentAdapter appointmentAdapter;
    Calendar calendar;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_schedule, container, false);
        mapping();
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        appointmentList =  new ArrayList<>();
        calendar = Calendar.getInstance();
        Log.d("calendar", "Calendar: " + calendar.toString());
        getAppointmentByDate(calendar);
        appointmentAdapter = new AppointmentAdapter(getActivity(), appointmentList, R.layout.fragment_index_appointment);
        Log.d("List", appointmentList.toString());
        lvListAppointment.setAdapter(appointmentAdapter);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDayClick(@NonNull EventDay eventDay) {
                calendar = eventDay.getCalendar();
                Toast.makeText(getActivity(), "CLick day: " + calendar.getTime(), Toast.LENGTH_SHORT).show();
                getAppointmentByDate(calendar);
                appointmentAdapter.notifyDataSetChanged();
            }
        });
        btnAddAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddAppointment();
            }
        });
        return view;
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
            appointmentAdapter.notifyDataSetChanged();
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
//                                   appointmentAdapter.notifyDataSetChanged();
                                   Log.d("appointment", "Appointment by Date: " + appointment.toString());
                                   Log.d("List", "List after: " + appointmentList.toString());
                               }
                               sortTime();
                               appointmentAdapter.notifyDataSetChanged();
                           }
                       });

    }

    private void openAddAppointment() {
        AddAppointmentFragment addAppointmentFragment = new AddAppointmentFragment();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out);  // popExit
        MainActivity.bottomNavigationView.setVisibility(View.INVISIBLE);
        fragmentTransaction.replace(R.id.frame_container, addAppointmentFragment);
        fragmentTransaction.addToBackStack(ScheduleFragment.class.getSimpleName());//thêm Fragment vào stack để quay lại
        fragmentTransaction.commit();
    }

    private void mapping(){
        calendarView = view.findViewById(R.id.calenderView);
        btnAddAppointment = view.findViewById(R.id.btnAddAppointment);
        lvListAppointment = view.findViewById(R.id.gvListAppointment);
    }
}
