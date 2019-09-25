package com.dso30bt.project2019.engineerdashboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dso30bt.project2019.engineerdashboard.R;
import com.dso30bt.project2019.engineerdashboard.models.Engineer;
import com.dso30bt.project2019.engineerdashboard.models.User;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Joesta on 2019/09/16.
 */
public class EngineersAdapter extends RecyclerView.Adapter<EngineersAdapter.ViewHolder> {

    private Context context;
    private View view;
    private List<Engineer> engineerList;

    public EngineersAdapter(Context context, List<Engineer> engineerList) {
        this.context = context;
        this.engineerList = engineerList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.users_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String fullName = engineerList.get(position).getFirstName() + " " + engineerList.get(position).getLastName();
        holder.tvFullName.setText(fullName);
    }

    @Override
    public int getItemCount() {
        return engineerList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvFullName;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            initUI();
        }

        private void initUI() {
            tvFullName = view.findViewById(R.id.fullNameText);
        }
    }
}
