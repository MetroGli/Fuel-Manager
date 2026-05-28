package co.edu.unipiloto.fuelmanager.authority;

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

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.VH> {

    public interface OnPdfClick { void onPdf(Receipt receipt); }

    private final List<Receipt>  items;
    private final OnPdfClick     pdfListener;
    private static final NumberFormat COP = NumberFormat.getInstance(new Locale("es", "CO"));

    public ReceiptAdapter(List<Receipt> items, OnPdfClick pdfListener) {
        this.items       = items;
        this.pdfListener = pdfListener;
    }

    public ReceiptAdapter(List<Receipt> items) {
        this(items, null);
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receipt, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Receipt r = items.get(pos);

        h.tvReceiptId.setText("Recibo #" + r.getId() + "  ·  Venta #" + r.getSaleId());
        h.tvFuel.setText(r.getFuelType());
        h.tvVolume.setText(String.format(Locale.getDefault(),
                "%.1f gal x $%s = ", r.getVolumeGal(), COP.format(r.getPricePerGal())));
        h.tvTotal.setText("$" + COP.format(r.getTotal()));

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

        if (h.btnPdf != null) {
            if (pdfListener != null) {
                h.btnPdf.setVisibility(View.VISIBLE);
                h.btnPdf.setOnClickListener(v -> pdfListener.onPdf(r));
            } else {
                h.btnPdf.setVisibility(View.GONE);
            }
        }
    }

    @Override public int getItemCount() { return items.size(); }

    public void updateData(List<Receipt> list) {
        items.clear(); items.addAll(list); notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView       tvReceiptId, tvFuel, tvVolume, tvTotal, tvPlate, tvDate;
        MaterialButton btnPdf;

        VH(View v) {
            super(v);
            tvReceiptId = v.findViewById(R.id.tvReceiptId);
            tvFuel      = v.findViewById(R.id.tvReceiptFuel);
            tvVolume    = v.findViewById(R.id.tvReceiptVolume);
            tvTotal     = v.findViewById(R.id.tvReceiptTotal);
            tvPlate     = v.findViewById(R.id.tvReceiptPlate);
            tvDate      = v.findViewById(R.id.tvReceiptDate);
            btnPdf      = v.findViewById(R.id.btnGeneratePdf);
        }
    }
}