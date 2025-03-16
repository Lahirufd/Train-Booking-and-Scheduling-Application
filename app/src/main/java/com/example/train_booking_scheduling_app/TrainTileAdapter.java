package com.example.train_booking_scheduling_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TrainTileAdapter extends RecyclerView.Adapter<TrainTileAdapter.TrainViewHolder> {
    private final List<Train> trainList;
    private OnTrainSelectListener onTrainSelectListener;

    // Interface for click listener
    public interface OnTrainSelectListener {
        void onTrainSelected(Train train);
    }

    // Constructor without listener for schedule viewing
    public TrainTileAdapter(List<Train> trainList) {
        this.trainList = trainList;
        this.onTrainSelectListener = null;
    }

    // Constructor with listener for seat selection
    public TrainTileAdapter(List<Train> trainList, OnTrainSelectListener onTrainSelectListener) {
        this.trainList = trainList;
        this.onTrainSelectListener = onTrainSelectListener;
    }

    @NonNull
    @Override
    public TrainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_train_tile, parent, false);
        return new TrainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrainViewHolder holder, int position) {
        Train train = trainList.get(position);
        holder.trainName.setText(train.getName());
        holder.trainNumber.setText(String.valueOf(train.getNumber()));
        holder.trainDeparture.setText(train.getDeparture());
        holder.trainArrival.setText(train.getArrival());
        holder.trainDate.setText("Date: " + train.getDate());

        // Set click listener only if one was provided
        if (onTrainSelectListener != null) {
            holder.itemView.setOnClickListener(v -> onTrainSelectListener.onTrainSelected(train));
        }
    }

    @Override
    public int getItemCount() {
        return trainList.size();
    }

    public static class TrainViewHolder extends RecyclerView.ViewHolder {
        TextView trainName, trainNumber, trainDeparture, trainArrival, trainDate;

        public TrainViewHolder(@NonNull View itemView) {
            super(itemView);
            trainName = itemView.findViewById(R.id.trainName);
            trainNumber = itemView.findViewById(R.id.trainNumber);
            trainDeparture = itemView.findViewById(R.id.trainDeparture);
            trainArrival = itemView.findViewById(R.id.trainArrival);
            trainDate = itemView.findViewById(R.id.trainDate);
        }
    }
}