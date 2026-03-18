package co.edu.unipiloto.fuelmanager.alerts;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.model.PriceAlert;

public class AlertsAdapter extends RecyclerView.Adapter<AlertsAdapter.VH> {

    public interface OnAlertToggle {
        void onToggle(PriceAlert alert, boolean activate);
    }

    private final List<PriceAlert> items;
    private final OnAlertToggle    listener;
    private static final NumberFormat COP = NumberFormat.getInstance(new Locale("es", "CO"));

    public AlertsAdapter(List<PriceAlert> items, OnAlertToggle listener) {
        this.items    = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_alert, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        PriceAlert a = items.get(position);

        h.tvStation.setText(a.getStationName());
        h.tvFuel.setText(fuelEmoji(a.getFuelType()) + " " + a.getFuelType());
        h.tvPrice.setText("Precio ref: $" + COP.format(a.getLastKnownPrice()) + "/gal");

        // Color por combustible
        int color;
        switch (a.getFuelType()) {
            case "Extra": color = 0xFFFF8F00; break;
            case "ACPM":  color = 0xFF42A5F5; break;
            default:      color = 0xFFE65100; break;
        }
        h.tvFuel.setTextColor(color);

        // Toggle sin disparar listener al hacer bind
        h.switchAlert.setOnCheckedChangeListener(null);
        h.switchAlert.setChecked(a.isActive());
        h.switchAlert.setOnCheckedChangeListener((btn, checked) ->
                listener.onToggle(a, checked));
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void updateData(List<PriceAlert> newList) {
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
        TextView     tvStation, tvFuel, tvPrice;
        SwitchMaterial switchAlert;
        VH(View v) {
            super(v);
            tvStation   = v.findViewById(R.id.tvAlertStation);
            tvFuel      = v.findViewById(R.id.tvAlertFuel);
            tvPrice     = v.findViewById(R.id.tvAlertPrice);
            switchAlert = v.findViewById(R.id.switchAlert);
        }
    }
}