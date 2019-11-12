package com.dso30bt.project2019.engineerdashboard.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.dso30bt.project2019.engineerdashboard.R;
import com.dso30bt.project2019.engineerdashboard.models.Report;
import com.dso30bt.project2019.engineerdashboard.utils.Utils;

import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Joesta on 2019/09/16.
 */
public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    private Context context;
    private View view;
    private List<Report> reportList;

    public ReportAdapter(Context context, List<Report> reportList) {
        this.context = context;
        this.reportList = reportList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.report_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Date reportDate = reportList.get(position).getReportDate();
        double latitude = reportList.get(position).getPothole().getCoordinates().getLatitude();
        double longitude = reportList.get(position).getPothole().getCoordinates().getLongitude();
        String status = reportList.get(position).getStatus().getDescription();
        String assignedTo = "N/A";

        if (reportList.get(position).getConstructor() != null) {
            assignedTo = reportList.get(position).getConstructor().getFirstName() + reportList.get(position).getConstructor().getLastName();
        }

        StringBuffer sb = new StringBuffer();
        sb
                .append("Date Report\t: ").append(reportDate).append(System.lineSeparator())
                .append("Latitude\t: ").append(latitude).append(System.lineSeparator())
                .append("Longitude\t: ").append(longitude).append(System.lineSeparator())
                .append("Status\t: ").append(status).append(System.lineSeparator())
                .append("Assigned to\t: ").append(assignedTo).append(System.lineSeparator());

        holder.tvReportDetails.setText(sb.toString());
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvReportDetails;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            initUI();
        }

        private void initUI() {
            tvReportDetails = view.findViewById(R.id.reportText);
        }
    }
}
