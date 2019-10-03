package com.dso30bt.project2019.engineerdashboard.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.dso30bt.project2019.engineerdashboard.R;
import com.dso30bt.project2019.engineerdashboard.models.Person;
import com.dso30bt.project2019.engineerdashboard.repository.UserImpl;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Joesta on 2019/09/30.
 */
public class RoadUsersConstructorsAdapter extends RecyclerView.Adapter<RoadUsersConstructorsAdapter.ViewHolder>
        implements Filterable {

    private View view;
    private Context context;
    private List<Person> usersList;
    private List<Person> userFilteredList;
    private String mFirstName;
    private String mLastName;

    public RoadUsersConstructorsAdapter(Context context, List<Person> usersList) {
        this.context = context;
        this.usersList = usersList;
        this.userFilteredList = usersList;
    }

    @NonNull
    @Override
    public RoadUsersConstructorsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new
                ViewHolder(LayoutInflater.from(context).inflate(R.layout.road_user_constructor_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RoadUsersConstructorsAdapter.ViewHolder holder, int position) {
        mFirstName = userFilteredList.get(position).getFirstName();
        mLastName = userFilteredList.get(position).getLastName();
        String role = "Role : " + userFilteredList.get(position).getRole().getRoleDescription().toLowerCase();

        String fullNames = mFirstName + " " + mLastName;
        holder.tvFullNamesText.setText(fullNames);
        holder.tvUserRoleText.setText(role);
    }

    @Override
    public int getItemCount() {
        return userFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return
                new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        String charFilter = constraint.toString();
                        if (charFilter.isEmpty()) {
                            userFilteredList = usersList;
                        } else {

                            List<Person> filteredList = new ArrayList<>();
                            // traverse through all users
                            for (Person person : usersList) {
                                // filter user by first name or last name
                                if (person.getFirstName().toLowerCase().contains(charFilter)
                                        || person.getLastName().toLowerCase().contains(charFilter)) {
                                    // add filtered person to filtered list
                                    filteredList.add(person);
                                }
                            }

                            userFilteredList = filteredList;
                        }

                        FilterResults filterResults = new FilterResults();
                        filterResults.values = userFilteredList;
                        return filterResults;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        userFilteredList = (List<Person>) results.values;
                        notifyDataSetChanged();
                    }
                };
    }

    // class holds widgets
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvFullNamesText;
        TextView tvUserRoleText;
        ImageView deleteImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            iniUIFor(R.id.fullNameText, R.id.userRoleText, R.id.deleteImageView);
        }

        private void iniUIFor(int fullNameText, int userRoleText, int deleteImageView) {
            this.tvFullNamesText = view.findViewById(fullNameText);
            this.tvUserRoleText = view.findViewById(userRoleText);
            this.deleteImageView = view.findViewById(deleteImageView);
            this.deleteImageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.deleteImageView:
                    showConfirmDialog();
                    break;
                default:
                    break;
            }
        }

        private void showConfirmDialog() {
           mFirstName = userFilteredList.get(getAdapterPosition()).getFirstName();
           mLastName = userFilteredList.get(getAdapterPosition()).getLastName();
           String fullNames = mFirstName + " " + mLastName;

            buildDialog("Caution - Action cannot be undone!", "Delete " + fullNames)
                    .create()
                    .show();
        }

        private AlertDialog.Builder buildDialog(String title, String message) {
            return
                    new AlertDialog.Builder(context)
                            .setTitle(title)
                            .setMessage(message)
                            .setPositiveButton("Yes", ((dialog, which) -> deleteUser()))
                            .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()));

        }

        private void deleteUser() {
            new UserImpl(context)
                    .deleteUserOrConstructor(userFilteredList.get(getAdapterPosition()));
        }
    }
}
