package co.edu.unipiloto.fuelmanager.normative;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.model.NormativePrice;

public class NormativePriceAdapter
        extends RecyclerView.Adapter<NormativePriceAdapter.VH> {

    private final List<NormativePrice> data = new ArrayList<>();
    private static final NumberFormat COP = NumberFormat.getInstance(new Locale("es", "CO"));

    public void setData(List<NormativePrice> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_normative_price, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        NormativePrice p = data.get(pos);

        h.tvFuel.setText(p.getFuelType());
        h.tvPrice.setText("$" + COP.format(p.getPricePerGallon()) + "/gal");
        h.tvSource.setText(p.getSource() != null ? p.getSource() : "MINMINAS");

        String date = p.getEffectiveDate();
        h.tvDate.setText(date != null && date.length() > 24 ? date.substring(0, 24) : date);

        // Color por tipo
        int color;
        switch (p.getFuelType()) {
            case "EXTRA":     color = 0xFFFF8F00; break;
            case "ACPM":      color = 0xFF42A5F5; break;
            default:          color = 0xFFE65100; break;
        }
        h.tvFuel.setTextColor(color);
        h.tvPrice.setTextColor(color);
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvFuel, tvPrice, tvDate, tvSource;
        VH(View v) {
            super(v);
            tvFuel   = v.findViewById(R.id.tvFuel);
            tvPrice  = v.findViewById(R.id.tvPrice);
            tvDate   = v.findViewById(R.id.tvDate);
            tvSource = v.findViewById(R.id.tvSource);
        }
    }
}