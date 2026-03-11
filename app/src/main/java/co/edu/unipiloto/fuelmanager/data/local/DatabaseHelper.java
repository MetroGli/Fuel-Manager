package co.edu.unipiloto.fuelmanager.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.fuelmanager.data.model.FuelSale;
import co.edu.unipiloto.fuelmanager.data.model.NormativePrice;
import co.edu.unipiloto.fuelmanager.data.model.PriceAlert;
import co.edu.unipiloto.fuelmanager.data.model.User;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME    = "fuelmanager.db";
    private static final int    DATABASE_VERSION = 6; // ← sube a 6

    // ── Tabla usuarios ──────────────────────────────────────
    public static final String TABLE_USERS           = "users";
    public static final String COL_ID                = "id";
    public static final String COL_NAME              = "name";
    public static final String COL_EMAIL             = "email";
    public static final String COL_PASSWORD          = "password";
    public static final String COL_ROLE              = "role";
    public static final String COL_CREATED_AT        = "created_at";

    // ── Tabla estaciones ────────────────────────────────────
    public static final String TABLE_STATIONS        = "stations";
    public static final String COL_ST_NAME           = "st_name";
    public static final String COL_ADDRESS           = "address";
    public static final String COL_ZONE              = "zone";
    public static final String COL_PRICE_CORRIENTE   = "price_corriente";
    public static final String COL_PRICE_EXTRA       = "price_extra";
    public static final String COL_PRICE_ACPM        = "price_acpm";

    // ── Tabla inventario ────────────────────────────────────
    public static final String TABLE_INVENTORY       = "inventory_movements";
    public static final String COL_INV_FUEL_TYPE     = "fuel_type";
    public static final String COL_INV_MOV_TYPE      = "mov_type";
    public static final String COL_INV_VOLUME        = "volume_gal";
    public static final String COL_INV_NOTE          = "note";
    public static final String COL_INV_DATE          = "mov_date";
    public static final String COL_INV_STATION_ID    = "station_id";

    // ── Tabla precios normativos ────────────────────────────
    public static final String TABLE_NORMATIVE_PRICES = "normative_prices";
    public static final String COL_NP_FUEL_TYPE       = "fuel_type";
    public static final String COL_NP_PRICE           = "price_per_gallon";
    public static final String COL_NP_CITY            = "city";
    public static final String COL_NP_DATE            = "effective_date";
    public static final String COL_NORM_SOURCE        = "source";

    // ── Tabla ventas ────────────────────────────────────────
    public static final String TABLE_SALES            = "fuel_sales";
    public static final String COL_SALE_FUEL_TYPE     = "fuel_type";
    public static final String COL_SALE_VOLUME        = "volume_gal";
    public static final String COL_SALE_PRICE_GAL     = "price_per_gallon";
    public static final String COL_SALE_TOTAL         = "total_price";
    public static final String COL_SALE_PLATE         = "client_plate";
    public static final String COL_SALE_DATE          = "sale_date";
    public static final String COL_SALE_STATION_ID    = "station_id";

    // ── Tabla alertas de precio ─────────────────────────────
    public static final String TABLE_ALERTS           = "price_alerts";
    public static final String COL_AL_STATION_ID      = "station_id";
    public static final String COL_AL_STATION_NAME    = "station_name";
    public static final String COL_AL_FUEL_TYPE       = "fuel_type";
    public static final String COL_AL_LAST_PRICE      = "last_known_price";
    public static final String COL_AL_ACTIVE          = "active";
    public static final String COL_AL_USER_ID         = "user_id";

    // ── CREATE statements ───────────────────────────────────
    private static final String CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME + " TEXT NOT NULL, " +
                    COL_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COL_PASSWORD + " TEXT NOT NULL, " +
                    COL_ROLE + " TEXT NOT NULL DEFAULT 'CLIENTE', " +
                    COL_CREATED_AT + " TEXT NOT NULL);";

    private static final String CREATE_STATIONS =
            "CREATE TABLE " + TABLE_STATIONS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_ST_NAME + " TEXT NOT NULL, " +
                    COL_ADDRESS + " TEXT NOT NULL, " +
                    COL_ZONE + " TEXT NOT NULL, " +
                    COL_PRICE_CORRIENTE + " REAL NOT NULL, " +
                    COL_PRICE_EXTRA + " REAL NOT NULL, " +
                    COL_PRICE_ACPM + " REAL NOT NULL);";

    private static final String CREATE_INVENTORY =
            "CREATE TABLE " + TABLE_INVENTORY + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_INV_FUEL_TYPE + " TEXT NOT NULL, " +
                    COL_INV_MOV_TYPE + " TEXT NOT NULL, " +
                    COL_INV_VOLUME + " REAL NOT NULL, " +
                    COL_INV_NOTE + " TEXT, " +
                    COL_INV_DATE + " TEXT NOT NULL, " +
                    COL_INV_STATION_ID + " INTEGER NOT NULL);";

    private static final String CREATE_NORMATIVE_PRICES =
            "CREATE TABLE " + TABLE_NORMATIVE_PRICES + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NP_FUEL_TYPE + " TEXT NOT NULL, " +
                    COL_NP_PRICE + " REAL NOT NULL, " +
                    COL_NP_CITY + " TEXT NOT NULL, " +
                    COL_NP_DATE + " TEXT NOT NULL, " +
                    COL_NORM_SOURCE + " TEXT);";

    private static final String CREATE_SALES =
            "CREATE TABLE " + TABLE_SALES + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_SALE_FUEL_TYPE + " TEXT NOT NULL, " +
                    COL_SALE_VOLUME + " REAL NOT NULL, " +
                    COL_SALE_PRICE_GAL + " REAL NOT NULL, " +
                    COL_SALE_TOTAL + " REAL NOT NULL, " +
                    COL_SALE_PLATE + " TEXT, " +
                    COL_SALE_DATE + " TEXT NOT NULL, " +
                    COL_SALE_STATION_ID + " INTEGER NOT NULL);";

    private static final String CREATE_ALERTS =
            "CREATE TABLE " + TABLE_ALERTS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_AL_STATION_ID + " INTEGER NOT NULL, " +
                    COL_AL_STATION_NAME + " TEXT NOT NULL, " +
                    COL_AL_FUEL_TYPE + " TEXT NOT NULL, " +
                    COL_AL_LAST_PRICE + " REAL NOT NULL, " +
                    COL_AL_ACTIVE + " INTEGER NOT NULL DEFAULT 1, " +
                    COL_AL_USER_ID + " INTEGER NOT NULL, " +
                    "UNIQUE(" + COL_AL_STATION_ID + ", " + COL_AL_FUEL_TYPE + ", " + COL_AL_USER_ID + "));";

    // ── Singleton ───────────────────────────────────────────
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null)
            instance = new DatabaseHelper(context.getApplicationContext());
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS);
        db.execSQL(CREATE_STATIONS);
        db.execSQL(CREATE_INVENTORY);
        db.execSQL(CREATE_NORMATIVE_PRICES);
        db.execSQL(CREATE_SALES);
        db.execSQL(CREATE_ALERTS);
        insertDefaultAdmin(db);
        insertSeedStations(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALERTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SALES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NORMATIVE_PRICES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ── Seeds ──────────────────────────────────────────────
    private void insertDefaultAdmin(SQLiteDatabase db) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME,       "Administrador");
        v.put(COL_EMAIL,      "admin@fuelmanager.co");
        v.put(COL_PASSWORD,   PasswordUtil.hash("Admin1234"));
        v.put(COL_ROLE,       "ADMIN");
        v.put(COL_CREATED_AT, new java.util.Date().toString());
        db.insert(TABLE_USERS, null, v);
    }

    private void insertSeedStations(SQLiteDatabase db) {
        Object[][] seeds = {
                {"Terpel Autopista Norte",  "Autopista Norte Km 5",      "Bogotá Norte",     9450.0, 10200.0, 8900.0},
                {"Primax Calle 80",         "Calle 80 # 100-15",         "Bogotá Occidente", 9380.0, 10150.0, 8820.0},
                {"Biomax Usaquén",          "Carrera 7 # 128-30",        "Bogotá Norte",     9500.0, 10250.0, 8950.0},
                {"ExxonMobil Suba",         "Av. Suba # 115-40",         "Bogotá Norte",     9420.0, 10180.0, 8870.0},
                {"Terpel Américas",         "Av. Las Américas # 52-10",  "Bogotá Occidente", 9360.0, 10100.0, 8800.0},
                {"Primax Kennedy",          "Calle 38 Sur # 72-20",      "Bogotá Sur",       9310.0, 10050.0, 8750.0},
                {"Biomax Restrepo",         "Carrera 18 # 18-60 Sur",    "Bogotá Sur",       9280.0, 10020.0, 8720.0},
                {"Texaco Chapinero",        "Carrera 13 # 62-35",        "Bogotá Centro",    9470.0, 10220.0, 8920.0},
                {"ExxonMobil Candelaria",   "Calle 13 # 3-50",           "Bogotá Centro",    9400.0, 10160.0, 8850.0},
                {"Terpel Bosa",             "Calle 65 Sur # 80-10",      "Bogotá Sur",       9260.0, 10000.0, 8700.0},
        };
        for (Object[] s : seeds) {
            ContentValues v = new ContentValues();
            v.put(COL_ST_NAME,         (String) s[0]);
            v.put(COL_ADDRESS,         (String) s[1]);
            v.put(COL_ZONE,            (String) s[2]);
            v.put(COL_PRICE_CORRIENTE, (Double) s[3]);
            v.put(COL_PRICE_EXTRA,     (Double) s[4]);
            v.put(COL_PRICE_ACPM,      (Double) s[5]);
            db.insert(TABLE_STATIONS, null, v);
        }
    }

    // ── CRUD Usuarios ──────────────────────────────────────
    public long insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME,       user.getName());
        values.put(COL_EMAIL,      user.getEmail().toLowerCase().trim());
        values.put(COL_PASSWORD,   PasswordUtil.hash(user.getPassword()));
        values.put(COL_ROLE,       user.getRole());
        values.put(COL_CREATED_AT, new java.util.Date().toString());
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                COL_EMAIL + "=? AND " + COL_PASSWORD + "=?",
                new String[]{email.toLowerCase().trim(), PasswordUtil.hash(password)},
                null, null, null);
        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        db.close();
        return user;
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_ID},
                COL_EMAIL + "=?",
                new String[]{email.toLowerCase().trim()},
                null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }

    private User cursorToUser(Cursor c) {
        return new User(
                c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                c.getString(c.getColumnIndexOrThrow(COL_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(COL_PASSWORD)),
                c.getString(c.getColumnIndexOrThrow(COL_ROLE))
        );
    }

    // ── CRUD Precios Normativos ────────────────────────────
    public long insertNormativePrice(NormativePrice price) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NP_FUEL_TYPE, price.getFuelType());
        values.put(COL_NP_PRICE,     price.getPricePerGallon());
        values.put(COL_NP_CITY,      "BOGOTA");
        values.put(COL_NP_DATE,      price.getEffectiveDate());
        values.put(COL_NORM_SOURCE,  price.getSource());
        long id = db.insert(TABLE_NORMATIVE_PRICES, null, values);
        db.close();
        return id;
    }

    public List<NormativePrice> getNormativePrices() {
        List<NormativePrice> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NORMATIVE_PRICES
                + " ORDER BY " + COL_NP_FUEL_TYPE, null);
        if (c.moveToFirst()) {
            do {
                NormativePrice p = new NormativePrice();
                p.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
                p.setFuelType(c.getString(c.getColumnIndexOrThrow(COL_NP_FUEL_TYPE)));
                p.setPricePerGallon(c.getDouble(c.getColumnIndexOrThrow(COL_NP_PRICE)));
                p.setEffectiveDate(c.getString(c.getColumnIndexOrThrow(COL_NP_DATE)));
                p.setSource(c.getString(c.getColumnIndexOrThrow(COL_NORM_SOURCE)));
                list.add(p);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public void clearNormativePrices() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NORMATIVE_PRICES, null, null);
        db.close();
    }

    // ── CRUD Ventas ───────────────────────────────────────
    public long insertSale(FuelSale sale) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_SALE_FUEL_TYPE,  sale.getFuelType());
        values.put(COL_SALE_VOLUME,     sale.getVolumeGal());
        values.put(COL_SALE_PRICE_GAL,  sale.getPricePerGal());
        values.put(COL_SALE_TOTAL,      sale.getTotalPrice());
        values.put(COL_SALE_PLATE,      sale.getClientPlate());
        values.put(COL_SALE_DATE,       sale.getDate());
        values.put(COL_SALE_STATION_ID, sale.getStationId());
        long id = db.insert(TABLE_SALES, null, values);
        db.close();
        return id;
    }

    public List<FuelSale> getSales(int stationId) {
        List<FuelSale> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_SALES, null,
                COL_SALE_STATION_ID + "=?",
                new String[]{String.valueOf(stationId)},
                null, null, COL_ID + " DESC");
        if (c != null) {
            while (c.moveToNext()) {
                FuelSale s = new FuelSale();
                s.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
                s.setFuelType(c.getString(c.getColumnIndexOrThrow(COL_SALE_FUEL_TYPE)));
                s.setVolumeGal(c.getDouble(c.getColumnIndexOrThrow(COL_SALE_VOLUME)));
                s.setPricePerGal(c.getDouble(c.getColumnIndexOrThrow(COL_SALE_PRICE_GAL)));
                s.setTotalPrice(c.getDouble(c.getColumnIndexOrThrow(COL_SALE_TOTAL)));
                s.setClientPlate(c.getString(c.getColumnIndexOrThrow(COL_SALE_PLATE)));
                s.setDate(c.getString(c.getColumnIndexOrThrow(COL_SALE_DATE)));
                s.setStationId(c.getInt(c.getColumnIndexOrThrow(COL_SALE_STATION_ID)));
                list.add(s);
            }
            c.close();
        }
        db.close();
        return list;
    }

    public long insertInventoryMovement(String fuelType, String movType,
                                        double volume, String note, String date, int stationId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_INV_FUEL_TYPE,  fuelType);
        v.put(COL_INV_MOV_TYPE,   movType);
        v.put(COL_INV_VOLUME,     volume);
        v.put(COL_INV_NOTE,       note);
        v.put(COL_INV_DATE,       date);
        v.put(COL_INV_STATION_ID, stationId);
        long id = db.insert(TABLE_INVENTORY, null, v);
        db.close();
        return id;
    }

    // ── CRUD Alertas ──────────────────────────────────────
    /**
     * Activa una alerta. Si ya existe (misma estación + combustible + usuario),
     * la reactiva y actualiza el precio de referencia.
     */
    public long upsertAlert(PriceAlert alert) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_AL_STATION_ID,   alert.getStationId());
        v.put(COL_AL_STATION_NAME, alert.getStationName());
        v.put(COL_AL_FUEL_TYPE,    alert.getFuelType());
        v.put(COL_AL_LAST_PRICE,   alert.getLastKnownPrice());
        v.put(COL_AL_ACTIVE,       1);
        v.put(COL_AL_USER_ID,      alert.getUserId());
        // INSERT OR REPLACE aprovecha el UNIQUE constraint
        long id = db.insertWithOnConflict(TABLE_ALERTS, null, v,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return id;
    }

    /** Desactiva una alerta específica. */
    public void deactivateAlert(int stationId, String fuelType, int userId) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_AL_ACTIVE, 0);
        db.update(TABLE_ALERTS, v,
                COL_AL_STATION_ID + "=? AND " + COL_AL_FUEL_TYPE + "=? AND " + COL_AL_USER_ID + "=?",
                new String[]{String.valueOf(stationId), fuelType, String.valueOf(userId)});
        db.close();
    }

    /** Verifica si una alerta está activa para una estación+combustible+usuario. */
    public boolean isAlertActive(int stationId, String fuelType, int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_ALERTS, new String[]{COL_AL_ACTIVE},
                COL_AL_STATION_ID + "=? AND " + COL_AL_FUEL_TYPE + "=? AND "
                        + COL_AL_USER_ID + "=? AND " + COL_AL_ACTIVE + "=1",
                new String[]{String.valueOf(stationId), fuelType, String.valueOf(userId)},
                null, null, null);
        boolean active = c != null && c.getCount() > 0;
        if (c != null) c.close();
        db.close();
        return active;
    }

    /** Retorna todas las alertas activas de un usuario. */
    public List<PriceAlert> getActiveAlerts(int userId) {
        List<PriceAlert> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_ALERTS, null,
                COL_AL_USER_ID + "=? AND " + COL_AL_ACTIVE + "=1",
                new String[]{String.valueOf(userId)},
                null, null, COL_AL_STATION_NAME + " ASC");
        if (c != null) {
            while (c.moveToNext()) list.add(cursorToAlert(c));
            c.close();
        }
        db.close();
        return list;
    }

    /**
     * Compara el precio actual de cada estación vigilada contra el precio
     * guardado. Retorna las alertas cuyo precio cambió.
     */
    public List<PriceAlert> checkPriceChanges(int userId) {
        List<PriceAlert> changed = new ArrayList<>();
        List<PriceAlert> active  = getActiveAlerts(userId);
        SQLiteDatabase db = getReadableDatabase();

        for (PriceAlert alert : active) {
            String priceCol;
            switch (alert.getFuelType()) {
                case "Extra": priceCol = COL_PRICE_EXTRA;     break;
                case "ACPM":  priceCol = COL_PRICE_ACPM;      break;
                default:      priceCol = COL_PRICE_CORRIENTE; break;
            }

            Cursor c = db.query(TABLE_STATIONS,
                    new String[]{priceCol},
                    COL_ID + "=?",
                    new String[]{String.valueOf(alert.getStationId())},
                    null, null, null);

            if (c != null && c.moveToFirst()) {
                double currentPrice = c.getDouble(0);
                if (currentPrice != alert.getLastKnownPrice()) {
                    alert.setLastKnownPrice(currentPrice); // precio nuevo
                    changed.add(alert);
                }
                c.close();
            }
        }
        db.close();

        // Actualizar last_known_price para las que cambiaron
        for (PriceAlert a : changed) {
            SQLiteDatabase wdb = getWritableDatabase();
            ContentValues v = new ContentValues();
            v.put(COL_AL_LAST_PRICE, a.getLastKnownPrice());
            wdb.update(TABLE_ALERTS, v,
                    COL_AL_STATION_ID + "=? AND " + COL_AL_FUEL_TYPE + "=? AND " + COL_AL_USER_ID + "=?",
                    new String[]{String.valueOf(a.getStationId()), a.getFuelType(), String.valueOf(a.getUserId())});
            wdb.close();
        }

        return changed;
    }

    /** Actualiza precios de una estación y dispara revisión de alertas. */
    public boolean updateStationPrices(int stationId, double corriente,
                                       double extra, double acpm) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_PRICE_CORRIENTE, corriente);
        v.put(COL_PRICE_EXTRA,     extra);
        v.put(COL_PRICE_ACPM,      acpm);
        int rows = db.update(TABLE_STATIONS, v,
                COL_ID + "=?", new String[]{String.valueOf(stationId)});
        db.close();
        return rows > 0;
    }

    private PriceAlert cursorToAlert(Cursor c) {
        PriceAlert a = new PriceAlert();
        a.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
        a.setStationId(c.getInt(c.getColumnIndexOrThrow(COL_AL_STATION_ID)));
        a.setStationName(c.getString(c.getColumnIndexOrThrow(COL_AL_STATION_NAME)));
        a.setFuelType(c.getString(c.getColumnIndexOrThrow(COL_AL_FUEL_TYPE)));
        a.setLastKnownPrice(c.getDouble(c.getColumnIndexOrThrow(COL_AL_LAST_PRICE)));
        a.setActive(c.getInt(c.getColumnIndexOrThrow(COL_AL_ACTIVE)) == 1);
        a.setUserId(c.getInt(c.getColumnIndexOrThrow(COL_AL_USER_ID)));
        return a;
    }
}