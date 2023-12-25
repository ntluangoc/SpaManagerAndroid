package com.example.demoappspa.Controller;



import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.bumptech.glide.Glide;
import com.example.demoappspa.Entity.Product;
import com.example.demoappspa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class DetailProductFragment extends Fragment {
    View view;
    ImageView imgReturnArrowDetail, imgProductDetail;
    TextView txtNameProductDetail, txtPriceDetail, txtSkinProductDetail,txtDescriptionDetail;
    Button btnEditProduct, btnDeleteProduct;
    Product productDetail;
    FragmentManager fragmentManager;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_detail_product2, container, false);
        mapping();

        fragmentManager = getActivity().getSupportFragmentManager();
        Bundle bundle = getArguments();
        productDetail = (Product) bundle.getSerializable("productDetail");
        setDetail(productDetail);
        imgReturnArrowDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReturnListProduct();
            }
        });
        btnEditProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditDialog();
            }
        });
        btnDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct();

            }
        });
        return view;
    }
    private void deleteImg(Product product_temp){
        //lấy tên ảnh
        //https://firebasestorage.googleapis.com/v0/b/democloundfirestore-c0875.appspot.com/o/ImageProductList%2F5SKlP9TvQUVdoqRZF2fHh2xQBP22_img_1660741390133.png?alt=media&token=0abc245d-fbaa-4aec-94e8-bee576968c5a
        String first_str[] = product_temp.getImgProduct().split("%");
        String first_str_temp = first_str[1].substring(2);
        String second_str[] = first_str_temp.split("\\?") ;
        String name_img = second_str[0];
        System.out.println("Tên file img nhận được là: " + name_img);
        Log.d("AAA", "Img name file: " + name_img);
        // Create a reference to the file to delete
        StorageReference desertRef = storageRef.child("ImageProductList/" + name_img);
        // Delete the file
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.d("delete", "File deleted successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });
    }
    private void deleteProduct(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Confirm?");
        builder.setMessage("Do you want to delete this product?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteImg(productDetail);
                MainActivity.db.collection("ProductList").document(productDetail.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("AAA", "DocumentSnapshot successfully deleted!");
                                Toast.makeText(getActivity(), "Delete product complete", Toast.LENGTH_SHORT).show();
                                ReturnListProduct();
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
    private void openEditDialog() {
        Toast.makeText(getActivity(), "Click edit", Toast.LENGTH_SHORT).show();
        EditProductFragment editProductFragment = new EditProductFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("productEdit", productDetail);
        editProductFragment.setArguments(bundle);
        editProductFragment.show(fragmentManager, null);

    }

    private void ReturnListProduct(){
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
        fragmentManager.popBackStack();
        fragmentTransaction.commit();
    }
    private void setDetail(Product product) {
        DocumentReference docRef = MainActivity.db.collection("ProductList").document(product.getId());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("AAA", "DocumentSnapshot data: " + document.getData());
                        Product product_temp = new Product(document.getId(), document.getString("imgProduct"), document.getString("nameProduct"), document.getString("price"), document.getString("skin"), document.getString("description"), document.getString("author"));
                        Picasso.get().load(product_temp.getImgProduct()).into(imgProductDetail);
                        txtNameProductDetail.setText(product_temp.getNameProduct());
                        txtPriceDetail.setText(product_temp.getPrice());
                        txtSkinProductDetail.setText(product_temp.getSkin());
                        txtDescriptionDetail.setText(product_temp.getDescription());
                        productDetail = product_temp;
                        Log.d("BBB", "Product detail: " + productDetail);
                    } else {
                        Log.d("AAA", "No such document");
                    }
                } else {
                    Log.d("AAA", "get failed with ", task.getException());
                }
            }
        });

    }
    private void mapping() {
        imgReturnArrowDetail = view.findViewById(R.id.imgReturnArrowDetail);
        imgProductDetail = view.findViewById(R.id.imgProductDetail);
        txtNameProductDetail = view.findViewById(R.id.txtNameProductDetail);
        txtPriceDetail = view.findViewById(R.id.txtPriceDetail);
        txtSkinProductDetail = view.findViewById(R.id.txtSkinProductDetail);
        txtDescriptionDetail = view.findViewById(R.id.txtDescriptionDetail);
        btnEditProduct = view.findViewById(R.id.btnEditProduct);
        btnDeleteProduct = view.findViewById(R.id.btnDeleteProduct);
    }
}
