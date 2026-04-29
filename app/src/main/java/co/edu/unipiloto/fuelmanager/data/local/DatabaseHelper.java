package co.edu.unipiloto.fuelmanager.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.fuelmanager.data.model.Delivery;
import co.edu.unipiloto.fuelmanager.data.model.FuelSale;
import co.edu.unipiloto.fuelmanager.data.model.NormativePrice;
import co.edu.unipiloto.fuelmanager.data.model.PriceAlert;
import co.edu.unipiloto.fuelmanager.data.model.PriceUpdate;
import co.edu.unipiloto.fuelmanager.data.model.Receipt;
import co.edu.unipiloto.fuelmanager.data.model.Station;
import co.edu.unipiloto.fuelmanager.data.model.Subsidy;
import co.edu.unipiloto.fuelmanager.data.model.User;
import co.edu.unipiloto.fuelmanager.data.model.WholesalePrice;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME    = "fuelmanager.db";
    private static final int    DATABASE_VERSION = 10; // ← v10

    // ── Tabla usuarios ──────────────────────────────────────
    public static final String TABLE_USERS           = "users";
    public static final String COL_ID                = "id";
    public static final String COL_NAME              = "name";
    public static final String COL_EMAIL             = "email";
    public static final String COL_PASSWORD          = "password";
    public static final String COL_ROLE              = "role";
    public static final String COL_CREATED_AT        = "created_at";
    public static final String COL_VEHICLE_TYPE      = "vehicle_type"; // ← NUEVO v10

    // ── Tabla estaciones ────────────────────────────────────
    public static final String TABLE_STATIONS        = "stations";
    public static final String COL_ST_NAME           = "st_name";
    public static final String COL_ADDRESS           = "address";
    public static final String COL_ZONE              = "zone";
    public static final String COL_PRICE_CORRIENTE   = "price_corriente";
    public static final String COL_PRICE_EXTRA       = "price_extra";
    public static final String COL_PRICE_ACPM        = "price_acpm";
    public static final String COL_USER_ID           = "user_id";

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

    // ── Tabla alertas ───────────────────────────────────────
    public static final String TABLE_ALERTS           = "price_alerts";
    public static final String COL_AL_STATION_ID      = "station_id";
    public static final String COL_AL_STATION_NAME    = "station_name";
    public static final String COL_AL_FUEL_TYPE       = "fuel_type";
    public static final String COL_AL_LAST_PRICE      = "last_known_price";
    public static final String COL_AL_ACTIVE          = "active";
    public static final String COL_AL_USER_ID         = "user_id";

    // ── Tabla entregas distribuidor ─────────────────────────
    public static final String TABLE_DELIVERIES       = "deliveries";
    public static final String COL_DL_STATION_ID      = "station_id";
    public static final String COL_DL_STATION_NAME    = "station_name";
    public static final String COL_DL_FUEL_TYPE       = "fuel_type";
    public static final String COL_DL_VOLUME          = "volume_gal";
    public static final String COL_DL_DATE            = "delivery_date";
    public static final String COL_DL_NOTES           = "notes";
    public static final String COL_DL_DISTRIBUTOR_ID  = "distributor_id";

    // ── Tabla actualización de precios (HU-04) ──────────────
    public static final String TABLE_PRICE_UPDATES    = "price_updates";
    public static final String COL_PU_STATION_ID      = "station_id";
    public static final String COL_PU_STATION_NAME    = "station_name";
    public static final String COL_PU_OLD_COR         = "old_corriente";
    public static final String COL_PU_NEW_COR         = "new_corriente";
    public static final String COL_PU_OLD_EXT         = "old_extra";
    public static final String COL_PU_NEW_EXT         = "new_extra";
    public static final String COL_PU_OLD_ACPM        = "old_acpm";
    public static final String COL_PU_NEW_ACPM        = "new_acpm";
    public static final String COL_PU_DATE            = "update_date";
    public static final String COL_PU_DIST_ID         = "distributor_id";

    // ── Tabla subsidios (HU-11) ─────────────────────────────
    public static final String TABLE_SUBSIDIES        = "subsidies";
    public static final String COL_SUB_TARGET_TYPE    = "target_type";
    public static final String COL_SUB_TARGET_VALUE   = "target_value";
    public static final String COL_SUB_FUEL_TYPE      = "fuel_type";
    public static final String COL_SUB_DISCOUNT_PCT   = "discount_pct";
    public static final String COL_SUB_START          = "start_date";
    public static final String COL_SUB_END            = "end_date";
    public static final String COL_SUB_NOTES          = "notes";
    public static final String COL_SUB_ACTIVE         = "active";
    public static final String COL_SUB_AUTH_ID        = "authority_id";

    // ── Tabla recibos (HU-12) ───────────────────────────────
    public static final String TABLE_RECEIPTS         = "receipts";
    public static final String COL_REC_SALE_ID        = "sale_id";
    public static final String COL_REC_FUEL_TYPE      = "fuel_type";
    public static final String COL_REC_VOLUME         = "volume_gal";
    public static final String COL_REC_PRICE_GAL      = "price_per_gallon";
    public static final String COL_REC_TOTAL          = "total";
    public static final String COL_REC_PLATE          = "client_plate";
    public static final String COL_REC_DATE           = "receipt_date";
    public static final String COL_REC_STATION_ID     = "station_id";

    // ── Tabla precios mayoristas (HU-13) ────────────────────
    public static final String TABLE_WHOLESALE        = "wholesale_prices";
    public static final String COL_WS_STATION_ID      = "station_id";
    public static final String COL_WS_STATION_NAME    = "station_name";
    public static final String COL_WS_FUEL_TYPE       = "fuel_type";
    public static final String COL_WS_PRICE           = "price_per_gallon";
    public static final String COL_WS_DATE            = "effective_date";
    public static final String COL_WS_DIST_ID         = "distributor_id";

    // ── Tipos de vehículo ───────────────────────────────────
    public static final String VEHICLE_CARRO  = "Carro";
    public static final String VEHICLE_MOTO   = "Moto";
    public static final String VEHICLE_CAMION = "Camión";

    // ── CREATE statements ───────────────────────────────────
    private static final String CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME + " TEXT NOT NULL, " +
                    COL_EMAIL + " TEXT NOT NULL UNIQUE, " +
                    COL_PASSWORD + " TEXT NOT NULL, " +
                    COL_ROLE + " TEXT NOT NULL DEFAULT 'CLIENTE', " +
                    COL_VEHICLE_TYPE + " TEXT, " +
                    COL_CREATED_AT + " TEXT NOT NULL);";

    private static final String CREATE_STATIONS =
            "CREATE TABLE " + TABLE_STATIONS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_ST_NAME + " TEXT NOT NULL, " +
                    COL_ADDRESS + " TEXT NOT NULL, " +
                    COL_ZONE + " TEXT NOT NULL, " +
                    COL_PRICE_CORRIENTE + " REAL NOT NULL, " +
                    COL_PRICE_EXTRA + " REAL NOT NULL, " +
                    COL_PRICE_ACPM + " REAL NOT NULL, " +
                    COL_USER_ID + " INTEGER DEFAULT -1);";

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

    private static final String CREATE_DELIVERIES =
            "CREATE TABLE " + TABLE_DELIVERIES + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_DL_STATION_ID + " INTEGER NOT NULL, " +
                    COL_DL_STATION_NAME + " TEXT NOT NULL, " +
                    COL_DL_FUEL_TYPE + " TEXT NOT NULL, " +
                    COL_DL_VOLUME + " REAL NOT NULL, " +
                    COL_DL_DATE + " TEXT NOT NULL, " +
                    COL_DL_NOTES + " TEXT, " +
                    COL_DL_DISTRIBUTOR_ID + " INTEGER NOT NULL);";

    private static final String CREATE_PRICE_UPDATES =
            "CREATE TABLE " + TABLE_PRICE_UPDATES + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_PU_STATION_ID + " INTEGER NOT NULL, " +
                    COL_PU_STATION_NAME + " TEXT NOT NULL, " +
                    COL_PU_OLD_COR + " REAL NOT NULL, " +
                    COL_PU_NEW_COR + " REAL NOT NULL, " +
                    COL_PU_OLD_EXT + " REAL NOT NULL, " +
                    COL_PU_NEW_EXT + " REAL NOT NULL, " +
                    COL_PU_OLD_ACPM + " REAL NOT NULL, " +
                    COL_PU_NEW_ACPM + " REAL NOT NULL, " +
                    COL_PU_DATE + " TEXT NOT NULL, " +
                    COL_PU_DIST_ID + " INTEGER NOT NULL);";

    private static final String CREATE_SUBSIDIES =
            "CREATE TABLE " + TABLE_SUBSIDIES + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_SUB_TARGET_TYPE + " TEXT NOT NULL, " +
                    COL_SUB_TARGET_VALUE + " TEXT NOT NULL, " +
                    COL_SUB_FUEL_TYPE + " TEXT NOT NULL, " +
                    COL_SUB_DISCOUNT_PCT + " REAL NOT NULL, " +
                    COL_SUB_START + " TEXT NOT NULL, " +
                    COL_SUB_END + " TEXT NOT NULL, " +
                    COL_SUB_NOTES + " TEXT, " +
                    COL_SUB_ACTIVE + " INTEGER NOT NULL DEFAULT 1, " +
                    COL_SUB_AUTH_ID + " INTEGER NOT NULL);";

    private static final String CREATE_RECEIPTS =
            "CREATE TABLE " + TABLE_RECEIPTS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_REC_SALE_ID + " INTEGER NOT NULL, " +
                    COL_REC_FUEL_TYPE + " TEXT NOT NULL, " +
                    COL_REC_VOLUME + " REAL NOT NULL, " +
                    COL_REC_PRICE_GAL + " REAL NOT NULL, " +
                    COL_REC_TOTAL + " REAL NOT NULL, " +
                    COL_REC_PLATE + " TEXT, " +
                    COL_REC_DATE + " TEXT NOT NULL, " +
                    COL_REC_STATION_ID + " INTEGER NOT NULL);";

    private static final String CREATE_WHOLESALE =
            "CREATE TABLE " + TABLE_WHOLESALE + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_WS_STATION_ID + " INTEGER NOT NULL, " +
                    COL_WS_STATION_NAME + " TEXT NOT NULL, " +
                    COL_WS_FUEL_TYPE + " TEXT NOT NULL, " +
                    COL_WS_PRICE + " REAL NOT NULL, " +
                    COL_WS_DATE + " TEXT NOT NULL, " +
                    COL_WS_DIST_ID + " INTEGER NOT NULL);";

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
        db.execSQL(CREATE_DELIVERIES);
        db.execSQL(CREATE_PRICE_UPDATES);
        db.execSQL(CREATE_SUBSIDIES);
        db.execSQL(CREATE_RECEIPTS);
        db.execSQL(CREATE_WHOLESALE);
        insertDefaultAdmin(db);
        insertSeedStations(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 8) {
            try { db.execSQL("ALTER TABLE " + TABLE_STATIONS + " ADD COLUMN " + COL_USER_ID + " INTEGER DEFAULT -1"); } catch (Exception ignored) {}
            try { db.execSQL(CREATE_PRICE_UPDATES); } catch (Exception ignored) {}
        }
        if (oldVersion < 9) {
            try { db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SUBSIDIES + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_SUB_TARGET_TYPE + " TEXT NOT NULL, " + COL_SUB_TARGET_VALUE + " TEXT NOT NULL, " + COL_SUB_FUEL_TYPE + " TEXT NOT NULL, " + COL_SUB_DISCOUNT_PCT + " REAL NOT NULL, " + COL_SUB_START + " TEXT NOT NULL, " + COL_SUB_END + " TEXT NOT NULL, " + COL_SUB_NOTES + " TEXT, " + COL_SUB_ACTIVE + " INTEGER NOT NULL DEFAULT 1, " + COL_SUB_AUTH_ID + " INTEGER NOT NULL);"); } catch (Exception ignored) {}
            try { db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RECEIPTS + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_REC_SALE_ID + " INTEGER NOT NULL, " + COL_REC_FUEL_TYPE + " TEXT NOT NULL, " + COL_REC_VOLUME + " REAL NOT NULL, " + COL_REC_PRICE_GAL + " REAL NOT NULL, " + COL_REC_TOTAL + " REAL NOT NULL, " + COL_REC_PLATE + " TEXT, " + COL_REC_DATE + " TEXT NOT NULL, " + COL_REC_STATION_ID + " INTEGER NOT NULL);"); } catch (Exception ignored) {}
        }
        if (oldVersion < 10) {
            // Agrega tipo de vehículo a usuarios existentes
            try { db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COL_VEHICLE_TYPE + " TEXT"); } catch (Exception ignored) {}
            // Crea tabla de precios mayoristas
            try { db.execSQL(CREATE_WHOLESALE); } catch (Exception ignored) {}
        }
    }

    // ── Seeds ───────────────────────────────────────────────
    private void insertDefaultAdmin(SQLiteDatabase db) {
        ContentValues v = new ContentValues();
        v.put(COL_NAME, "Administrador"); v.put(COL_EMAIL, "admin@fuelmanager.co");
        v.put(COL_PASSWORD, PasswordUtil.hash("Admin1234")); v.put(COL_ROLE, "ADMIN");
        v.put(COL_CREATED_AT, new java.util.Date().toString());
        db.insert(TABLE_USERS, null, v);
    }

    private void insertSeedStations(SQLiteDatabase db) {
        Object[][] seeds = {
                {"Terpel Autopista Norte","Autopista Norte Km 5","Bogotá Norte",9450.0,10200.0,8900.0},
                {"Primax Calle 80","Calle 80 # 100-15","Bogotá Occidente",9380.0,10150.0,8820.0},
                {"Biomax Usaquén","Carrera 7 # 128-30","Bogotá Norte",9500.0,10250.0,8950.0},
                {"ExxonMobil Suba","Av. Suba # 115-40","Bogotá Norte",9420.0,10180.0,8870.0},
                {"Terpel Américas","Av. Las Américas # 52-10","Bogotá Occidente",9360.0,10100.0,8800.0},
                {"Primax Kennedy","Calle 38 Sur # 72-20","Bogotá Sur",9310.0,10050.0,8750.0},
                {"Biomax Restrepo","Carrera 18 # 18-60 Sur","Bogotá Sur",9280.0,10020.0,8720.0},
                {"Texaco Chapinero","Carrera 13 # 62-35","Bogotá Centro",9470.0,10220.0,8920.0},
                {"ExxonMobil Candelaria","Calle 13 # 3-50","Bogotá Centro",9400.0,10160.0,8850.0},
                {"Terpel Bosa","Calle 65 Sur # 80-10","Bogotá Sur",9260.0,10000.0,8700.0},
        };
        for (Object[] s : seeds) {
            ContentValues v = new ContentValues();
            v.put(COL_ST_NAME,(String)s[0]); v.put(COL_ADDRESS,(String)s[1]);
            v.put(COL_ZONE,(String)s[2]); v.put(COL_PRICE_CORRIENTE,(Double)s[3]);
            v.put(COL_PRICE_EXTRA,(Double)s[4]); v.put(COL_PRICE_ACPM,(Double)s[5]);
            v.put(COL_USER_ID,-1);
            db.insert(TABLE_STATIONS, null, v);
        }
    }

    // ════════════════════════════════════════════════════════
    //  USUARIOS
    // ════════════════════════════════════════════════════════

    public long insertUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COL_NAME, user.getName());
        v.put(COL_EMAIL, user.getEmail().toLowerCase().trim());
        v.put(COL_PASSWORD, PasswordUtil.hash(user.getPassword()));
        v.put(COL_ROLE, user.getRole());
        v.put(COL_VEHICLE_TYPE, user.getVehicleType()); // puede ser null
        v.put(COL_CREATED_AT, new java.util.Date().toString());
        long id = db.insert(TABLE_USERS, null, v);
        db.close(); return id;
    }

    public long insertUserWithStation(User user, Station station) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction(); long userId = -1;
        try {
            ContentValues uv = new ContentValues();
            uv.put(COL_NAME, user.getName());
            uv.put(COL_EMAIL, user.getEmail().toLowerCase().trim());
            uv.put(COL_PASSWORD, PasswordUtil.hash(user.getPassword()));
            uv.put(COL_ROLE, user.getRole());
            uv.put(COL_VEHICLE_TYPE, user.getVehicleType());
            uv.put(COL_CREATED_AT, new java.util.Date().toString());
            userId = db.insert(TABLE_USERS, null, uv);

            ContentValues sv = new ContentValues();
            sv.put(COL_ST_NAME, station.getName()); sv.put(COL_ADDRESS, station.getAddress());
            sv.put(COL_ZONE, station.getZone()); sv.put(COL_PRICE_CORRIENTE, station.getPriceCorriente());
            sv.put(COL_PRICE_EXTRA, station.getPriceExtra()); sv.put(COL_PRICE_ACPM, station.getPriceAcpm());
            sv.put(COL_USER_ID, userId);
            db.insert(TABLE_STATIONS, null, sv);
            db.setTransactionSuccessful();
        } finally { db.endTransaction(); db.close(); }
        return userId;
    }

    public User loginUser(String email, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, null,
                COL_EMAIL + "=? AND " + COL_PASSWORD + "=?",
                new String[]{email.toLowerCase().trim(), PasswordUtil.hash(password)},
                null, null, null);
        User user = null;
        if (c != null && c.moveToFirst()) { user = cursorToUser(c); c.close(); }
        db.close(); return user;
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, new String[]{COL_ID},
                COL_EMAIL + "=?", new String[]{email.toLowerCase().trim()},
                null, null, null);
        boolean exists = c != null && c.getCount() > 0;
        if (c != null) c.close(); db.close(); return exists;
    }

    private User cursorToUser(Cursor c) {
        User u = new User(
                c.getInt(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                c.getString(c.getColumnIndexOrThrow(COL_EMAIL)),
                c.getString(c.getColumnIndexOrThrow(COL_PASSWORD)),
                c.getString(c.getColumnIndexOrThrow(COL_ROLE)));
        int vtIdx = c.getColumnIndex(COL_VEHICLE_TYPE);
        if (vtIdx >= 0) u.setVehicleType(c.getString(vtIdx));
        return u;
    }

    // ════════════════════════════════════════════════════════
    //  ESTACIONES
    // ════════════════════════════════════════════════════════

    public List<Station> getAllStations() {
        List<Station> list = new ArrayList<>(); SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_STATIONS, null, null, null, null, null, COL_PRICE_CORRIENTE + " ASC");
        if (c != null) { while (c.moveToNext()) list.add(cursorToStation(c)); c.close(); }
        db.close(); return list;
    }
    public List<Station> getAllStationsSimple() { return getAllStations(); }

    public List<Station> getStationsByZone(String zone) {
        List<Station> list = new ArrayList<>(); SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_STATIONS, null, COL_ZONE + "=?", new String[]{zone},
                null, null, COL_PRICE_CORRIENTE + " ASC");
        if (c != null) { while (c.moveToNext()) list.add(cursorToStation(c)); c.close(); }
        db.close(); return list;
    }

    public Station getStationById(int stationId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_STATIONS, null, COL_ID + "=?",
                new String[]{String.valueOf(stationId)}, null, null, null);
        Station s = null;
        if (c != null && c.moveToFirst()) { s = cursorToStation(c); c.close(); }
        db.close(); return s;
    }

    public int getStationIdByUserId(int userId) {
        SQLiteDatabase db = getReadableDatabase(); int stationId = -1;
        Cursor c = db.query(TABLE_STATIONS, new String[]{COL_ID},
                COL_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, null);
        if (c != null && c.moveToFirst()) { stationId = c.getInt(0); c.close(); }
        db.close(); return stationId;
    }

    public boolean updateStationPrices(int stationId, double corriente, double extra, double acpm) {
        SQLiteDatabase db = getWritableDatabase(); ContentValues v = new ContentValues();
        v.put(COL_PRICE_CORRIENTE, corriente); v.put(COL_PRICE_EXTRA, extra); v.put(COL_PRICE_ACPM, acpm);
        int rows = db.update(TABLE_STATIONS, v, COL_ID + "=?", new String[]{String.valueOf(stationId)});
        db.close(); return rows > 0;
    }

    private Station cursorToStation(Cursor c) {
        Station s = new Station();
        s.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
        s.setName(c.getString(c.getColumnIndexOrThrow(COL_ST_NAME)));
        s.setAddress(c.getString(c.getColumnIndexOrThrow(COL_ADDRESS)));
        s.setZone(c.getString(c.getColumnIndexOrThrow(COL_ZONE)));
        s.setPriceCorriente(c.getDouble(c.getColumnIndexOrThrow(COL_PRICE_CORRIENTE)));
        s.setPriceExtra(c.getDouble(c.getColumnIndexOrThrow(COL_PRICE_EXTRA)));
        s.setPriceAcpm(c.getDouble(c.getColumnIndexOrThrow(COL_PRICE_ACPM)));
        return s;
    }

    // ════════════════════════════════════════════════════════
    //  INVENTARIO
    // ════════════════════════════════════════════════════════

    public long insertInventoryMovement(String fuelType, String movType,
                                        double volume, String note, String date, int stationId) {
        SQLiteDatabase db = getWritableDatabase(); ContentValues v = new ContentValues();
        v.put(COL_INV_FUEL_TYPE, fuelType); v.put(COL_INV_MOV_TYPE, movType);
        v.put(COL_INV_VOLUME, volume); v.put(COL_INV_NOTE, note);
        v.put(COL_INV_DATE, date); v.put(COL_INV_STATION_ID, stationId);
        long id = db.insert(TABLE_INVENTORY, null, v); db.close(); return id;
    }

    public List<co.edu.unipiloto.fuelmanager.data.model.InventoryMovement> getMovementsByStation(int stationId) {
        List<co.edu.unipiloto.fuelmanager.data.model.InventoryMovement> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_INVENTORY, null, COL_INV_STATION_ID + "=?",
                new String[]{String.valueOf(stationId)}, null, null, COL_ID + " DESC");
        if (c != null) { while (c.moveToNext()) list.add(cursorToMovement(c)); c.close(); }
        db.close(); return list;
    }

    public co.edu.unipiloto.fuelmanager.data.model.InventoryStock getStockByStation(int stationId) {
        List<co.edu.unipiloto.fuelmanager.data.model.InventoryMovement> movements = getMovementsByStation(stationId);
        double cor = 0, ext = 0, acp = 0;
        for (co.edu.unipiloto.fuelmanager.data.model.InventoryMovement m : movements) {
            double vol = m.getMovType().equals(co.edu.unipiloto.fuelmanager.data.model.InventoryMovement.TYPE_ENTRADA)
                    ? m.getVolumeGal() : -m.getVolumeGal();
            switch (m.getFuelType()) {
                case co.edu.unipiloto.fuelmanager.data.model.InventoryMovement.FUEL_EXTRA: ext += vol; break;
                case co.edu.unipiloto.fuelmanager.data.model.InventoryMovement.FUEL_ACPM:  acp += vol; break;
                default: cor += vol; break;
            }
        }
        return new co.edu.unipiloto.fuelmanager.data.model.InventoryStock(Math.max(0,cor), Math.max(0,ext), Math.max(0,acp));
    }

    private co.edu.unipiloto.fuelmanager.data.model.InventoryMovement cursorToMovement(Cursor c) {
        co.edu.unipiloto.fuelmanager.data.model.InventoryMovement m =
                new co.edu.unipiloto.fuelmanager.data.model.InventoryMovement();
        m.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
        m.setFuelType(c.getString(c.getColumnIndexOrThrow(COL_INV_FUEL_TYPE)));
        m.setMovType(c.getString(c.getColumnIndexOrThrow(COL_INV_MOV_TYPE)));
        m.setVolumeGal(c.getDouble(c.getColumnIndexOrThrow(COL_INV_VOLUME)));
        m.setNote(c.getString(c.getColumnIndexOrThrow(COL_INV_NOTE)));
        m.setDate(c.getString(c.getColumnIndexOrThrow(COL_INV_DATE)));
        m.setStationId(c.getInt(c.getColumnIndexOrThrow(COL_INV_STATION_ID)));
        return m;
    }

    // ════════════════════════════════════════════════════════
    //  PRECIOS NORMATIVOS
    // ════════════════════════════════════════════════════════

    public long insertNormativePrice(NormativePrice price) {
        SQLiteDatabase db = getWritableDatabase(); ContentValues v = new ContentValues();
        v.put(COL_NP_FUEL_TYPE, price.getFuelType()); v.put(COL_NP_PRICE, price.getPricePerGallon());
        v.put(COL_NP_CITY, "BOGOTA"); v.put(COL_NP_DATE, price.getEffectiveDate());
        v.put(COL_NORM_SOURCE, price.getSource());
        long id = db.insert(TABLE_NORMATIVE_PRICES, null, v); db.close(); return id;
    }

    public List<NormativePrice> getNormativePrices() {
        List<NormativePrice> list = new ArrayList<>(); SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_NORMATIVE_PRICES + " ORDER BY " + COL_NP_FUEL_TYPE, null);
        if (c.moveToFirst()) { do {
            NormativePrice p = new NormativePrice();
            p.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
            p.setFuelType(c.getString(c.getColumnIndexOrThrow(COL_NP_FUEL_TYPE)));
            p.setPricePerGallon(c.getDouble(c.getColumnIndexOrThrow(COL_NP_PRICE)));
            p.setEffectiveDate(c.getString(c.getColumnIndexOrThrow(COL_NP_DATE)));
            p.setSource(c.getString(c.getColumnIndexOrThrow(COL_NORM_SOURCE)));
            list.add(p);
        } while (c.moveToNext()); }
        c.close(); db.close(); return list;
    }

    public void clearNormativePrices() {
        SQLiteDatabase db = getWritableDatabase(); db.delete(TABLE_NORMATIVE_PRICES, null, null); db.close();
    }

    // ════════════════════════════════════════════════════════
    //  VENTAS
    // ════════════════════════════════════════════════════════

    public long insertSale(FuelSale sale) {
        SQLiteDatabase db = getWritableDatabase(); ContentValues v = new ContentValues();
        v.put(COL_SALE_FUEL_TYPE, sale.getFuelType()); v.put(COL_SALE_VOLUME, sale.getVolumeGal());
        v.put(COL_SALE_PRICE_GAL, sale.getPricePerGal()); v.put(COL_SALE_TOTAL, sale.getTotalPrice());
        v.put(COL_SALE_PLATE, sale.getClientPlate()); v.put(COL_SALE_DATE, sale.getDate());
        v.put(COL_SALE_STATION_ID, sale.getStationId());
        long id = db.insert(TABLE_SALES, null, v); db.close(); return id;
    }

    public List<FuelSale> getSales(int stationId) {
        List<FuelSale> list = new ArrayList<>(); SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_SALES, null, COL_SALE_STATION_ID + "=?",
                new String[]{String.valueOf(stationId)}, null, null, COL_ID + " DESC");
        if (c != null) { while (c.moveToNext()) list.add(cursorToSale(c)); c.close(); }
        db.close(); return list;
    }

    public List<FuelSale> getAllSales() {
        List<FuelSale> list = new ArrayList<>(); SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_SALES, null, null, null, null, null, COL_ID + " DESC");
        if (c != null) { while (c.moveToNext()) list.add(cursorToSale(c)); c.close(); }
        db.close(); return list;
    }

    private FuelSale cursorToSale(Cursor c) {
        FuelSale s = new FuelSale();
        s.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
        s.setFuelType(c.getString(c.getColumnIndexOrThrow(COL_SALE_FUEL_TYPE)));
        s.setVolumeGal(c.getDouble(c.getColumnIndexOrThrow(COL_SALE_VOLUME)));
        s.setPricePerGal(c.getDouble(c.getColumnIndexOrThrow(COL_SALE_PRICE_GAL)));
        s.setTotalPrice(c.getDouble(c.getColumnIndexOrThrow(COL_SALE_TOTAL)));
        s.setClientPlate(c.getString(c.getColumnIndexOrThrow(COL_SALE_PLATE)));
        s.setDate(c.getString(c.getColumnIndexOrThrow(COL_SALE_DATE)));
        s.setStationId(c.getInt(c.getColumnIndexOrThrow(COL_SALE_STATION_ID)));
        return s;
    }

    // ════════════════════════════════════════════════════════
    //  ALERTAS DE PRECIO
    // ════════════════════════════════════════════════════════

    public long upsertAlert(PriceAlert alert) {
        SQLiteDatabase db = getWritableDatabase(); ContentValues v = new ContentValues();
        v.put(COL_AL_STATION_ID, alert.getStationId()); v.put(COL_AL_STATION_NAME, alert.getStationName());
        v.put(COL_AL_FUEL_TYPE, alert.getFuelType()); v.put(COL_AL_LAST_PRICE, alert.getLastKnownPrice());
        v.put(COL_AL_ACTIVE, 1); v.put(COL_AL_USER_ID, alert.getUserId());
        long id = db.insertWithOnConflict(TABLE_ALERTS, null, v, SQLiteDatabase.CONFLICT_REPLACE);
        db.close(); return id;
    }

    public void deactivateAlert(int stationId, String fuelType, int userId) {
        SQLiteDatabase db = getWritableDatabase(); ContentValues v = new ContentValues(); v.put(COL_AL_ACTIVE, 0);
        db.update(TABLE_ALERTS, v, COL_AL_STATION_ID + "=? AND " + COL_AL_FUEL_TYPE + "=? AND " + COL_AL_USER_ID + "=?",
                new String[]{String.valueOf(stationId), fuelType, String.valueOf(userId)});
        db.close();
    }

    public boolean isAlertActive(int stationId, String fuelType, int userId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_ALERTS, new String[]{COL_AL_ACTIVE},
                COL_AL_STATION_ID + "=? AND " + COL_AL_FUEL_TYPE + "=? AND " + COL_AL_USER_ID + "=? AND " + COL_AL_ACTIVE + "=1",
                new String[]{String.valueOf(stationId), fuelType, String.valueOf(userId)}, null, null, null);
        boolean active = c != null && c.getCount() > 0; if (c != null) c.close(); db.close(); return active;
    }

    public List<PriceAlert> getActiveAlerts(int userId) {
        List<PriceAlert> list = new ArrayList<>(); SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_ALERTS, null, COL_AL_USER_ID + "=? AND " + COL_AL_ACTIVE + "=1",
                new String[]{String.valueOf(userId)}, null, null, COL_AL_STATION_NAME + " ASC");
        if (c != null) { while (c.moveToNext()) list.add(cursorToAlert(c)); c.close(); }
        db.close(); return list;
    }

    public List<PriceAlert> checkPriceChanges(int userId) {
        List<PriceAlert> changed = new ArrayList<>();
        List<PriceAlert> active = getActiveAlerts(userId);
        SQLiteDatabase db = getReadableDatabase();
        for (PriceAlert alert : active) {
            String priceCol;
            switch (alert.getFuelType()) {
                case "Extra": priceCol = COL_PRICE_EXTRA; break;
                case "ACPM":  priceCol = COL_PRICE_ACPM;  break;
                default:      priceCol = COL_PRICE_CORRIENTE; break;
            }
            Cursor c = db.query(TABLE_STATIONS, new String[]{priceCol}, COL_ID + "=?",
                    new String[]{String.valueOf(alert.getStationId())}, null, null, null);
            if (c != null && c.moveToFirst()) {
                double cur = c.getDouble(0);
                if (cur != alert.getLastKnownPrice()) { alert.setLastKnownPrice(cur); changed.add(alert); }
                c.close();
            }
        }
        db.close();
        for (PriceAlert a : changed) {
            SQLiteDatabase wdb = getWritableDatabase(); ContentValues v = new ContentValues();
            v.put(COL_AL_LAST_PRICE, a.getLastKnownPrice());
            wdb.update(TABLE_ALERTS, v, COL_AL_STATION_ID + "=? AND " + COL_AL_FUEL_TYPE + "=? AND " + COL_AL_USER_ID + "=?",
                    new String[]{String.valueOf(a.getStationId()), a.getFuelType(), String.valueOf(a.getUserId())});
            wdb.close();
        }
        return changed;
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

    // ════════════════════════════════════════════════════════
    //  ENTREGAS DISTRIBUIDOR
    // ════════════════════════════════════════════════════════

    public long insertDelivery(Delivery delivery) {
        String now = delivery.getDate(); SQLiteDatabase db = getWritableDatabase(); ContentValues v = new ContentValues();
        v.put(COL_DL_STATION_ID, delivery.getStationId()); v.put(COL_DL_STATION_NAME, delivery.getStationName());
        v.put(COL_DL_FUEL_TYPE, delivery.getFuelType()); v.put(COL_DL_VOLUME, delivery.getVolumeGal());
        v.put(COL_DL_DATE, now); v.put(COL_DL_NOTES, delivery.getNotes());
        v.put(COL_DL_DISTRIBUTOR_ID, delivery.getDistributorId());
        long deliveryId = db.insert(TABLE_DELIVERIES, null, v); db.close();
        String note = "Entrega #" + deliveryId + " · Distribuidor";
        if (delivery.getNotes() != null && !delivery.getNotes().isEmpty()) note += " · " + delivery.getNotes();
        insertInventoryMovement(delivery.getFuelType(), "ENTRADA", delivery.getVolumeGal(), note, now, delivery.getStationId());
        return deliveryId;
    }

    public List<Delivery> getDeliveries(int distributorId) {
        List<Delivery> list = new ArrayList<>(); SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_DELIVERIES, null, COL_DL_DISTRIBUTOR_ID + "=?",
                new String[]{String.valueOf(distributorId)}, null, null, COL_ID + " DESC");
        if (c != null) { while (c.moveToNext()) list.add(cursorToDelivery(c)); c.close(); }
        db.close(); return list;
    }

    public List<Delivery> getAllDeliveries() {
        List<Delivery> list = new ArrayList<>(); SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_DELIVERIES, null, null, null, null, null, COL_ID + " DESC");
        if (c != null) { while (c.moveToNext()) list.add(cursorToDelivery(c)); c.close(); }
        db.close(); return list;
    }

    private Delivery cursorToDelivery(Cursor c) {
        Delivery d = new Delivery();
        d.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
        d.setStationId(c.getInt(c.getColumnIndexOrThrow(COL_DL_STATION_ID)));
        d.setStationName(c.getString(c.getColumnIndexOrThrow(COL_DL_STATION_NAME)));
        d.setFuelType(c.getString(c.getColumnIndexOrThrow(COL_DL_FUEL_TYPE)));
        d.setVolumeGal(c.getDouble(c.getColumnIndexOrThrow(COL_DL_VOLUME)));
        d.setDate(c.getString(c.getColumnIndexOrThrow(COL_DL_DATE)));
        d.setNotes(c.getString(c.getColumnIndexOrThrow(COL_DL_NOTES)));
        d.setDistributorId(c.getInt(c.getColumnIndexOrThrow(COL_DL_DISTRIBUTOR_ID)));
        return d;
    }

    // ════════════════════════════════════════════════════════
    //  ACTUALIZACIÓN DE PRECIOS (HU-04)
    // ════════════════════════════════════════════════════════

    public long insertPriceUpdate(PriceUpdate pu) {
        SQLiteDatabase db = getWritableDatabase(); ContentValues v = new ContentValues();
        v.put(COL_PU_STATION_ID, pu.getStationId()); v.put(COL_PU_STATION_NAME, pu.getStationName());
        v.put(COL_PU_OLD_COR, pu.getOldCorriente()); v.put(COL_PU_NEW_COR, pu.getNewCorriente());
        v.put(COL_PU_OLD_EXT, pu.getOldExtra());     v.put(COL_PU_NEW_EXT, pu.getNewExtra());
        v.put(COL_PU_OLD_ACPM, pu.getOldAcpm());     v.put(COL_PU_NEW_ACPM, pu.getNewAcpm());
        v.put(COL_PU_DATE, pu.getDate());             v.put(COL_PU_DIST_ID, pu.getDistributorId());
        long id = db.insert(TABLE_PRICE_UPDATES, null, v); db.close(); return id;
    }

    public List<PriceUpdate> getAllPriceUpdates() {
        List<PriceUpdate> list = new ArrayList<>(); SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PRICE_UPDATES, null, null, null, null, null, COL_ID + " DESC");
        if (c != null) { while (c.moveToNext()) {
            PriceUpdate pu = new PriceUpdate();
            pu.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
            pu.setStationId(c.getInt(c.getColumnIndexOrThrow(COL_PU_STATION_ID)));
            pu.setStationName(c.getString(c.getColumnIndexOrThrow(COL_PU_STATION_NAME)));
            pu.setOldCorriente(c.getDouble(c.getColumnIndexOrThrow(COL_PU_OLD_COR)));
            pu.setNewCorriente(c.getDouble(c.getColumnIndexOrThrow(COL_PU_NEW_COR)));
            pu.setOldExtra(c.getDouble(c.getColumnIndexOrThrow(COL_PU_OLD_EXT)));
            pu.setNewExtra(c.getDouble(c.getColumnIndexOrThrow(COL_PU_NEW_EXT)));
            pu.setOldAcpm(c.getDouble(c.getColumnIndexOrThrow(COL_PU_OLD_ACPM)));
            pu.setNewAcpm(c.getDouble(c.getColumnIndexOrThrow(COL_PU_NEW_ACPM)));
            pu.setDate(c.getString(c.getColumnIndexOrThrow(COL_PU_DATE)));
            pu.setDistributorId(c.getInt(c.getColumnIndexOrThrow(COL_PU_DIST_ID)));
            list.add(pu);
        } c.close(); }
        db.close(); return list;
    }

    // ════════════════════════════════════════════════════════
    //  SUBSIDIOS (HU-11)
    // ════════════════════════════════════════════════════════

    public long insertSubsidy(Subsidy s) {
        SQLiteDatabase db = getWritableDatabase(); ContentValues v = new ContentValues();
        v.put(COL_SUB_TARGET_TYPE, s.getTargetType()); v.put(COL_SUB_TARGET_VALUE, s.getTargetValue());
        v.put(COL_SUB_FUEL_TYPE, s.getFuelType()); v.put(COL_SUB_DISCOUNT_PCT, s.getDiscountPct());
        v.put(COL_SUB_START, s.getStartDate()); v.put(COL_SUB_END, s.getEndDate());
        v.put(COL_SUB_NOTES, s.getNotes()); v.put(COL_SUB_ACTIVE, s.isActive() ? 1 : 0);
        v.put(COL_SUB_AUTH_ID, s.getAuthorityId());
        long id = db.insert(TABLE_SUBSIDIES, null, v); db.close(); return id;
    }

    public List<Subsidy> getAllSubsidies() {
        List<Subsidy> list = new ArrayList<>(); SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_SUBSIDIES, null, null, null, null, null, COL_ID + " DESC");
        if (c != null) { while (c.moveToNext()) {
            Subsidy s = new Subsidy();
            s.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
            s.setTargetType(c.getString(c.getColumnIndexOrThrow(COL_SUB_TARGET_TYPE)));
            s.setTargetValue(c.getString(c.getColumnIndexOrThrow(COL_SUB_TARGET_VALUE)));
            s.setFuelType(c.getString(c.getColumnIndexOrThrow(COL_SUB_FUEL_TYPE)));
            s.setDiscountPct(c.getDouble(c.getColumnIndexOrThrow(COL_SUB_DISCOUNT_PCT)));
            s.setStartDate(c.getString(c.getColumnIndexOrThrow(COL_SUB_START)));
            s.setEndDate(c.getString(c.getColumnIndexOrThrow(COL_SUB_END)));
            s.setNotes(c.getString(c.getColumnIndexOrThrow(COL_SUB_NOTES)));
            s.setActive(c.getInt(c.getColumnIndexOrThrow(COL_SUB_ACTIVE)) == 1);
            s.setAuthorityId(c.getInt(c.getColumnIndexOrThrow(COL_SUB_AUTH_ID)));
            list.add(s);
        } c.close(); }
        db.close(); return list;
    }

    public void deactivateSubsidy(int subsidyId) {
        SQLiteDatabase db = getWritableDatabase(); ContentValues v = new ContentValues(); v.put(COL_SUB_ACTIVE, 0);
        db.update(TABLE_SUBSIDIES, v, COL_ID + "=?", new String[]{String.valueOf(subsidyId)}); db.close();
    }

    public List<User> getUsersByRole(String role) {
        List<User> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_USERS, null,
                COL_ROLE + "=?", new String[]{role},
                null, null, COL_NAME + " ASC");
        if (c != null) {
            while (c.moveToNext()) list.add(cursorToUser(c));
            c.close();
        }
        db.close();
        return list;
    }

    // ════════════════════════════════════════════════════════
    //  SUBSIDIOS ACTIVOS (para aplicar en pantalla de ventas)
    // ════════════════════════════════════════════════════════



     public Subsidy getActiveSubsidyForUser(String targetValue, String fuelType) {
     SQLiteDatabase db = getReadableDatabase();
     // Busca subsidio activo para ese value y ese combustible (o TODOS)
     Cursor c = db.query(TABLE_SUBSIDIES, null,
     COL_SUB_TARGET_VALUE + "=? AND (" +
     COL_SUB_FUEL_TYPE + "=? OR " + COL_SUB_FUEL_TYPE + "='TODOS') AND " +
     COL_SUB_ACTIVE + "=1",
     new String[]{targetValue, fuelType},
     null, null, COL_ID + " DESC", "1");
     Subsidy s = null;
     if (c != null && c.moveToFirst()) { s = cursorToSubsidy(c); c.close(); }
     db.close();
     return s;
     }

     public Subsidy getActiveSubsidyByZone(String zone, String fuelType) {
     SQLiteDatabase db = getReadableDatabase();
     Cursor c = db.query(TABLE_SUBSIDIES, null,
     COL_SUB_TARGET_TYPE + "='REGION' AND " +
     COL_SUB_TARGET_VALUE + "=? AND (" +
     COL_SUB_FUEL_TYPE + "=? OR " + COL_SUB_FUEL_TYPE + "='TODOS') AND " +
     COL_SUB_ACTIVE + "=1",
     new String[]{zone, fuelType},
     null, null, COL_ID + " DESC", "1");
     Subsidy s = null;
     if (c != null && c.moveToFirst()) { s = cursorToSubsidy(c); c.close(); }
     db.close();
     return s;
     }

     private Subsidy cursorToSubsidy(Cursor c) {
     Subsidy s = new Subsidy();
     s.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
     s.setTargetType(c.getString(c.getColumnIndexOrThrow(COL_SUB_TARGET_TYPE)));
     s.setTargetValue(c.getString(c.getColumnIndexOrThrow(COL_SUB_TARGET_VALUE)));
     s.setFuelType(c.getString(c.getColumnIndexOrThrow(COL_SUB_FUEL_TYPE)));
     s.setDiscountPct(c.getDouble(c.getColumnIndexOrThrow(COL_SUB_DISCOUNT_PCT)));
     s.setStartDate(c.getString(c.getColumnIndexOrThrow(COL_SUB_START)));
     s.setEndDate(c.getString(c.getColumnIndexOrThrow(COL_SUB_END)));
     s.setNotes(c.getString(c.getColumnIndexOrThrow(COL_SUB_NOTES)));
     s.setActive(c.getInt(c.getColumnIndexOrThrow(COL_SUB_ACTIVE)) == 1);
     s.setAuthorityId(c.getInt(c.getColumnIndexOrThrow(COL_SUB_AUTH_ID)));
     return s;
     }

    // ════════════════════════════════════════════════════════
    //  RECIBOS (HU-12)
    // ════════════════════════════════════════════════════════

    public long insertReceipt(long saleId, String fuelType, double volume,
                              double pricePerGal, double total,
                              String plate, String date, int stationId) {
        SQLiteDatabase db = getWritableDatabase(); ContentValues v = new ContentValues();
        v.put(COL_REC_SALE_ID, saleId); v.put(COL_REC_FUEL_TYPE, fuelType);
        v.put(COL_REC_VOLUME, volume); v.put(COL_REC_PRICE_GAL, pricePerGal);
        v.put(COL_REC_TOTAL, total); v.put(COL_REC_PLATE, plate);
        v.put(COL_REC_DATE, date); v.put(COL_REC_STATION_ID, stationId);
        long id = db.insert(TABLE_RECEIPTS, null, v); db.close(); return id;
    }

    public List<Receipt> getReceiptsByStation(int stationId) {
        List<Receipt> list = new ArrayList<>(); SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECEIPTS, null, COL_REC_STATION_ID + "=?",
                new String[]{String.valueOf(stationId)}, null, null, COL_ID + " DESC");
        if (c != null) { while (c.moveToNext()) list.add(cursorToReceipt(c)); c.close(); }
        db.close(); return list;
    }

    public List<Receipt> getAllReceipts() {
        List<Receipt> list = new ArrayList<>(); SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECEIPTS, null, null, null, null, null, COL_ID + " DESC");
        if (c != null) { while (c.moveToNext()) list.add(cursorToReceipt(c)); c.close(); }
        db.close(); return list;
    }

    public Receipt getReceiptBySaleId(long saleId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECEIPTS, null, COL_REC_SALE_ID + "=?",
                new String[]{String.valueOf(saleId)}, null, null, null);
        Receipt r = null;
        if (c != null && c.moveToFirst()) { r = cursorToReceipt(c); c.close(); }
        db.close(); return r;
    }

    private Receipt cursorToReceipt(Cursor c) {
        Receipt r = new Receipt();
        r.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
        r.setSaleId(c.getLong(c.getColumnIndexOrThrow(COL_REC_SALE_ID)));
        r.setFuelType(c.getString(c.getColumnIndexOrThrow(COL_REC_FUEL_TYPE)));
        r.setVolumeGal(c.getDouble(c.getColumnIndexOrThrow(COL_REC_VOLUME)));
        r.setPricePerGal(c.getDouble(c.getColumnIndexOrThrow(COL_REC_PRICE_GAL)));
        r.setTotal(c.getDouble(c.getColumnIndexOrThrow(COL_REC_TOTAL)));
        r.setClientPlate(c.getString(c.getColumnIndexOrThrow(COL_REC_PLATE)));
        r.setDate(c.getString(c.getColumnIndexOrThrow(COL_REC_DATE)));
        r.setStationId(c.getInt(c.getColumnIndexOrThrow(COL_REC_STATION_ID)));
        return r;
    }

    // ════════════════════════════════════════════════════════
    //  PRECIOS MAYORISTAS (HU-13)
    // ════════════════════════════════════════════════════════

    /**
     * Inserta un precio mayorista. Si ya existe uno para la misma
     * estación + combustible, se agrega como nuevo registro histórico.
     */
    public long insertWholesalePrice(WholesalePrice wp) {
        SQLiteDatabase db = getWritableDatabase(); ContentValues v = new ContentValues();
        v.put(COL_WS_STATION_ID,   wp.getStationId());
        v.put(COL_WS_STATION_NAME, wp.getStationName());
        v.put(COL_WS_FUEL_TYPE,    wp.getFuelType());
        v.put(COL_WS_PRICE,        wp.getPricePerGallon());
        v.put(COL_WS_DATE,         wp.getEffectiveDate());
        v.put(COL_WS_DIST_ID,      wp.getDistributorId());
        long id = db.insert(TABLE_WHOLESALE, null, v); db.close(); return id;
    }

    /** Precio mayorista vigente más reciente para una estación + combustible. */
    public WholesalePrice getLatestWholesalePrice(int stationId, String fuelType) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_WHOLESALE, null,
                COL_WS_STATION_ID + "=? AND " + COL_WS_FUEL_TYPE + "=?",
                new String[]{String.valueOf(stationId), fuelType},
                null, null, COL_ID + " DESC", "1");
        WholesalePrice wp = null;
        if (c != null && c.moveToFirst()) { wp = cursorToWholesale(c); c.close(); }
        db.close(); return wp;
    }

    /** Todos los precios mayoristas definidos por un distribuidor. */
    public List<WholesalePrice> getWholesalePricesByDistributor(int distributorId) {
        List<WholesalePrice> list = new ArrayList<>(); SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_WHOLESALE, null, COL_WS_DIST_ID + "=?",
                new String[]{String.valueOf(distributorId)}, null, null, COL_ID + " DESC");
        if (c != null) { while (c.moveToNext()) list.add(cursorToWholesale(c)); c.close(); }
        db.close(); return list;
    }

    /** Todos los precios mayoristas del sistema (para reportes y autoridad). */
    public List<WholesalePrice> getAllWholesalePrices() {
        List<WholesalePrice> list = new ArrayList<>(); SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_WHOLESALE, null, null, null, null, null, COL_ID + " DESC");
        if (c != null) { while (c.moveToNext()) list.add(cursorToWholesale(c)); c.close(); }
        db.close(); return list;
    }

    private WholesalePrice cursorToWholesale(Cursor c) {
        WholesalePrice wp = new WholesalePrice();
        wp.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
        wp.setStationId(c.getInt(c.getColumnIndexOrThrow(COL_WS_STATION_ID)));
        wp.setStationName(c.getString(c.getColumnIndexOrThrow(COL_WS_STATION_NAME)));
        wp.setFuelType(c.getString(c.getColumnIndexOrThrow(COL_WS_FUEL_TYPE)));
        wp.setPricePerGallon(c.getDouble(c.getColumnIndexOrThrow(COL_WS_PRICE)));
        wp.setEffectiveDate(c.getString(c.getColumnIndexOrThrow(COL_WS_DATE)));
        wp.setDistributorId(c.getInt(c.getColumnIndexOrThrow(COL_WS_DIST_ID)));
        return wp;
    }
}