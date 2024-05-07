package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private List<UsersList.User> filteredList;

    public UserAdapter(List<UsersList.User> userList) {
        this.filteredList = new ArrayList<>(userList);
    }

    public void filterList(List<UsersList.User> filteredList) {
        this.filteredList = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UsersList.User user = filteredList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView emailTextView;
        TextView firstNameTextView;
        TextView lastNameTextView;
        ImageView avatarImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
            firstNameTextView = itemView.findViewById(R.id.firstNameTextView);
            lastNameTextView = itemView.findViewById(R.id.lastNameTextView);
            avatarImageView = itemView.findViewById(R.id.avatarImageView);
        }

        public void bind(UsersList.User user) {
            emailTextView.setText(user.getEmail());
            firstNameTextView.setText(user.getFirst_name());
            lastNameTextView.setText(user.getLast_name());
            Picasso.get().load(user.getAvatar()).into(avatarImageView);
        }
    }
}
