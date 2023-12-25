package com.example.demoappspa.Controller;

import static android.app.Activity.RESULT_OK;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.demoappspa.Entity.Product;
import com.example.demoappspa.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

public class AddProductFragment extends Fragment {
    View view;
    public static final int REQ_CODE = 222;

    ImageView imgAddProduct, imgReturnArrowAdd;
    EditText edtAddNameProduct, edtAddPrice, edtAddDescription;
    Spinner spnAddSkin;
    String[] listSkins = {"Oily skin", "Combination skin", "Normal skin", "Dry skin"};
    Button btnSaveProduct;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    Uri downloadURL;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_add_product, container, false);
        mapping();
        imgAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQ_CODE);
            }
        });
        spnAddSkin.setAdapter(new MyAdapter(getActivity(), R.layout.activity_list_skin, listSkins));
        Product product_temp = new Product();
        spnAddSkin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        product_temp.setSkin("Oily skin");
                        break;
                    case 1:
                        product_temp.setSkin("Combination skin");
                        break;
                    case 2:
                        product_temp.setSkin("Normal skin");
                        break;
                    case 3:
                        product_temp.setSkin("Dry skin");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btnSaveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImg(product_temp);
            }
        });
        imgReturnArrowAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReturnListProduct();
            }
        });
        return view;
    }
    public void SaveAddProduct(Product product_temp){
        Log.d("URL", "URL next: " + downloadURL);
        product_temp.setAuthor(MainActivity.user.getUID());
        product_temp.setImgProduct(String.valueOf(downloadURL));
        product_temp.setNameProduct(edtAddNameProduct.getText().toString().trim()); //.trim() bỏ qua khoảng trống ở đầu và cuối string
        product_temp.setPrice(edtAddPrice.getText().toString().trim());
        product_temp.setDescription(edtAddDescription.getText().toString().trim());
        MainActivity.db.collection("ProductList")
                .add(product_temp)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("AAA", "DocumentSnapshot written with ID: " + documentReference.getId());
                        Toast.makeText(getActivity(), "Add completed", Toast.LENGTH_SHORT).show();
                        ReturnListProduct();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("AAA", "Error adding document", e);
                        Toast.makeText(getActivity(), "Add failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void uploadImg(Product product_temp){
        Calendar calendar = Calendar.getInstance(); //lấy ngày hệ thống
        StorageReference mountainImagesRef = storageRef.child("ImageProductList/"+String.valueOf(MainActivity.user.getUID())+"_img_" + calendar.getTimeInMillis()+".png");
        imgAddProduct.setDrawingCacheEnabled(true);
        imgAddProduct.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgAddProduct.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getActivity(), "Failed to upload image!", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return mountainImagesRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            downloadURL = task.getResult();
                            Log.d("URL", "URL image: " +downloadURL);
                            SaveAddProduct(product_temp);
                        } else {
                            // Handle failures
                            Toast.makeText(getActivity(), "Failed to upload image!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
    //hàm lấy ảnh từ gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgAddProduct.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    private void ReturnListProduct(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        fragmentManager.popBackStack();
        fragmentTransaction.commit();
    }
    private void mapping() {
        imgReturnArrowAdd = view.findViewById(R.id.imgReturnArrowAdd);
        imgAddProduct = view.findViewById(R.id.imgAddProduct);
        edtAddNameProduct = view.findViewById(R.id.edtAddNameProduct);
        edtAddPrice = view.findViewById(R.id.edtAddPrice);
        edtAddDescription = view.findViewById(R.id.edtAddDescription);
        spnAddSkin = view.findViewById(R.id.spnAddSkin);
        btnSaveProduct = view.findViewById(R.id.btnSaveProduct);
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
            txtListSkin.setTextSize(TypedValue.COMPLEX_UNIT_PX, 60);
            txtListSkin.setGravity(Gravity.CENTER|Gravity.LEFT);
            txtListSkin.setText(listSkins[position]);
            txtListSkin.setHeight(90);
            txtListSkin.setPadding(5,0,0,0);
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
