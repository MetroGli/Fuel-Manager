package co.edu.unipiloto.fuelmanager.sales;

import android.content.ContentValues;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.Receipt;
import co.edu.unipiloto.fuelmanager.data.model.Station;
import co.edu.unipiloto.fuelmanager.utils.SessionManager;

public class ReceiptPdfActivity extends AppCompatActivity {

    /**
     * Extra opcional: si se pasa este int, la Activity muestra y descarga
     * directamente ese recibo específico (usado desde Autoridad y desde
     * el botón "Ver PDF" en el historial de la estación).
     */
    public static final String EXTRA_RECEIPT_ID = "receipt_id";

    private RecyclerView      recyclerReceipts;
    private ReceiptListAdapter adapter;
    private TextView           tvStationName;

    private DatabaseHelper db;
    private int            stationId;
    private String         stationName = "Estación";
    private static final NumberFormat COP = NumberFormat.getInstance(new Locale("es", "CO"));

    // Colores PDF
    private static final int COLOR_ORANGE  = Color.parseColor("#E65100");
    private static final int COLOR_BG      = Color.parseColor("#0D1117");
    private static final int COLOR_SURFACE = Color.parseColor("#161B22");
    private static final int COLOR_WHITE   = Color.WHITE;
    private static final int COLOR_GRAY    = Color.parseColor("#B0BEC5");
    private static final int COLOR_GREEN   = Color.parseColor("#66BB6A");
    private static final int PAGE_W = 595, PAGE_H = 842, MARGIN = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_pdf);

        db = DatabaseHelper.getInstance(this);
        SessionManager session = new SessionManager(this);
        int userId = session.getUserId();
        stationId = db.getStationIdByUserId(userId);
        if (stationId < 0) stationId = userId;

        Station st = db.getStationById(stationId);
        if (st != null) stationName = st.getName();

        // ── MODO 1: recibo individual pasado por intent ──────────────────────
        // Usado cuando Autoridad o Estación pulsan "Ver PDF" en un recibo concreto
        int specificReceiptId = getIntent().getIntExtra(EXTRA_RECEIPT_ID, -1);
        if (specificReceiptId != -1) {
            // Buscar el recibo por id directo
            Receipt r = db.getReceiptBySaleId(specificReceiptId); // reusamos saleId lookup
            if (r == null) {
                // Fallback: iterar todos hasta encontrar el id correcto
                List<Receipt> all = db.getAllReceipts();
                for (Receipt rec : all) {
                    if (rec.getId() == specificReceiptId) { r = rec; break; }
                }
            }
            if (r != null) {
                generateReceiptPdf(r);
            } else {
                Toast.makeText(this, "Recibo #" + specificReceiptId + " no encontrado",
                        Toast.LENGTH_LONG).show();
            }
            finish(); // cerrar tras generar — no mostrar lista
            return;
        }

        // ── MODO 2: lista de recibos de la estación (comportamiento original) ─
        tvStationName = findViewById(R.id.tvReceiptStation);
        if (tvStationName != null) tvStationName.setText(stationName);

        recyclerReceipts = findViewById(R.id.recyclerStationReceipts);
        adapter = new ReceiptListAdapter(new ArrayList<>(), this::generateReceiptPdf);
        recyclerReceipts.setLayoutManager(new LinearLayoutManager(this));
        recyclerReceipts.setAdapter(adapter);

        MaterialButton btnAllPdf = findViewById(R.id.btnAllReceiptsPdf);
        if (btnAllPdf != null) btnAllPdf.setOnClickListener(v -> generateAllReceiptsPdf());

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        loadReceipts();
    }

    private void loadReceipts() {
        new Thread(() -> {
            List<Receipt> list = db.getReceiptsByStation(stationId);
            runOnUiThread(() -> adapter.updateData(list));
        }).start();
    }

    // ── PDF de un recibo individual ─────────────────────────────────────────

    private void generateReceiptPdf(Receipt receipt) {
        new Thread(() -> {
            try {
                PdfDocument doc = new PdfDocument();
                PdfDocument.PageInfo info =
                        new PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 1).create();
                PdfDocument.Page page = doc.startPage(info);
                drawReceiptPage(page.getCanvas(), receipt);
                doc.finishPage(page);
                String fileName = "Recibo_" + receipt.getId() +
                        "_Venta_" + receipt.getSaleId() + ".pdf";
                savePdf(doc, fileName);
                doc.close();
                runOnUiThread(() ->
                        Toast.makeText(this, "PDF guardado: " + fileName,
                                Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    // ── PDF con todos los recibos de la estación ────────────────────────────

    private void generateAllReceiptsPdf() {
        new Thread(() -> {
            try {
                List<Receipt> list = db.getReceiptsByStation(stationId);
                if (list.isEmpty()) {
                    runOnUiThread(() -> Toast.makeText(this,
                            "Sin recibos registrados", Toast.LENGTH_SHORT).show());
                    return;
                }
                PdfDocument doc = new PdfDocument();
                for (int i = 0; i < list.size(); i++) {
                    PdfDocument.PageInfo info =
                            new PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, i + 1).create();
                    PdfDocument.Page page = doc.startPage(info);
                    drawReceiptPage(page.getCanvas(), list.get(i));
                    doc.finishPage(page);
                }
                String fileName = "Recibos_" + stationName.replace(" ", "_") +
                        "_" + System.currentTimeMillis() + ".pdf";
                savePdf(doc, fileName);
                doc.close();
                runOnUiThread(() -> Toast.makeText(this,
                        "PDF con " + list.size() + " recibos guardado",
                        Toast.LENGTH_LONG).show());
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    // ── Dibujar página PDF ──────────────────────────────────────────────────

    private void drawReceiptPage(Canvas c, Receipt r) {
        Paint bg = new Paint(); bg.setColor(COLOR_BG);
        c.drawRect(0, 0, PAGE_W, PAGE_H, bg);

        Paint headerBg = new Paint(); headerBg.setColor(COLOR_ORANGE);
        c.drawRect(0, 0, PAGE_W, 90, headerBg);

        Paint title = new Paint();
        title.setColor(COLOR_WHITE); title.setTextSize(24f);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        c.drawText("FUEL MANAGER", MARGIN, 38, title);

        Paint sub = new Paint(); sub.setColor(COLOR_WHITE); sub.setTextSize(13f);
        c.drawText("Recibo de Venta  ·  " + stationName, MARGIN, 62, sub);

        Paint receiptNum = new Paint(); receiptNum.setColor(COLOR_WHITE); receiptNum.setTextSize(11f);
        c.drawText("Recibo #" + r.getId() + "  ·  Venta #" + r.getSaleId(), MARGIN, 82, receiptNum);

        int y = 130;
        drawField(c, "Combustible", r.getFuelType(), y); y += 40;
        drawField(c, "Volumen",
                String.format(Locale.getDefault(), "%.2f galones", r.getVolumeGal()), y); y += 40;
        drawField(c, "Precio por galón",
                "$" + COP.format(r.getPricePerGal()) + " COP", y); y += 40;

        if (r.getClientPlate() != null && !r.getClientPlate().isEmpty()) {
            drawField(c, "Placa vehículo", r.getClientPlate(), y); y += 40;
        }

        String date = r.getDate();
        drawField(c, "Fecha", date.length() > 24 ? date.substring(0, 24) : date, y); y += 60;

        Paint totalBg = new Paint(); totalBg.setColor(COLOR_SURFACE);
        c.drawRect(MARGIN, y - 10, PAGE_W - MARGIN, y + 60, totalBg);

        Paint totalLabel = new Paint(); totalLabel.setColor(COLOR_GRAY); totalLabel.setTextSize(13f);
        c.drawText("TOTAL A PAGAR", MARGIN + 10, y + 18, totalLabel);

        Paint totalVal = new Paint(); totalVal.setColor(COLOR_GREEN); totalVal.setTextSize(28f);
        totalVal.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        c.drawText("$" + COP.format(r.getTotal()) + " COP", MARGIN + 10, y + 50, totalVal);

        y += 90;
        Paint linePaint = new Paint(); linePaint.setColor(COLOR_ORANGE); linePaint.setStrokeWidth(2f);
        c.drawLine(MARGIN, y, PAGE_W - MARGIN, y, linePaint); y += 20;

        Paint foot = new Paint(); foot.setColor(COLOR_GRAY); foot.setTextSize(10f);
        c.drawText("Gracias por su preferencia  ·  FuelManager", MARGIN, y, foot);
        c.drawText("Documento generado automáticamente", MARGIN, y + 16, foot);
    }

    private void drawField(Canvas c, String label, String value, int y) {
        Paint lbl = new Paint(); lbl.setColor(COLOR_GRAY); lbl.setTextSize(11f);
        c.drawText(label.toUpperCase(), MARGIN, y, lbl);

        Paint val = new Paint(); val.setColor(COLOR_WHITE); val.setTextSize(16f);
        val.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        c.drawText(value, MARGIN, y + 20, val);

        Paint line = new Paint(); line.setColor(0xFF2C3E50); line.setStrokeWidth(1f);
        c.drawLine(MARGIN, y + 28, PAGE_W - MARGIN, y + 28, line);
    }

    private void savePdf(PdfDocument doc, String fileName) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            Uri uri = getContentResolver().insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri == null) throw new IOException("No se pudo crear el archivo");
            try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                doc.writeTo(out);
            }
        } else {
            java.io.File dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
            if (!dir.exists()) dir.mkdirs();
            try (OutputStream out = new java.io.FileOutputStream(
                    new java.io.File(dir, fileName))) {
                doc.writeTo(out);
            }
        }
    }
}