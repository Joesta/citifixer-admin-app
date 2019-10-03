package com.dso30bt.project2019.engineerdashboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dso30bt.project2019.engineerdashboard.R;
import com.dso30bt.project2019.engineerdashboard.models.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Joesta on 2019/09/16.
 */
public class RoadUsersAdapter extends RecyclerView.Adapter<RoadUsersAdapter.ViewHolder> {

    private Context context;
    private View view;
    private List<User> userList;

    public RoadUsersAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.users_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String fullName = userList.get(position).getFirstName() + " " + userList.get(position).getLastName();
        holder.tvFullName.setText(fullName);
        holder.detailsImageView.setImageDrawable(null);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvFullName;
        ImageView detailsImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            initUI();
        }

        private void initUI() {
            tvFullName = view.findViewById(R.id.fullNameText);
            detailsImageView = view.findViewById(R.id.detailsImageView);
        }
    }
}
