package com.example.expmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class CatAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {


    private List<String> labels ;
    public static TextView keyTextView;
    public static ImageView imgview;
    Context context;


    public CatAdaptor(Context context,List<String> data) {
        this.context = context;
        labels = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View itemView) {
            super(itemView);
            keyTextView = itemView.findViewById(R.id.cat_tv);
            imgview = itemView.findViewById(R.id.del_cat);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cat_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String key = labels.get(position);
        keyTextView.setText(key);
        holder.setIsRecyclable(false);

        imgview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               delete_cat(key);
            }
        });

    }

    private void delete_cat(String label) {
        DatabaseHelper db = new DatabaseHelper(context);
        db.deleteLabel(label);
        Toast.makeText(context, label+" Category deleted", Toast.LENGTH_SHORT).show();
        labels.remove(label);
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return labels.size();
    }

    public void clear(){
        labels.clear();
        notifyDataSetChanged();
    }
}
