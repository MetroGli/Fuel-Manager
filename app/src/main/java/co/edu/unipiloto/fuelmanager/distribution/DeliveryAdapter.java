package co.edu.unipiloto.fuelmanager.distribution;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.model.Delivery;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.VH> {

    private final List<Delivery> items;

    public DeliveryAdapter(List<Delivery> items) { this.items = items; }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_delivery, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Delivery d = items.get(position);

        h.tvStation.setText(d.getStationName());
        h.tvFuel.setText(fuelEmoji(d.getFuelType()) + " " + d.getFuelType());
        h.tvVolume.setText(String.format(Locale.getDefault(),
                "%.1f galones", d.getVolumeGal()));

        String date = d.getDate();
        h.tvDate.setText(date != null && date.length() > 24 ? date.substring(0, 24) : date);

        if (d.getNotes() != null && !d.getNotes().isEmpty()) {
            h.tvNotes.setVisibility(View.VISIBLE);
            h.tvNotes.setText("📝 " + d.getNotes());
        } else {
            h.tvNotes.setVisibility(View.GONE);
        }

        // Color por combustible
        int color;
        switch (d.getFuelType()) {
            case "Extra": color = 0xFFFF8F00; break;
            case "ACPM":  color = 0xFF42A5F5; break;
            default:      color = 0xFFE65100; break;
        }
        h.tvFuel.setTextColor(color);
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void updateData(List<Delivery> newList) {
        items.clear();
        items.addAll(newList);
        notifyDataSetChanged();
    }

    private String fuelEmoji(String fuel) {
        switch (fuel) {
            case "Extra": return "🟡";
            case "ACPM":  return "🔵";
            default:      return "🟠";
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvStation, tvFuel, tvVolume, tvDate, tvNotes;
        VH(View v) {
            super(v);
            tvStation = v.findViewById(R.id.tvDeliveryStation);
            tvFuel    = v.findViewById(R.id.tvDeliveryFuel);
            tvVolume  = v.findViewById(R.id.tvDeliveryVolume);
            tvDate    = v.findViewById(R.id.tvDeliveryDate);
            tvNotes   = v.findViewById(R.id.tvDeliveryNotes);
        }
    }
}