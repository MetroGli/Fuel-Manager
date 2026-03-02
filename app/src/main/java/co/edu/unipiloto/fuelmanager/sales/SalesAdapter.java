package co.edu.unipiloto.fuelmanager.sales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.model.FuelSale;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.ViewHolder> {

    private final List<FuelSale> items;
    private static final NumberFormat COP = NumberFormat.getInstance(new Locale("es", "CO"));

    public SalesAdapter(List<FuelSale> items) { this.items = items; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sale, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        FuelSale s = items.get(position);

        h.tvFuelType.setText(s.getFuelType());
        h.tvVolume.setText(String.format(Locale.getDefault(), "%.1f gal", s.getVolumeGal()));
        h.tvTotal.setText("$" + COP.format(s.getTotalPrice()));

        if (s.getClientPlate() != null && !s.getClientPlate().isEmpty()) {
            h.tvPlate.setVisibility(View.VISIBLE);
            h.tvPlate.setText("🚗 " + s.getClientPlate());
        } else {
            h.tvPlate.setVisibility(View.GONE);
        }

        String date = s.getDate();
        h.tvDate.setText(date.length() > 24 ? date.substring(0, 24) : date);

        // Color por tipo de combustible
        int color;
        switch (s.getFuelType()) {
            case "Extra": color = 0xFFFF8F00; break;
            case "ACPM":  color = 0xFF42A5F5; break;
            default:      color = 0xFFE65100; break;
        }
        h.tvFuelType.setTextColor(color);
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void updateData(List<FuelSale> newList) {
        items.clear();
        items.addAll(newList);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFuelType, tvVolume, tvTotal, tvPlate, tvDate;
        ViewHolder(View v) {
            super(v);
            tvFuelType = v.findViewById(R.id.tvSaleFuel);
            tvVolume   = v.findViewById(R.id.tvSaleVolume);
            tvTotal    = v.findViewById(R.id.tvSaleTotal);
            tvPlate    = v.findViewById(R.id.tvSalePlate);
            tvDate     = v.findViewById(R.id.tvSaleDate);
        }
    }
}