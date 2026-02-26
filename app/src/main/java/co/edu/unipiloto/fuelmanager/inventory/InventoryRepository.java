package co.edu.unipiloto.fuelmanager.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.InventoryMovement;
import co.edu.unipiloto.fuelmanager.data.model.InventoryStock;

public class InventoryRepository {

    private final DatabaseHelper db;

    public InventoryRepository(Context context) {
        db = DatabaseHelper.getInstance(context);
    }


    public long insertMovement(InventoryMovement mov) {
        SQLiteDatabase database = db.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(DatabaseHelper.COL_INV_FUEL_TYPE,  mov.getFuelType());
        v.put(DatabaseHelper.COL_INV_MOV_TYPE,   mov.getMovType());
        v.put(DatabaseHelper.COL_INV_VOLUME,     mov.getVolumeGal());
        v.put(DatabaseHelper.COL_INV_NOTE,       mov.getNote());
        v.put(DatabaseHelper.COL_INV_DATE,       mov.getDate());
        v.put(DatabaseHelper.COL_INV_STATION_ID, mov.getStationId());
        long id = database.insert(DatabaseHelper.TABLE_INVENTORY, null, v);
        database.close();
        return id;
    }


    public List<InventoryMovement> getMovements(int stationId) {
        SQLiteDatabase database = db.getReadableDatabase();
        List<InventoryMovement> list = new ArrayList<>();
        Cursor c = database.query(
                DatabaseHelper.TABLE_INVENTORY, null,
                DatabaseHelper.COL_INV_STATION_ID + "=?",
                new String[]{String.valueOf(stationId)},
                null, null,
                DatabaseHelper.COL_ID + " DESC"
        );
        if (c != null) {
            while (c.moveToNext()) list.add(cursorToMov(c));
            c.close();
        }
        database.close();
        return list;
    }

    public InventoryStock getCurrentStock(int stationId) {
        SQLiteDatabase database = db.getReadableDatabase();
        double corriente = calcStock(database, stationId, InventoryMovement.FUEL_CORRIENTE);
        double extra     = calcStock(database, stationId, InventoryMovement.FUEL_EXTRA);
        double acpm      = calcStock(database, stationId, InventoryMovement.FUEL_ACPM);
        database.close();
        return new InventoryStock(corriente, extra, acpm);
    }

    private double calcStock(SQLiteDatabase database, int stationId, String fuelType) {
        // Suma entradas
        double entradas = sumByType(database, stationId, fuelType, InventoryMovement.TYPE_ENTRADA);
        // Suma salidas
        double salidas  = sumByType(database, stationId, fuelType, InventoryMovement.TYPE_SALIDA);
        return Math.max(0, entradas - salidas);
    }

    private double sumByType(SQLiteDatabase db, int stationId, String fuel, String movType) {
        Cursor c = db.rawQuery(
                "SELECT SUM(" + DatabaseHelper.COL_INV_VOLUME + ") FROM "
                        + DatabaseHelper.TABLE_INVENTORY
                        + " WHERE " + DatabaseHelper.COL_INV_STATION_ID + "=?"
                        + " AND "   + DatabaseHelper.COL_INV_FUEL_TYPE  + "=?"
                        + " AND "   + DatabaseHelper.COL_INV_MOV_TYPE   + "=?",
                new String[]{String.valueOf(stationId), fuel, movType}
        );
        double sum = 0;
        if (c != null && c.moveToFirst() && !c.isNull(0)) sum = c.getDouble(0);
        if (c != null) c.close();
        return sum;
    }

    private InventoryMovement cursorToMov(Cursor c) {
        InventoryMovement m = new InventoryMovement();
        m.setId(c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
        m.setFuelType(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_INV_FUEL_TYPE)));
        m.setMovType(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_INV_MOV_TYPE)));
        m.setVolumeGal(c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_INV_VOLUME)));
        m.setNote(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_INV_NOTE)));
        m.setDate(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_INV_DATE)));
        m.setStationId(c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_INV_STATION_ID)));
        return m;
    }
}