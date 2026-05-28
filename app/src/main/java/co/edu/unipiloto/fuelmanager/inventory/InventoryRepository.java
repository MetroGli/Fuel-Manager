package co.edu.unipiloto.fuelmanager.inventory;

import android.content.Context;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import co.edu.unipiloto.fuelmanager.data.model.InventoryMovement;
import co.edu.unipiloto.fuelmanager.data.model.InventoryStock;
import co.edu.unipiloto.fuelmanager.utils.ApiClient;

public class InventoryRepository {

    public InventoryRepository(Context context) {
    }

    public long insertMovement(InventoryMovement mov) {
        try {
            JSONObject body = new JSONObject();
            body.put("fuelType",   mov.getFuelType());
            body.put("movType",    mov.getMovType());
            body.put("volumeGal",  mov.getVolumeGal());
            body.put("note",       mov.getNote() != null ? mov.getNote() : "");
            body.put("date",       mov.getDate());
            body.put("stationId",  mov.getStationId());
            String resp = ApiClient.postRaw("/inventory", body.toString());
            return new JSONObject(resp).optInt("id", -1);
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    public List<InventoryMovement> getMovements(int stationId) {
        return ApiClient.getInventoryMovements(stationId);
    }

    public InventoryStock getCurrentStock(int stationId) {
        return ApiClient.getInventoryStock(stationId);
    }
}