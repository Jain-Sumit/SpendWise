package com.example.expmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.MyViewHolder> {
    private Context context;
    private OnItemsClick onItemsClick;
    private List<ExpenseModel> expenseModelList;

    public ExpenseAdapter(Context context, OnItemsClick onItemsClick) {
        this.context = context;
        this.onItemsClick = onItemsClick;
        expenseModelList = new ArrayList<>();
    }

    public void add(ExpenseModel expenseModel) {
        expenseModelList.add(expenseModel);
        notifyDataSetChanged();
    }

    public void clear() {
        expenseModelList.clear();
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ExpenseModel expenseModel = expenseModelList.get(position);
        long t = expenseModel.getTime();
        Date date = new Date(t);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yy", Locale.getDefault());
        String formattedDate = dateFormat.format(date);

        if (position == 0) {
            holder.date_tv.setVisibility(View.VISIBLE);
            holder.date_tv.setText(formattedDate);
        } else {
            ExpenseModel expenseModel1 = expenseModelList.get(position - 1);
            long t1 = expenseModel1.getTime();
            Date date1 = new Date(t1);
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd MMM yy", Locale.getDefault());
            String formattedDate1 = dateFormat1.format(date1);

            if (formattedDate.equals(formattedDate1)) {
                holder.date_tv.setVisibility(View.GONE);
            } else {
                holder.date_tv.setVisibility(View.VISIBLE);
                holder.date_tv.setText(formattedDate);
            }
        }

        String cat = expenseModel.getCategory().substring(0, 1).toUpperCase() + expenseModel.getCategory().substring(1);

        holder.category.setText(cat);
        holder.amount.setText("₹ " + expenseModel.getAmount());
        holder.itemView.setBackgroundColor(Color.TRANSPARENT);

        String priority = expenseModel.getType();

        if (priority.equals("Expense")) {
            holder.amount.setTextColor(Color.parseColor("#E57373"));
        } else {
            holder.amount.setTextColor(Color.parseColor("#81C784"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("expenseId", expenseModelList.get(position).getExpenseId());
                String a = String.valueOf(expenseModelList.get(position).getAmount());
                intent.putExtra("amount", a);
                intent.putExtra("uid", expenseModelList.get(position).getUid());
                intent.putExtra("category", expenseModelList.get(position).getCategory());
                intent.putExtra("note", expenseModelList.get(position).getNote());
                intent.putExtra("type", expenseModelList.get(position).getType());
                context.startActivity(intent);
            }
        });

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return expenseModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView category;
        TextView amount;
        TextView date_tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            amount = itemView.findViewById(R.id.amount);
            date_tv = itemView.findViewById(R.id.d);
        }
    }
}
