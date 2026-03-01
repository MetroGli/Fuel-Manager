package co.edu.unipiloto.fuelmanager.data.repository;

import android.content.Context;

import java.util.List;

import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.NormativePrice;

public class NormativePriceRepository {

    private DatabaseHelper dbHelper;

    public NormativePriceRepository(Context context) {
        dbHelper = DatabaseHelper.getInstance(context);
    }

    public long insert(NormativePrice price) {
        return dbHelper.insertNormativePrice(price);
    }

    public List<NormativePrice> getAll() {
        return dbHelper.getNormativePrices();
    }

    public void clear() {
        dbHelper.clearNormativePrices();
    }

    public boolean fetchAndSaveFromJson() {
        try {
            clear();

            NormativePrice p1 = new NormativePrice(0,"CORRIENTE",9450.0,new java.util.Date().toString(),"MINMINAS");
            NormativePrice p2 = new NormativePrice(0,"EXTRA",10200.0,new java.util.Date().toString(),"MINMINAS");
            NormativePrice p3 = new NormativePrice(0,"ACPM",8900.0,new java.util.Date().toString(),"MINMINAS");

            insert(p1);
            insert(p2);
            insert(p3);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}