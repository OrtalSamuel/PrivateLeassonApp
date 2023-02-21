package com.example.privateleassonapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.MiniPostHolder> implements RecyclerView.OnItemTouchListener{

    public ArrayList<Lesson> lessons;
    private final Context context;
    private final boolean  isTutor;

    public LessonAdapter(Context context, ArrayList<Lesson> lessons,boolean isTutor ) {
        this.context = context;
        this.lessons = lessons;
        this.isTutor = isTutor;
    }

    @NonNull
    @Override
    public MiniPostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.lesson_tile, parent, false);
        return new MiniPostHolder(v);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MiniPostHolder holder, @SuppressLint("RecyclerView") int position) {
        if(isTutor) {
            holder.lesson_tile_TXT_UserName.setText("A lesson with " + lessons.get(position).studentName);
            holder.dateLine.setVisibility(View.GONE);
            holder.phoneLine.setVisibility(View.GONE);
        }
        else {
            holder.lesson_tile_TXT_UserName.setText("A lesson with " + lessons.get(position).tutorName);
            holder.tvPrice.setText(lessons.get(position).price + "₪");
            holder.tvPhone.setText(lessons.get(position).tutorPhone);
        }
        if(lessons.get(position).hour.length()<5) lessons.get(position).hour += "0";
        holder.lesson_tile_TXT_time.setText(lessons.get(position).day + " , " + lessons.get(position).hour);
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }


    public static class MiniPostHolder extends RecyclerView.ViewHolder {

        TextView lesson_tile_TXT_UserName;
        TextView lesson_tile_TXT_time;
        TextView tvPrice;
        TextView tvPhone;
        ImageView imageView;

        LinearLayout phoneLine, dateLine;

        public MiniPostHolder(@NonNull View itemView) {
            super(itemView);
            lesson_tile_TXT_UserName = itemView.findViewById(R.id.lesson_tile_TXT_UserName);
            lesson_tile_TXT_time = itemView.findViewById(R.id.lesson_tile_TXT_time);
            imageView = itemView.findViewById(R.id.imageView);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            phoneLine = itemView.findViewById(R.id.phoneLine);
            dateLine = itemView.findViewById(R.id.dateLine);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

}