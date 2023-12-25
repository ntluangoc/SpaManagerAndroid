package com.example.demoappspa.Controller;


import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoappspa.Entity.Product;
import com.example.demoappspa.ProductAdapter;
import com.example.demoappspa.R;
import com.example.demoappspa.RecyclerItemClickListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListProductFragment extends Fragment {
    View view;
    public List<Product> list;
    ProductAdapter adapter;
    RecyclerView gvListProduct;
    EditText edtSearch;
    Spinner spnListSkin;
    Button btnAddProduct;
    String[] listSkins = {"All","Oily skin", "Combination skin", "Normal skin", "Dry skin"};

//    "All","Da dầu", "Da hỗn hợp", "Da thường", "Da khô"

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_list_product, container, false);
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);

        mapping();
        list = new ArrayList<>();
        adapter = new ProductAdapter(getActivity(), list, R.layout.activity_detail_product);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        getAll();
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        gvListProduct.setAdapter(adapter);
        gvListProduct.setLayoutManager(gridLayoutManager);
        searching();
        spnListSkin.setAdapter(new MyAdapter(getActivity(), R.layout.activity_list_skin, listSkins));
        spnListSkin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i>0) searchingBySkin(listSkins[i]);
                else {
                    Toast.makeText(getActivity(), "Show All", Toast.LENGTH_SHORT).show();
                    adapter.list = list;
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        gvListProduct.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), gvListProduct, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                openDetail(position);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddProduct();
            }
        });
        return view;
    }
    private void openAddProduct(){
        AddProductFragment addProductFragment = new AddProductFragment();
        fragmentTransaction.setCustomAnimations(R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out);  // popExit
        MainActivity.bottomNavigationView.setVisibility(View.INVISIBLE);
        fragmentTransaction.replace(R.id.frame_container, addProductFragment);
        fragmentTransaction.addToBackStack(new ListProductFragment().getClass().getSimpleName());//thêm Fragment vào stack để quay lại
        fragmentTransaction.commit();
    }
    private void openDetail(int i){
        Product product_temp = list.get(i);
        DetailProductFragment detailProductFragment = new DetailProductFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("productDetail", product_temp);
        detailProductFragment.setArguments(bundle);
        fragmentTransaction.setCustomAnimations(R.anim.slide_in,  // enter
                R.anim.fade_out,  // exit
                R.anim.fade_in,   // popEnter
                R.anim.slide_out);  // popExit
        MainActivity.bottomNavigationView.setVisibility(View.INVISIBLE);
        fragmentTransaction.replace(R.id.frame_container, detailProductFragment);
        fragmentTransaction.addToBackStack(new ListProductFragment().getClass().getSimpleName());//thêm Fragment vào stack để quay lại
        fragmentTransaction.commit();
    }
    private void getAll() {
        MainActivity.db.collection("ProductList")
                .whereEqualTo("author",  MainActivity.user.getUID())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product_temp = new Product(document.getId(), document.getString("imgProduct"), document.getString("nameProduct"), document.getString("price"), document.getString("skin"), document.getString("description"), document.getString("author"));
                                list.add(product_temp);
                                adapter.notifyDataSetChanged();
                                Log.d("AAA", "Getting data completed with ID: " + product_temp.getId());
                            }
                        } else {
                            Log.d("AAA", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }


    private void searchingBySkin(String text){
        ArrayList<Product> filteredList = new ArrayList<>();
        for (Product item : list){
            if(item.getSkin().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }
    private void mapping() {
        gvListProduct = view.findViewById(R.id.gvListProduct);
        edtSearch = view.findViewById(R.id.edtSearch);
        spnListSkin = view.findViewById(R.id.spnListSkin);
        btnAddProduct = view.findViewById(R.id.btnAddProduct);
    }
    private void filter(String text){
        ArrayList<Product> filteredList = new ArrayList<>();
        for (Product item : list){
            if(item.getNameProduct().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }
    private void searching(){
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }
    public class MyAdapter extends ArrayAdapter {

        public MyAdapter(Context context, int textViewResourceId,
                         String[] objects) {

            super(context, textViewResourceId, objects);
        }
        public View getCustomView(int position, View convertView,
                                  ViewGroup parent) {
            // Inflating the layout for the custom Spinner
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.activity_list_skin, parent, false);

            // Declaring and Typecasting the textview in the inflated layout
            TextView txtListSkin = (TextView) layout
                    .findViewById(R.id.txtListSkin);
            // Setting the text using the array
            txtListSkin.setTextSize(TypedValue.COMPLEX_UNIT_PX, 40);
            txtListSkin.setText(listSkins[position]);
            return layout;
        }
        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }
    }

}
