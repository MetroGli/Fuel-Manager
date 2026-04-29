package co.edu.unipiloto.fuelmanager.distribution;

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
import co.edu.unipiloto.fuelmanager.data.model.WholesalePrice;

public class WholesaleAdapter extends RecyclerView.Adapter<WholesaleAdapter.VH> {

    private final List<WholesalePrice> items;
    private static final NumberFormat COP = NumberFormat.getInstance(new Locale("es", "CO"));

    public WholesaleAdapter(List<WholesalePrice> items) { this.items = items; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wholesale, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        WholesalePrice wp = items.get(pos);
        h.tvStation.setText(wp.getStationName());
        h.tvFuel.setText(fuelEmoji(wp.getFuelType()) + " " + wp.getFuelType());
        h.tvPrice.setText("$" + COP.format(wp.getPricePerGallon()) + "/gal");
        String date = wp.getEffectiveDate();
        h.tvDate.setText(date != null && date.length() > 24 ? date.substring(0, 24) : date);

        int color;
        switch (wp.getFuelType()) {
            case "Extra": color = 0xFFFF8F00; break;
            case "ACPM":  color = 0xFF42A5F5; break;
            default:      color = 0xFFE65100; break;
        }
        h.tvFuel.setTextColor(color);
        h.tvPrice.setTextColor(color);
    }

    @Override public int getItemCount() { return items.size(); }

    public void updateData(List<WholesalePrice> list) {
        items.clear(); items.addAll(list); notifyDataSetChanged();
    }

    private String fuelEmoji(String fuel) {
        switch (fuel) { case "Extra": return "🟡"; case "ACPM": return "🔵"; default: return "🟠"; }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvStation, tvFuel, tvPrice, tvDate;
        VH(View v) {
            super(v);
            tvStation = v.findViewById(R.id.tvWholesaleStation);
            tvFuel    = v.findViewById(R.id.tvWholesaleFuel);
            tvPrice   = v.findViewById(R.id.tvWholesalePrice);
            tvDate    = v.findViewById(R.id.tvWholesaleDate);
        }
    }
}
