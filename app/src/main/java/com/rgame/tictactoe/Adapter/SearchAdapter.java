package com.rgame.tictactoe.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rgame.tictactoe.Modal.SearchDataItem;
import com.rgame.tictactoe.R;
import com.rgame.tictactoe.RealTimeGameActivity;
import com.rgame.tictactoe.RoomActivity;
import com.rgame.tictactoe.Tools.AppVariables;
import com.rgame.tictactoe.Tools.OfflineData;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ItemHolder> {

    private Context context;
    private List<SearchDataItem> list;
    private Drawable open,closed;
    private Dialog dialog,progress;
    private TextView owner_t,room_t;
    private EditText password_e;
    private Button join_b;
    private DatabaseReference databaseReference;
    private OfflineData data;

    public SearchAdapter(Context context,List<SearchDataItem>list) {
        this.context = context;
        this.open = this.context.getResources().getDrawable(R.drawable.ic_unlocked);
        this.closed = this.context.getResources().getDrawable(R.drawable.ic_locked);
        this.list = list;

        databaseReference = FirebaseDatabase.getInstance().getReference();
        data = new OfflineData(context);
        progress = new SpotsDialog.Builder().setContext(context).setTheme(R.style.custom_dialog).build();
        progress.setCancelable(false);

        this.dialog = new Dialog(context);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.room_password_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        owner_t = dialog.findViewById(R.id.password_owner_name);
        room_t = dialog.findViewById(R.id.password_room_name);
        password_e = dialog.findViewById(R.id.password_enter);
        join_b = dialog.findViewById(R.id.password_join_button);
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_search_layout,parent,false);
        return new SearchAdapter.ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, final int position) {
        holder.owner_name.setText(list.get(position).getOwner_name());
        holder.room_name.setText(list.get(position).getRoom_name());
        holder.room_type.setText(list.get(position).getRoom_type());

        if (list.get(position).getRoom_type().equals("Private")) {
            holder.room_type.setCompoundDrawablesWithIntrinsicBounds(closed,null,null,null);
        }else {
            holder.room_type.setCompoundDrawablesWithIntrinsicBounds(open,null,null,null);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.search_join_button :
                        if (list.get(position).getRoom_type().equals("Private")) {
                            showDialog(position);
                        }else {
                            databaseReference.child(list.get(position).getRoom_name()).child(AppVariables.OPONANT).setValue(data.getStoredName());
                            startingMatch(list.get(position).getRoom_name());
                        }
                        break;
                }
            }
        };

        holder.join_room.setOnClickListener(onClickListener);

    }

    public void startingMatch(final String host) {
        progress.show();
        databaseReference.child(AppVariables.START_MATCH).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(host).exists()) {
                    databaseReference.child(AppVariables.START_MATCH).child(host).removeValue();
                    progress.dismiss();
                    Intent i = new Intent(context, RealTimeGameActivity.class);
                    i.putExtra("HOST",host);
                    context.startActivity(i);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void showDialog(final int position) {
        owner_t.setText(list.get(position).getOwner_name());
        room_t.setText(list.get(position).getRoom_name());
        join_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getRoom_password().equals(password_e.getText().toString())) {
                    databaseReference.child(list.get(position).getRoom_name()).child(AppVariables.OPONANT).setValue(data.getStoredName());
                    startingMatch(list.get(position).getRoom_name());
                }else {
                    Toast.makeText(context, AppVariables.ERROR_MESSAGE,Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ItemHolder extends RecyclerView.ViewHolder {
        private TextView owner_name,room_name,room_type;
        private Button join_room;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            owner_name = itemView.findViewById(R.id.search_owner_name);
            room_name = itemView.findViewById(R.id.search_room_name);
            join_room = itemView.findViewById(R.id.search_join_button);
            room_type = itemView.findViewById(R.id.search_room_type);
        }
    }
}
