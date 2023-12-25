package com.example.demoappspa;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demoappspa.Entity.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    Context context;
    public List<Product> list;
    int layout;

    public ProductAdapter(Context context, List<Product> list, int layout) {
        this.context = context;
        this.list = list;
        this.layout = layout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_detail_product,parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = list.get(position);
//        Picasso.get().load(product.getImgProduct()).into(holder.imgProduct);
        Glide.with(holder.imgProduct.getContext()).load(product.getImgProduct()).into(holder.imgProduct);
        holder.txtNameProduct.setText(product.getNameProduct());
        holder.txtPrice.setText(product.getPrice());
        holder.txtSkin.setText(product.getSkin());
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private View itemview;
        public ImageView imgProduct;
        public TextView txtNameProduct;
        public TextView txtPrice;
        public TextView txtSkin;

        public ViewHolder(View itemView) {
            super(itemView);
            itemview = itemView;
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtNameProduct = itemView.findViewById(R.id.txtNameProduct);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtSkin = itemView.findViewById(R.id.txtSkin);
        }

    }
    //searching
    public void filterList(ArrayList<Product> filteredList){
        list = filteredList;
        notifyDataSetChanged();
    }

}
