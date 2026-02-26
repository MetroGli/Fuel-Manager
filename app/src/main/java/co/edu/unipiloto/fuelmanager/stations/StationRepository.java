package co.edu.unipiloto.fuelmanager.stations;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.fuelmanager.data.local.DatabaseHelper;
import co.edu.unipiloto.fuelmanager.data.model.Station;

public class StationRepository {

    private final DatabaseHelper db;

    public StationRepository(Context context) {
        db = DatabaseHelper.getInstance(context);
    }

    public List<Station> getAllOrderedByPrice() {
        SQLiteDatabase database = db.getReadableDatabase();
        List<Station> list = new ArrayList<>();

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_STATIONS,
                null, null, null, null, null,
                DatabaseHelper.COL_PRICE_CORRIENTE + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToStation(cursor));
            }
            cursor.close();
        }
        database.close();
        return list;
    }

    public List<Station> getByZone(String zone) {
        SQLiteDatabase database = db.getReadableDatabase();
        List<Station> list = new ArrayList<>();

        Cursor cursor = database.query(
                DatabaseHelper.TABLE_STATIONS,
                null,
                DatabaseHelper.COL_ZONE + "=?",
                new String[]{zone},
                null, null,
                DatabaseHelper.COL_PRICE_CORRIENTE + " ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                list.add(cursorToStation(cursor));
            }
            cursor.close();
        }
        database.close();
        return list;
    }

    public boolean updatePrices(int stationId, double corriente, double extra, double acpm) {
        SQLiteDatabase database = db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_PRICE_CORRIENTE, corriente);
        values.put(DatabaseHelper.COL_PRICE_EXTRA, extra);
        values.put(DatabaseHelper.COL_PRICE_ACPM, acpm);
        int rows = database.update(
                DatabaseHelper.TABLE_STATIONS, values,
                DatabaseHelper.COL_ID + "=?",
                new String[]{String.valueOf(stationId)}
        );
        database.close();
        return rows > 0;
    }

    private Station cursorToStation(Cursor c) {
        Station s = new Station();
        s.setId(c.getInt(c.getColumnIndexOrThrow(DatabaseHelper.COL_ID)));
        s.setName(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_ST_NAME)));
        s.setAddress(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_ADDRESS)));
        s.setZone(c.getString(c.getColumnIndexOrThrow(DatabaseHelper.COL_ZONE)));
        s.setPriceCorriente(c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_PRICE_CORRIENTE)));
        s.setPriceExtra(c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_PRICE_EXTRA)));
        s.setPriceAcpm(c.getDouble(c.getColumnIndexOrThrow(DatabaseHelper.COL_PRICE_ACPM)));
        return s;
    }
}