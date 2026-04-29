package co.edu.unipiloto.fuelmanager.distribution;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.model.PriceUpdate;

public class PriceUpdateHistoryAdapter
        extends RecyclerView.Adapter<PriceUpdateHistoryAdapter.VH> {

    private final List<PriceUpdate> items;

    public PriceUpdateHistoryAdapter(List<PriceUpdate> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_price_update, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        PriceUpdate pu = items.get(pos);

        h.tvStation.setText(pu.getStationName());

        // Corriente
        h.tvCorriente.setText(String.format(
                "Corriente: $%.0f → $%.0f", pu.getOldCorriente(), pu.getNewCorriente()));
        colorDiff(h.tvCorriente, pu.getOldCorriente(), pu.getNewCorriente());

        // Extra
        h.tvExtra.setText(String.format(
                "Extra: $%.0f → $%.0f", pu.getOldExtra(), pu.getNewExtra()));
        colorDiff(h.tvExtra, pu.getOldExtra(), pu.getNewExtra());

        // ACPM
        h.tvAcpm.setText(String.format(
                "ACPM: $%.0f → $%.0f", pu.getOldAcpm(), pu.getNewAcpm()));
        colorDiff(h.tvAcpm, pu.getOldAcpm(), pu.getNewAcpm());

        // Fecha (truncar)
        String date = pu.getDate();
        h.tvDate.setText(date.length() > 24 ? date.substring(0, 24) : date);
    }

    private void colorDiff(TextView tv, double oldVal, double newVal) {
        if (newVal > oldVal)      tv.setTextColor(0xFFEF5350); // rojo — subió
        else if (newVal < oldVal) tv.setTextColor(0xFF66BB6A); // verde — bajó
        else                      tv.setTextColor(0xFFB0BEC5); // gris — igual
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void updateData(List<PriceUpdate> newList) {
        items.clear();
        items.addAll(newList);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvStation, tvCorriente, tvExtra, tvAcpm, tvDate;
        VH(View v) {
            super(v);
            tvStation   = v.findViewById(R.id.tvStationName);
            tvCorriente = v.findViewById(R.id.tvPriceCorriente);
            tvExtra     = v.findViewById(R.id.tvPriceExtra);
            tvAcpm      = v.findViewById(R.id.tvPriceAcpm);
            tvDate      = v.findViewById(R.id.tvDate);
        }
    }
}