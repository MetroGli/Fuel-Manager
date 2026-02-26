package co.edu.unipiloto.fuelmanager.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import co.edu.unipiloto.fuelmanager.data.model.User;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME    = "fuelmanager.db";
    private static final int    DATABASE_VERSION = 3;

    // ── Tabla usuarios ───────────────────────────────────────
    public static final String TABLE_USERS          = "users";
    public static final String COL_ID               = "id";
    public static final String COL_NAME             = "name";
    public static final String COL_EMAIL            = "email";
    public static final String COL_PASSWORD         = "password";
    public static final String COL_ROLE             = "role";
    public static final String COL_CREATED_AT       = "created_at";

    // ── Tabla estaciones ─────────────────────────────────────
    public static final String TABLE_STATIONS       = "stations";
    public static final String COL_ST_NAME          = "st_name";
    public static final String COL_ADDRESS          = "address";
    public static final String COL_ZONE             = "zone";
    public static final String COL_PRICE_CORRIENTE  = "price_corriente";
    public static final String COL_PRICE_EXTRA      = "price_extra";
    public static final String COL_PRICE_ACPM       = "price_acpm";

    // ── Tabla inventario ─────────────────────────────────────
    public static final String TABLE_INVENTORY      = "inventory_movements";
    public static final String COL_INV_FUEL_TYPE    = "fuel_type";
    public static final String COL_INV_MOV_TYPE     = "mov_type";
    public static final String COL_INV_VOLUME       = "volume_gal";
    public static final String COL_INV_NOTE         = "note";
    public static final String COL_INV_DATE         = "mov_date";
    public static final String COL_INV_STATION_ID   = "station_id";

    // ── CREATE statements ────────────────────────────────────
    private static final String CREATE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COL_ID         + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME       + " TEXT NOT NULL, " +
                    COL_EMAIL      + " TEXT NOT NULL UNIQUE, " +
                    COL_PASSWORD   + " TEXT NOT NULL, " +
                    COL_ROLE       + " TEXT NOT NULL DEFAULT 'CLIENTE', " +
                    COL_CREATED_AT + " TEXT NOT NULL);";

    private static final String CREATE_STATIONS =
            "CREATE TABLE " + TABLE_STATIONS + " (" +
                    COL_ID              + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_ST_NAME         + " TEXT NOT NULL, " +
                    COL_ADDRESS         + " TEXT NOT NULL, " +
                    COL_ZONE            + " TEXT NOT NULL, " +
                    COL_PRICE_CORRIENTE + " REAL NOT NULL, " +
                    COL_PRICE_EXTRA     + " REAL NOT NULL, " +
                    COL_PRICE_ACPM      + " REAL NOT NULL);";

    private static final String CREATE_INVENTORY =
            "CREATE TABLE " + TABLE_INVENTORY + " (" +
                    COL_ID              + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_INV_FUEL_TYPE   + " TEXT NOT NULL, " +
                    COL_INV_MOV_TYPE    + " TEXT NOT NULL, " +
                    COL_INV_VOLUME      + " REAL NOT NULL, " +
                    COL_INV_NOTE        + " TEXT, " +
                    COL_INV_DATE        + " TEXT NOT NULL, " +
                    COL_INV_STATION_ID  + " INTEGER NOT NULL);";

    // ── Singleton ────────────────────────────────────────────
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
        insertDefaultAdmin(db);
        insertSeedStations(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INVENTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

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

    // ── CRUD Usuarios ─────────────────────────────────────────
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
}