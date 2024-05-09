package com.pwdmngr.passwordmanager.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pwdmngr.passwordmanager.MainActivity;
import com.pwdmngr.passwordmanager.PwdView;
import com.pwdmngr.passwordmanager.R;
import com.pwdmngr.passwordmanager.model.PwdModel;
import com.pwdmngr.passwordmanager.util.DatabaseHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PwdAdapter extends RecyclerView.Adapter<PwdAdapter.ViewHolder> {
    private List<PwdModel> pwdList;
    private MainActivity activity;

    public PwdAdapter(MainActivity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pwd_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       PwdModel item = pwdList.get(position);
       // Set the password title on the card
       holder.text.setText(item.getTitle());

       final Intent i = new Intent(activity, PwdView.class);

       // Add the data to the intent
        int id  = item.getId();
        i.putExtra("id", id);

       // Show all data when card is clicked
       holder.card.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               activity.startActivity(i);
           }
       });
    }

    @Override
    public int getItemCount() {
        return pwdList.size();
    }

    public void update(List<PwdModel> pwdList) {
        this.pwdList = pwdList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView card;
        TextView text;
        ViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.card_text);
            card = view.findViewById(R.id.card);
        }
    }
}
