package com.example.demoappspa.Controller;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.demoappspa.Controller.AddProductFragment;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;


public class EditProductFragment extends DialogFragment {
    View view;
    String[] listSkins = {"Oily skin", "Combination skin", "Normal skin", "Dry skin"};
    ImageView imgEditProduct;
    EditText edtEditNameProduct, edtEditPrice, edtEditDescription;
    Spinner spnEditSkin;
    Button btnEditDialogProduct, btnCancelDialogProduct;
    Product productEdit;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    Uri downloadURL;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.EditDialog);
//        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        //lấy dữ liệu từ Detail Product
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = getArguments();
        fragmentManager.getFragment(bundle, "productEdit");
        productEdit = (Product) bundle.getSerializable("productEdit");
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dialog_edit_product, container, false);
        mapping();
        setEditView();
        imgEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, AddProductFragment.REQ_CODE);
            }
        });
        spnEditSkin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        productEdit.setSkin("Oily skin");
                        break;
                    case 1:
                        productEdit.setSkin("Combination skin");
                        break;
                    case 2:
                        productEdit.setSkin("Normal skin");
                        break;
                    case 3:
                        productEdit.setSkin("Dry skin");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnEditDialogProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImg(productEdit);
                ReturnDetailProduct();
                Toast.makeText(getActivity(), "Edit product completed", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });
        btnCancelDialogProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }
    private void ReturnDetailProduct(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        fragmentManager.popBackStack();
        fragmentTransaction.commit();
    }
    private void saveEditProduct(Product product_temp){
        Log.d("URL", "URL next: " + downloadURL);


        MainActivity.db.collection("ProductList").document(product_temp.getId())
                .set(product_temp)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("BBB", "Product Edit sau khi save: " + product_temp.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }
    private void uploadImg(Product product_temp){
        Calendar calendar = Calendar.getInstance(); //lấy ngày hệ thống
        StorageReference mountainImagesRef = storageRef.child("ImageProductList/"+String.valueOf(MainActivity.user.getUID())+"_img_" + calendar.getTimeInMillis()+".png");
        imgEditProduct.setDrawingCacheEnabled(true);
        imgEditProduct.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imgEditProduct.getDrawable()).getBitmap();
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
                            product_temp.setImgProduct(String.valueOf(downloadURL));
                            product_temp.setNameProduct(edtEditNameProduct.getText().toString().trim()); //.trim() bỏ qua khoảng trống ở đầu và cuối string
                            product_temp.setPrice(edtEditPrice.getText().toString().trim());
                            product_temp.setDescription(edtEditDescription.getText().toString().trim());
                            saveEditProduct(product_temp);
                        } else {
                            // Handle failures
                            Toast.makeText(getActivity(), "Failed to upload image!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
    private void setEditView() {
        Picasso.get().load(productEdit.getImgProduct()).into(imgEditProduct);
        edtEditNameProduct.setText(productEdit.getNameProduct());
        edtEditPrice.setText(productEdit.getPrice());
        edtEditDescription.setText(productEdit.getDescription());
        spnEditSkin.setAdapter(new MyAdapter(getActivity(), R.layout.activity_list_skin, listSkins));
        //set text mặc định theo skin product
        if (productEdit.getSkin().equals("Oily skin")) spnEditSkin.setSelection(0);
        else if (productEdit.getSkin().equals("Combination skin")) spnEditSkin.setSelection(1);
        else if (productEdit.getSkin().equals("Normal skin")) spnEditSkin.setSelection(2);
        else spnEditSkin.setSelection(3);
        Log.d("BBB", "Edit product with ID: " + productEdit.getId());
    }

    //hàm lấy ảnh từ gallery
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddProductFragment.REQ_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgEditProduct.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    private void mapping() {
        imgEditProduct = view.findViewById(R.id.imgEditProduct);
        edtEditNameProduct = view.findViewById(R.id.edtEditNameProduct);
        edtEditPrice = view.findViewById(R.id.edtEditPrice);
        edtEditDescription = view.findViewById(R.id.edtEditDescription);
        spnEditSkin = view.findViewById(R.id.spnEditSkin);
        btnEditDialogProduct = view.findViewById(R.id.btnEditDialogProduct);
        btnCancelDialogProduct = view.findViewById(R.id.btnCancelDialogProduct);

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
            txtListSkin.setGravity(Gravity.CENTER | Gravity.LEFT);
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
