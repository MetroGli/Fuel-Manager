package co.edu.unipiloto.fuelmanager.normative;

import android.content.Context;

import java.util.List;

import co.edu.unipiloto.fuelmanager.data.model.NormativePrice;
import co.edu.unipiloto.fuelmanager.utils.ApiClient;

public class NormativePriceRepository {

    public NormativePriceRepository(Context context) {
        // Context ya no es necesario
    }

    public long insert(NormativePrice price) {
        return ApiClient.insertNormativePrice(price);
    }

    public List<NormativePrice> getAll() {
        return ApiClient.getNormativePrices();
    }

    public void clear() {
        ApiClient.clearNormativePrices();
    }

    public boolean fetchAndSaveFromJson() {
        try {
            clear();

            insert(new NormativePrice(0, "CORRIENTE", 9450.0,  new java.util.Date().toString(), "MINMINAS"));
            insert(new NormativePrice(0, "EXTRA",     10200.0, new java.util.Date().toString(), "MINMINAS"));
            insert(new NormativePrice(0, "ACPM",      8900.0,  new java.util.Date().toString(), "MINMINAS"));

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}