package co.edu.unipiloto.fuelmanager.sales;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.model.Receipt;

public class ReceiptListAdapter extends RecyclerView.Adapter<ReceiptListAdapter.VH> {

    public interface OnGeneratePdf { void onGenerate(Receipt receipt); }

    private final List<Receipt>  items;
    private final OnGeneratePdf  listener;
    private static final NumberFormat COP = NumberFormat.getInstance(new Locale("es", "CO"));

    public ReceiptListAdapter(List<Receipt> items, OnGeneratePdf listener) {
        this.items    = items;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receipt_pdf, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Receipt r = items.get(pos);

        h.tvReceiptId.setText("Recibo #" + r.getId() + "  ·  Venta #" + r.getSaleId());
        h.tvFuel.setText(r.getFuelType());
        h.tvDetails.setText(String.format(Locale.getDefault(),
                "%.1f gal  ×  $%s  =  $%s",
                r.getVolumeGal(), COP.format(r.getPricePerGal()), COP.format(r.getTotal())));

        if (r.getClientPlate() != null && !r.getClientPlate().isEmpty()) {
            h.tvPlate.setVisibility(View.VISIBLE);
            h.tvPlate.setText("🚗 " + r.getClientPlate());
        } else {
            h.tvPlate.setVisibility(View.GONE);
        }

        String date = r.getDate();
        h.tvDate.setText(date != null && date.length() > 24 ? date.substring(0, 24) : date);

        int color;
        switch (r.getFuelType()) {
            case "Extra": color = 0xFFFF8F00; break;
            case "ACPM":  color = 0xFF42A5F5; break;
            default:      color = 0xFFE65100; break;
        }
        h.tvFuel.setTextColor(color);

        h.btnPdf.setOnClickListener(v -> listener.onGenerate(r));
    }

    @Override public int getItemCount() { return items.size(); }

    public void updateData(List<Receipt> list) {
        items.clear(); items.addAll(list); notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvReceiptId, tvFuel, tvDetails, tvPlate, tvDate;
        MaterialButton btnPdf;
        VH(View v) {
            super(v);
            tvReceiptId = v.findViewById(R.id.tvReceiptIdPdf);
            tvFuel      = v.findViewById(R.id.tvReceiptFuelPdf);
            tvDetails   = v.findViewById(R.id.tvReceiptDetailsPdf);
            tvPlate     = v.findViewById(R.id.tvReceiptPlatePdf);
            tvDate      = v.findViewById(R.id.tvReceiptDatePdf);
            btnPdf      = v.findViewById(R.id.btnGeneratePdf);
        }
    }
}
