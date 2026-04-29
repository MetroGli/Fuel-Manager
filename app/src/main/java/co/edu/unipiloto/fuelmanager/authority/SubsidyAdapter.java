package co.edu.unipiloto.fuelmanager.authority;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import java.util.List;
import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.model.Subsidy;

public class SubsidyAdapter extends RecyclerView.Adapter<SubsidyAdapter.VH> {
    public interface OnDeactivate { void onDeactivate(int subsidyId); }
    private final List<Subsidy> items;
    private final OnDeactivate listener;
    public SubsidyAdapter(List<Subsidy> items, OnDeactivate listener) { this.items=items; this.listener=listener; }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subsidy, parent, false);
        return new VH(v);
    }
    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Subsidy s = items.get(pos);
        h.tvTarget.setText(s.getTargetType() + ": " + s.getTargetValue());
        h.tvFuel.setText(s.getFuelType()); h.tvDiscount.setText(s.getDiscountPct() + "% descuento");
        h.tvDates.setText("Hasta: " + s.getEndDate());
        h.tvStatus.setText(s.isActive() ? "✅ Activo" : "❌ Inactivo");
        h.tvStatus.setTextColor(s.isActive() ? 0xFF66BB6A : 0xFFEF5350);
        if (s.isActive()) {
            h.btnDeactivate.setVisibility(View.VISIBLE);
            h.btnDeactivate.setOnClickListener(v -> listener.onDeactivate(s.getId()));
        } else { h.btnDeactivate.setVisibility(View.GONE); }
    }
    @Override public int getItemCount() { return items.size(); }
    public void updateData(List<Subsidy> list) { items.clear(); items.addAll(list); notifyDataSetChanged(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTarget, tvFuel, tvDiscount, tvDates, tvStatus;
        MaterialButton btnDeactivate;
        VH(View v) {
            super(v);
            tvTarget=v.findViewById(R.id.tvSubsidyTarget); tvFuel=v.findViewById(R.id.tvSubsidyFuel);
            tvDiscount=v.findViewById(R.id.tvSubsidyDiscount); tvDates=v.findViewById(R.id.tvSubsidyDates);
            tvStatus=v.findViewById(R.id.tvSubsidyStatus); btnDeactivate=v.findViewById(R.id.btnDeactivateSubsidy);
        }
    }
}
