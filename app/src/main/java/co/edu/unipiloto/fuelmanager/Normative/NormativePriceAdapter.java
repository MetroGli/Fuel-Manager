package co.edu.unipiloto.fuelmanager.Normative;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.model.NormativePrice;
public class NormativePriceAdapter
        extends RecyclerView.Adapter<NormativePriceAdapter.VH> {

    private List<NormativePrice> data = new ArrayList<>();

    public void setData(List<NormativePrice> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvFuel, tvPrice, tvDate;

        VH(View v) {
            super(v);
            tvFuel = v.findViewById(R.id.tvFuel);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvDate = v.findViewById(R.id.tvDate);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_normative_price, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH h, int pos) {
        NormativePrice p = data.get(pos);

        h.tvFuel.setText(p.getFuelType());
        h.tvPrice.setText("$ " + p.getPricePerGallon());
        h.tvDate.setText(p.getEffectiveDate());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}