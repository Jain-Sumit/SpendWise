package com.example.expmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private Map<String, Long> mMap;
    public static TextView keyTextView,valueView,percentView;

    public MyAdapter(Map<String, Long> data) {
        mMap = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
            keyTextView = itemView.findViewById(R.id.textView1);
            valueView = itemView.findViewById(R.id.textView2);
            percentView = itemView.findViewById(R.id.textView3);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String key = (String) mMap.keySet().toArray()[position];
        Long value = mMap.get(key);
        Float percentage = null;
        Float totalFloat = null;


        for (Long val : mMap.values()) {
            if (totalFloat == null) {
                totalFloat = Float.valueOf(val);
            } else {
                totalFloat += Float.valueOf(val);
            }
        }

        if (totalFloat != null && value != null) {
            Float valueFloat = Float.valueOf(value);
            Float ratio = valueFloat / totalFloat;
            percentage = ratio * 100;
        }

        keyTextView.setText(key);String capitalizedKey = key.substring(0, 1).toUpperCase() + key.substring(1);
        keyTextView.setText(capitalizedKey);
        valueView.setText("₹" + value.toString());
        percentView.setText(String.format("%.1f", percentage) + "%");
    }


    @Override
    public int getItemCount() {
        return mMap.size();
    }
}


