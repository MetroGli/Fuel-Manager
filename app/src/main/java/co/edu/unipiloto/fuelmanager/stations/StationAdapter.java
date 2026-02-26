package co.edu.unipiloto.fuelmanager.stations;

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
import co.edu.unipiloto.fuelmanager.data.model.Station;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder> {

    private final List<Station> stations;
    private static final NumberFormat COP = NumberFormat.getInstance(new Locale("es", "CO"));

    public StationAdapter(List<Station> stations) {
        this.stations = stations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_station, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Station s = stations.get(position);

        h.tvRank.setText(String.valueOf(position + 1));
        h.tvName.setText(s.getName());
        h.tvAddress.setText(s.getAddress());
        h.tvZone.setText(s.getZone());
        h.tvPriceCorriente.setText("$" + COP.format(s.getPriceCorriente()));
        h.tvPriceExtra.setText("$" + COP.format(s.getPriceExtra()));
        h.tvPriceAcpm.setText("$" + COP.format(s.getPriceAcpm()));

        // Resaltar la más barata
        if (position == 0) {
            h.tvRank.setBackgroundResource(R.drawable.badge_best_price);
            h.tvRank.setTextColor(0xFFFFFFFF);
        } else {
            h.tvRank.setBackgroundResource(R.drawable.badge_rank);
            h.tvRank.setTextColor(0xFFB0BEC5);
        }
    }

    @Override
    public int getItemCount() { return stations.size(); }

    public void updateData(List<Station> newList) {
        stations.clear();
        stations.addAll(newList);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvAddress, tvZone;
        TextView tvPriceCorriente, tvPriceExtra, tvPriceAcpm;

        ViewHolder(View v) {
            super(v);
            tvRank           = v.findViewById(R.id.tvRank);
            tvName           = v.findViewById(R.id.tvStationName);
            tvAddress        = v.findViewById(R.id.tvAddress);
            tvZone           = v.findViewById(R.id.tvZone);
            tvPriceCorriente = v.findViewById(R.id.tvPriceCorriente);
            tvPriceExtra     = v.findViewById(R.id.tvPriceExtra);
            tvPriceAcpm      = v.findViewById(R.id.tvPriceAcpm);
        }
    }
}