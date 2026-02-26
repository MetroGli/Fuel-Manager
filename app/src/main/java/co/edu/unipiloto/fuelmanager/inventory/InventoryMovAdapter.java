package co.edu.unipiloto.fuelmanager.inventory;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.model.InventoryMovement;

public class InventoryMovAdapter extends RecyclerView.Adapter<InventoryMovAdapter.ViewHolder> {

    private final List<InventoryMovement> items;

    public InventoryMovAdapter(List<InventoryMovement> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_inventory_mov, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        InventoryMovement m = items.get(position);

        boolean isEntrada = m.getMovType().equals(InventoryMovement.TYPE_ENTRADA);

        // Ícono y color según tipo
        h.tvMovType.setText(isEntrada ? "▲ ENTRADA" : "▼ SALIDA");
        h.tvMovType.setTextColor(isEntrada ? 0xFF66BB6A : 0xFFEF5350);
        h.tvMovType.setBackgroundResource(isEntrada
                ? R.drawable.badge_entrada : R.drawable.badge_salida);

        h.tvFuelType.setText(m.getFuelType());
        h.tvVolume.setText(String.format(Locale.getDefault(), "%.1f gal", m.getVolumeGal()));

        // Fecha abreviada (primeras 16 letras)
        String date = m.getDate();
        h.tvDate.setText(date.length() > 24 ? date.substring(0, 24) : date);

        // Nota
        if (m.getNote() != null && !m.getNote().isEmpty()) {
            h.tvNote.setVisibility(View.VISIBLE);
            h.tvNote.setText(m.getNote());
        } else {
            h.tvNote.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void updateData(List<InventoryMovement> newList) {
        items.clear();
        items.addAll(newList);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMovType, tvFuelType, tvVolume, tvDate, tvNote;
        ViewHolder(View v) {
            super(v);
            tvMovType  = v.findViewById(R.id.tvMovType);
            tvFuelType = v.findViewById(R.id.tvFuelType);
            tvVolume   = v.findViewById(R.id.tvVolume);
            tvDate     = v.findViewById(R.id.tvDate);
            tvNote     = v.findViewById(R.id.tvNote);
        }
    }
}