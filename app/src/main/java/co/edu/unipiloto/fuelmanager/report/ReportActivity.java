package co.edu.unipiloto.fuelmanager.report;

import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.Delivery;
import co.edu.unipiloto.fuelmanager.data.model.FuelSale;
import co.edu.unipiloto.fuelmanager.data.model.NormativePrice;
import co.edu.unipiloto.fuelmanager.data.model.Station;

public class ReportActivity extends AppCompatActivity {

    private Button btnGenerate;
    private ImageButton btnBack;
    private TextView tvStatus;

    private DatabaseHelper db;
    private static final NumberFormat COP = NumberFormat.getInstance(new Locale("es", "CO"));

    // Colores del tema
    private static final int COLOR_ORANGE  = Color.parseColor("#E65100");
    private static final int COLOR_BG      = Color.parseColor("#0D1117");
    private static final int COLOR_SURFACE = Color.parseColor("#161B22");
    private static final int COLOR_WHITE   = Color.WHITE;
    private static final int COLOR_GRAY    = Color.parseColor("#B0BEC5");
    private static final int COLOR_GREEN   = Color.parseColor("#66BB6A");

    // Dimensiones página A4
    private static final int PAGE_W = 595;
    private static final int PAGE_H = 842;
    private static final int MARGIN = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        db = DatabaseHelper.getInstance(this);

        btnGenerate = findViewById(R.id.btnGenerate);
        btnBack     = findViewById(R.id.btnBack);
        tvStatus    = findViewById(R.id.tvStatus);

        btnBack.setOnClickListener(v -> finish());
        btnGenerate.setOnClickListener(v -> generatePdf());

