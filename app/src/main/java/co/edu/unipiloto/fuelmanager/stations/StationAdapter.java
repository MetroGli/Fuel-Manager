package co.edu.unipiloto.fuelmanager.stations;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.model.Station;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.VH> {

    public interface OnStationClick {
        void onClick(Station station);
    }

    private final List<Station>  items;
    private final OnStationClick listener;
    private static final NumberFormat COP = NumberFormat.getInstance(new Locale("es", "CO"));

    public StationAdapter(List<Station> items, OnStationClick listener) {
        this.items    = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_station, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Station s = items.get(position);

        h.tvName.setText(s.getName());
        h.tvAddress.setText(s.getAddress());
        h.tvZone.setText(s.getZone());
        h.tvCorriente.setText("$" + COP.format(s.getPriceCorriente()));
        h.tvExtra.setText("$"     + COP.format(s.getPriceExtra()));
        h.tvAcpm.setText("$"      + COP.format(s.getPriceAcpm()));

        // Badge solo en el #1
        if (position == 0) {
            h.badgeBest.setVisibility(View.VISIBLE);
            h.card.setCardBackgroundColor(0xFF1A2A1A);
        } else {
            h.badgeBest.setVisibility(View.GONE);
            h.card.setCardBackgroundColor(0xFF161B22);
        }

        // Toque → diálogo de alerta
        h.card.setOnClickListener(v -> {
            if (listener != null) listener.onClick(s);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void updateData(List<Station> newList) {
        items.clear();
        items.addAll(newList);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvRank, tvName, tvAddress, tvZone, tvCorriente, tvExtra, tvAcpm, badgeBest;

        VH(View v) {
            super(v);
            // CardView raíz no tiene android:id → usamos itemView directamente
            card        = (CardView) v;
            tvRank      = v.findViewById(R.id.tvRank);
            tvName      = v.findViewById(R.id.tvStationName);
            // IDs reales del item_station.xml existente
            tvAddress   = v.findViewById(R.id.tvAddress);
            tvZone      = v.findViewById(R.id.tvZone);
            tvCorriente = v.findViewById(R.id.tvPriceCorriente);
            tvExtra     = v.findViewById(R.id.tvPriceExtra);
            tvAcpm      = v.findViewById(R.id.tvPriceAcpm);
            // Badge opcional — puede no existir en el XML actual
            badgeBest   = v.findViewById(R.id.badgeBestPrice);
        }
    }
}