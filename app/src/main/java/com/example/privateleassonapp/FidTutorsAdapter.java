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

public class FidTutorsAdapter extends RecyclerView.Adapter<FidTutorsAdapter.MiniPostHolder> implements RecyclerView.OnItemTouchListener{

    public ArrayList<FirebaseTutor> tutors;
    private Context context;

    public FidTutorsAdapter(Context context, ArrayList<FirebaseTutor> tutorsList ) {
        this.context = context;
        this.tutors = tutorsList;
    }

    @NonNull
    @Override
    public MiniPostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.tutors_tile, parent, false);
        return new MiniPostHolder(v);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MiniPostHolder holder, @SuppressLint("RecyclerView") int position) {
       Picasso.get().load(tutors.get(position).url).into(holder.tutorsTile_IMG_Image);
        holder.tutorsTile_TXT_name.setText(tutors.get(position).userName);
        holder.tutorsTile_TXT_category.setText(tutors.get(position).category);
        holder.tutorsTile_TXT_price.setText(String.valueOf(tutors.get(position).price));
        holder.tutorsTile_TXT_score.setText(String.valueOf(tutors.get(position).price));

        holder.lTutor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewLessonActivity.class);
                Log.e("mList.get(position).toMap().toString()",tutors.get(position).toMap().toString());
                intent.putExtra("TUTOR",tutors.get(position).toMap());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return tutors.size();
    }


    public static class MiniPostHolder extends RecyclerView.ViewHolder {

        LinearLayout lTutor;
        TextView tutorsTile_TXT_name;
        TextView tutorsTile_TXT_category;
        TextView tutorsTile_TXT_price;
        TextView tutorsTile_TXT_score;
        ImageView tutorsTile_IMG_Image;


        public MiniPostHolder(@NonNull View itemView) {
            super(itemView);
            tutorsTile_IMG_Image = itemView.findViewById(R.id.tutorsTile_IMG_Image);
            lTutor = itemView.findViewById(R.id.lTutor);
            tutorsTile_TXT_name = itemView.findViewById(R.id.tutorsTile_TXT_name);
            tutorsTile_TXT_category = itemView.findViewById(R.id.tutorsTile_TXT_category);
            tutorsTile_TXT_price = itemView.findViewById(R.id.tutorsTile_TXT_price);
            tutorsTile_TXT_score = itemView.findViewById(R.id.tutorsTile_TXT_score);

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