        updateStats();
    }

    private void updateStats() {
        List<Station>        stations   = db.getAllStations();
        List<FuelSale>       sales      = db.getAllSales();
        List<Delivery>       deliveries = db.getAllDeliveries();
        List<NormativePrice> normative  = db.getNormativePrices();

        TextView tvStations   = findViewById(R.id.tvStationCount);
        TextView tvSales      = findViewById(R.id.tvSalesCount);
        TextView tvDeliveries = findViewById(R.id.tvDeliveriesCount);
        TextView tvNormative  = findViewById(R.id.tvNormativeCount);

        if (tvStations   != null) tvStations.setText(String.valueOf(stations.size()));
        if (tvSales      != null) tvSales.setText(String.valueOf(sales.size()));
        if (tvDeliveries != null) tvDeliveries.setText(String.valueOf(deliveries.size()));
        if (tvNormative  != null) tvNormative.setText(String.valueOf(normative.size()));
    }

    // ── Generación del PDF ───────────────────────────────────────────
    private void generatePdf() {
        btnGenerate.setEnabled(false);
        tvStatus.setText("Generando reporte...");

        new Thread(() -> {
            try {
                PdfDocument doc = buildPdf();
                savePdf(doc);
                doc.close();
                runOnUiThread(() -> {
                    tvStatus.setText("✓ Reporte guardado en Descargas");
                    btnGenerate.setEnabled(true);
                    Toast.makeText(this, "PDF guardado correctamente", Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    tvStatus.setText("Error al generar el reporte");
                    btnGenerate.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private PdfDocument buildPdf() {
        PdfDocument doc = new PdfDocument();

        List<Station>        stations   = db.getAllStations();
        List<FuelSale>       sales      = db.getAllSales();
        List<Delivery>       deliveries = db.getAllDeliveries();
        List<NormativePrice> normative  = db.getNormativePrices();

        // ── Página 1: Portada + Estaciones ───────────────────────────
        PdfDocument.PageInfo info1 = new PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 1).create();
        PdfDocument.Page page1 = doc.startPage(info1);
        Canvas c1 = page1.getCanvas();
        int y = drawCover(c1);
        y = drawSectionHeader(c1, "ESTACIONES DE SERVICIO", y + 20);
        drawStationsTable(c1, stations, y + 10);
        doc.finishPage(page1);

        // ── Página 2: Ventas + Normativos ────────────────────────────
        PdfDocument.PageInfo info2 = new PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 2).create();
        PdfDocument.Page page2 = doc.startPage(info2);
        Canvas c2 = page2.getCanvas();
        int y2 = MARGIN + 20;
        y2 = drawSectionHeader(c2, "HISTORIAL DE VENTAS", y2);
        y2 = drawSalesTable(c2, sales, y2 + 10);
        if (y2 + 120 < PAGE_H - MARGIN) {
            y2 = drawSectionHeader(c2, "PRECIOS NORMATIVOS MINMINAS", y2 + 20);
            drawNormativeTable(c2, normative, y2 + 10);
        }
        doc.finishPage(page2);

        // ── Página 3: Entregas ───────────────────────────────────────
        PdfDocument.PageInfo info3 = new PdfDocument.PageInfo.Builder(PAGE_W, PAGE_H, 3).create();
        PdfDocument.Page page3 = doc.startPage(info3);
        Canvas c3 = page3.getCanvas();
        int y3 = MARGIN + 20;
        y3 = drawSectionHeader(c3, "ENTREGAS DE DISTRIBUIDORES", y3);
        drawDeliveriesTable(c3, deliveries, y3 + 10);
        doc.finishPage(page3);

        return doc;
    }

    private int drawCover(Canvas c) {
        Paint bg = new Paint();
        bg.setColor(COLOR_BG);
        c.drawRect(0, 0, PAGE_W, 120, bg);

        Paint line = new Paint();
        line.setColor(COLOR_ORANGE);
        line.setStrokeWidth(3f);
        c.drawLine(MARGIN, 120, PAGE_W - MARGIN, 120, line);

        Paint title = new Paint();
        title.setColor(COLOR_ORANGE);
        title.setTextSize(22f);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        c.drawText("FUEL MANAGER", MARGIN, 55, title);

        Paint subtitle = new Paint();
        subtitle.setColor(COLOR_WHITE);
        subtitle.setTextSize(13f);
        c.drawText("Reporte General del Sistema", MARGIN, 80, subtitle);

        Paint dateP = new Paint();
        dateP.setColor(COLOR_GRAY);
        dateP.setTextSize(10f);
        String dateStr = new Date().toString();
        c.drawText("Generado: " + (dateStr.length() > 24 ? dateStr.substring(0, 24) : dateStr),
                MARGIN, 105, dateP);

        Paint badgeBg = new Paint();
        badgeBg.setColor(COLOR_ORANGE);
        badgeBg.setAlpha(30);
        c.drawRoundRect(PAGE_W - 200, 30, PAGE_W - MARGIN, 100, 8, 8, badgeBg);
        Paint badgeText = new Paint();
        badgeText.setColor(COLOR_ORANGE);
        badgeText.setTextSize(9f);
        badgeText.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        c.drawText("AUTORIDAD REGULADORA", PAGE_W - 190, 62, badgeText);
        c.drawText("CONFIDENCIAL", PAGE_W - 175, 82, badgeText);

        return 130;
    }

    private int drawSectionHeader(Canvas c, String title, int y) {
        Paint bg = new Paint();
        bg.setColor(COLOR_SURFACE);
        c.drawRect(MARGIN, y, PAGE_W - MARGIN, y + 22, bg);

        Paint line = new Paint();
        line.setColor(COLOR_ORANGE);
        line.setStrokeWidth(2f);
        c.drawLine(MARGIN, y + 22, MARGIN + 50, y + 22, line);

        Paint text = new Paint();
        text.setColor(COLOR_ORANGE);
        text.setTextSize(10f);
        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        c.drawText(title, MARGIN + 6, y + 15, text);

        return y + 22;
    }

    private void drawTableHeader(Canvas c, String[] cols, int[] widths, int y) {
        Paint bg = new Paint();
        bg.setColor(COLOR_ORANGE);
        bg.setAlpha(40);
        int totalW = 0;
        for (int w : widths) totalW += w;
        c.drawRect(MARGIN, y, MARGIN + totalW, y + 16, bg);

        Paint text = new Paint();
        text.setColor(COLOR_ORANGE);
        text.setTextSize(8f);
        text.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        int x = MARGIN + 4;
        for (int i = 0; i < cols.length; i++) {
            c.drawText(cols[i], x, y + 11, text);
            x += widths[i];
        }
    }

    private int drawStationsTable(Canvas c, List<Station> stations, int startY) {
        String[] headers = {"#", "NOMBRE", "ZONA", "CORRIENTE", "EXTRA", "ACPM"};
        int[]    widths  = {20, 175, 70, 90, 90, 90};
        drawTableHeader(c, headers, widths, startY);

        int y = startY + 18;
        Paint row = new Paint();
        Paint text = new Paint();
        text.setTextSize(8f);

        int i = 0;
        for (Station s : stations) {
            if (y + 14 > PAGE_H - MARGIN) break;
            row.setColor(i % 2 == 0 ? Color.parseColor("#0D1117") : Color.parseColor("#161B22"));
            c.drawRect(MARGIN, y - 2, PAGE_W - MARGIN, y + 12, row);

            text.setColor(COLOR_ORANGE);
            c.drawText(String.valueOf(i + 1), MARGIN + 4, y + 9, text);

            text.setColor(COLOR_WHITE);
            String name = s.getName().length() > 28 ? s.getName().substring(0, 28) + "…" : s.getName();
            c.drawText(name, MARGIN + 24, y + 9, text);

            text.setColor(COLOR_GRAY);
            String zone = s.getZone() != null ? s.getZone() : "-";
            if (zone.length() > 12) zone = zone.substring(0, 12) + "…";
            c.drawText(zone, MARGIN + 199, y + 9, text);

            text.setColor(COLOR_WHITE);
            c.drawText("$" + COP.format(s.getPriceCorriente()), MARGIN + 269, y + 9, text);
            c.drawText("$" + COP.format(s.getPriceExtra()),     MARGIN + 359, y + 9, text);
            c.drawText("$" + COP.format(s.getPriceAcpm()),      MARGIN + 449, y + 9, text);

            y += 14;
            i++;
        }
        return y;
    }

    private int drawSalesTable(Canvas c, List<FuelSale> sales, int startY) {
        if (sales.isEmpty()) {
            Paint noData = new Paint();
            noData.setColor(COLOR_GRAY);
            noData.setTextSize(9f);
            c.drawText("Sin registros de ventas", MARGIN + 10, startY + 12, noData);
            return startY + 20;
        }

        String[] headers = {"ID", "COMBUSTIBLE", "VOLUMEN", "PRECIO/GAL", "TOTAL", "PLACA"};
        int[]    widths  = {25, 110, 80, 90, 100, 90};
        drawTableHeader(c, headers, widths, startY);

        int y = startY + 18;
        Paint row = new Paint();
        Paint text = new Paint();
        text.setTextSize(7.5f);

        double totalRevenue = 0;
        int count = 0;

        for (FuelSale s : sales) {
            if (y + 14 > PAGE_H - MARGIN) break;

            row.setColor(count % 2 == 0 ? Color.parseColor("#0D1117") : Color.parseColor("#161B22"));
            c.drawRect(MARGIN, y - 2, PAGE_W - MARGIN, y + 12, row);

            text.setColor(COLOR_ORANGE);
            c.drawText(String.valueOf(s.getId()), MARGIN + 4, y + 9, text);

            text.setColor(COLOR_WHITE);
            c.drawText(s.getFuelType(), MARGIN + 29, y + 9, text);
            c.drawText(String.format("%.1f gal", s.getVolumeGal()), MARGIN + 139, y + 9, text);

            text.setColor(COLOR_GRAY);
            c.drawText("$" + COP.format(s.getPricePerGal()), MARGIN + 219, y + 9, text);

            text.setColor(COLOR_GREEN);
            // Usar getTotalPrice() que es el nombre correcto en tu modelo
            c.drawText("$" + COP.format(s.getTotalPrice()), MARGIN + 309, y + 9, text);

            text.setColor(COLOR_GRAY);
            String plate = s.getClientPlate() != null && !s.getClientPlate().isEmpty()
                    ? s.getClientPlate() : "-";
            c.drawText(plate, MARGIN + 409, y + 9, text);

            totalRevenue += s.getTotalPrice();
            y += 14;
            count++;
        }

        y += 4;
        Paint totalP = new Paint();
        totalP.setColor(COLOR_ORANGE);
        totalP.setTextSize(9f);
        totalP.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        c.drawText("TOTAL VENTAS: $" + COP.format(totalRevenue), MARGIN, y + 10, totalP);
        return y + 20;
    }

    private void drawNormativeTable(Canvas c, List<NormativePrice> prices, int startY) {
        if (prices.isEmpty()) {
            Paint noData = new Paint();
            noData.setColor(COLOR_GRAY);
            noData.setTextSize(9f);
            c.drawText("Sin precios normativos registrados", MARGIN + 10, startY + 12, noData);
            return;
        }

        String[] headers = {"COMBUSTIBLE", "PRECIO/GALÓN", "FECHA", "FUENTE"};
        int[]    widths  = {130, 130, 170, 85};
        drawTableHeader(c, headers, widths, startY);

        int y = startY + 18;
        Paint row = new Paint();
        Paint text = new Paint();
        text.setTextSize(8f);

        int i = 0;
        for (NormativePrice p : prices) {
            if (y + 14 > PAGE_H - MARGIN) break;
            row.setColor(i % 2 == 0 ? Color.parseColor("#0D1117") : Color.parseColor("#161B22"));
            c.drawRect(MARGIN, y - 2, PAGE_W - MARGIN, y + 12, row);

            text.setColor(COLOR_WHITE);
            c.drawText(p.getFuelType(), MARGIN + 4, y + 9, text);
            c.drawText("$" + COP.format(p.getPricePerGallon()), MARGIN + 134, y + 9, text);

            text.setColor(COLOR_GRAY);
            String date = p.getEffectiveDate();
            c.drawText(date.length() > 24 ? date.substring(0, 24) : date, MARGIN + 264, y + 9, text);
            c.drawText(p.getSource() != null ? p.getSource() : "-", MARGIN + 434, y + 9, text);

            y += 14;
            i++;
        }
    }

    private void drawDeliveriesTable(Canvas c, List<Delivery> deliveries, int startY) {
        if (deliveries.isEmpty()) {
            Paint noData = new Paint();
            noData.setColor(COLOR_GRAY);
            noData.setTextSize(9f);
            c.drawText("Sin entregas registradas", MARGIN + 10, startY + 12, noData);
            return;
        }

        String[] headers = {"ID", "ESTACIÓN", "COMBUSTIBLE", "VOLUMEN", "DIST.", "FECHA"};
        int[]    widths  = {25, 130, 90, 80, 60, 130};
        drawTableHeader(c, headers, widths, startY);

        int y = startY + 18;
        Paint row = new Paint();
        Paint text = new Paint();
        text.setTextSize(7.5f);

        double totalGal = 0;
        int i = 0;

        for (Delivery d : deliveries) {
            if (y + 14 > PAGE_H - MARGIN) break;

            row.setColor(i % 2 == 0 ? Color.parseColor("#0D1117") : Color.parseColor("#161B22"));
            c.drawRect(MARGIN, y - 2, PAGE_W - MARGIN, y + 12, row);

            text.setColor(COLOR_ORANGE);
            c.drawText(String.valueOf(d.getId()), MARGIN + 4, y + 9, text);

            text.setColor(COLOR_WHITE);
            // Usar getStationName() que existe en tu modelo Delivery
            String stName = d.getStationName() != null ? d.getStationName() : String.valueOf(d.getStationId());
            if (stName.length() > 18) stName = stName.substring(0, 18) + "…";
            c.drawText(stName, MARGIN + 29, y + 9, text);
            c.drawText(d.getFuelType(), MARGIN + 159, y + 9, text);

            text.setColor(COLOR_GREEN);
            c.drawText(String.format("%.1f gal", d.getVolumeGal()), MARGIN + 249, y + 9, text);

            text.setColor(COLOR_GRAY);
            c.drawText(String.valueOf(d.getDistributorId()), MARGIN + 329, y + 9, text);
            String date = d.getDate();
            c.drawText(date.length() > 18 ? date.substring(0, 18) : date, MARGIN + 389, y + 9, text);

            totalGal += d.getVolumeGal();
            y += 14;
            i++;
        }

        y += 4;
        Paint totalP = new Paint();
        totalP.setColor(COLOR_ORANGE);
        totalP.setTextSize(9f);
        totalP.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        c.drawText(String.format("TOTAL ENTREGADO: %.1f galones", totalGal), MARGIN, y + 10, totalP);
    }

    private void savePdf(PdfDocument doc) throws IOException {
        String fileName = "FuelManager_Reporte_" + System.currentTimeMillis() + ".pdf";

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
            java.io.File file = new java.io.File(dir, fileName);
            try (OutputStream out = new java.io.FileOutputStream(file)) {
                doc.writeTo(out);
            }
        }
    }
}