package co.edu.unipiloto.fuelmanager.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import co.edu.unipiloto.fuelmanager.data.local.PasswordUtil;
import co.edu.unipiloto.fuelmanager.data.model.Delivery;
import co.edu.unipiloto.fuelmanager.data.model.FuelSale;
import co.edu.unipiloto.fuelmanager.data.model.NormativePrice;
import co.edu.unipiloto.fuelmanager.data.model.Receipt;
import co.edu.unipiloto.fuelmanager.data.model.Station;
import co.edu.unipiloto.fuelmanager.data.model.Subsidy;
import co.edu.unipiloto.fuelmanager.data.model.User;
import co.edu.unipiloto.fuelmanager.data.model.WholesalePrice;
import co.edu.unipiloto.fuelmanager.data.model.InventoryMovement;
import co.edu.unipiloto.fuelmanager.data.model.InventoryStock;
import co.edu.unipiloto.fuelmanager.data.model.PriceUpdate;
import co.edu.unipiloto.fuelmanager.data.model.PriceAlert;


public class ApiClient {

    public static final String BASE_URL = "http://10.0.2.2:8080/api";


    private static String get(String endpoint) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        return readResponse(conn);
    }

    private static String post(String endpoint, String jsonBody) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes("UTF-8"));
        }
        return readResponse(conn);
    }

    private static String put(String endpoint, String jsonBody) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes("UTF-8"));
        }
        return readResponse(conn);
    }

    private static void delete(String endpoint) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        conn.setConnectTimeout(5000);
        conn.getResponseCode();
    }

    private static String readResponse(HttpURLConnection conn) throws Exception {
        int code = conn.getResponseCode();
        BufferedReader br = new BufferedReader(new InputStreamReader(
                code >= 400 ? conn.getErrorStream() : conn.getInputStream(), "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) sb.append(line);
        br.close();
        return sb.toString();
    }

    // ═══════════════════════════════════════════════════════
    //  USUARIOS
    // ═══════════════════════════════════════════════════════

    /** Equivale a db.loginUser(email, password) */
    public static User loginUser(String email, String password) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", PasswordUtil.hash(password));
            String resp = post("/users/login", body.toString());
            JSONObject obj = new JSONObject(resp);
            if (obj.has("id")) return jsonToUser(obj);
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public static long insertUser(User user) {
        try {
            String resp = post("/users", userToJson(user).toString());
            JSONObject obj = new JSONObject(resp);
            return obj.optInt("id", -1);
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    /** Equivale a db.insertUserWithStation(user, station) */
    public static long insertUserWithStation(User user, Station station) {
        long userId = insertUser(user);
        if (userId > 0) {
            station.setId(0); // nuevo
            insertStation(station);
        }
        return userId;
    }

    /** Equivale a db.getUsersByRole(role) */
    public static List<User> getUsersByRole(String role) {
        List<User> list = new ArrayList<>();
        try {
            String resp = get("/users");
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (role.equals(obj.optString("role")))
                    list.add(jsonToUser(obj));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Equivale a db.emailExists(email) */
    public static boolean emailExists(String email) {
        List<User> users = getUsersByRole("CLIENTE");
        try {
            String resp = get("/users");
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++) {
                if (email.equalsIgnoreCase(arr.getJSONObject(i).optString("email")))
                    return true;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Actualizar usuario */
    public static boolean updateUser(User user) {
        try {
            put("/users/" + user.getId(), userToJson(user).toString());
            return true;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Eliminar usuario */
    public static boolean deleteUser(int userId) {
        try { delete("/users/" + userId); return true; }
        catch (Exception e) { e.printStackTrace(); }
        return false;
    }
    /**
     *
     * Obtiene el usuario actual, le cambia el nombre y hace PUT.
     */
    public static boolean updateUserName(int userId, String newName) {
        try {
            String resp = get("/users/" + userId);
            org.json.JSONObject obj = new org.json.JSONObject(resp);
            if (!obj.has("id")) return false;

            obj.put("name", newName.trim());
            put("/users/" + userId, obj.toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ═══════════════════════════════════════════════════════
    //  ESTACIONES
    // ═══════════════════════════════════════════════════════

    /** Equivale a db.getAllStations() */
    public static List<Station> getAllStations() {
        List<Station> list = new ArrayList<>();
        try {
            String resp = get("/stations");
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++)
                list.add(jsonToStation(arr.getJSONObject(i)));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public static List<Station> getAllStationsSimple() { return getAllStations(); }

    /** Equivale a db.getStationsByZone(zone) */
    public static List<Station> getStationsByZone(String zone) {
        List<Station> all = getAllStations();
        List<Station> filtered = new ArrayList<>();
        for (Station s : all) if (zone.equals(s.getZone())) filtered.add(s);
        return filtered;
    }

    /** Equivale a db.getStationById(id) */
    public static Station getStationById(int id) {
        try {
            String resp = get("/stations/" + id);
            return jsonToStation(new JSONObject(resp));
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** Equivale a db.getStationIdByUserId(userId) — busca localmente */
    public static int getStationIdByUserId(int userId) {
        // El backend no tiene este endpoint; se resuelve buscando en la lista
        // En tu registro de estación, guardas el userId como distributorId o notes
        // Por ahora retorna userId como fallback (compatible con lógica existente)
        return userId;
    }

    /** Equivale a db.updateStationPrices(stationId, corriente, extra, acpm) */
    public static boolean updateStationPrices(int stationId, double corriente, double extra, double acpm) {
        try {
            Station st = getStationById(stationId);
            if (st == null) return false;
            st.setPriceCorriente(corriente);
            st.setPriceExtra(extra);
            st.setPriceAcpm(acpm);
            put("/stations/" + stationId, stationToJson(st).toString());
            return true;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Insertar nueva estación */
    public static long insertStation(Station station) {
        try {
            String resp = post("/stations", stationToJson(station).toString());
            return new JSONObject(resp).optInt("id", -1);
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    /** Eliminar estación */
    public static boolean deleteStation(int stationId) {
        try { delete("/stations/" + stationId); return true; }
        catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // ═══════════════════════════════════════════════════════
    //  VENTAS
    // ═══════════════════════════════════════════════════════

    /** Equivale a db.insertSale(sale) */
    public static long insertSale(FuelSale sale) {
        try {
            String resp = post("/sales", saleToJson(sale).toString());
            return new JSONObject(resp).optInt("id", -1);
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    /** Equivale a db.getSales(stationId) */
    public static List<FuelSale> getSales(int stationId) {
        List<FuelSale> list = new ArrayList<>();
        try {
            String resp = get("/sales/station/" + stationId);
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++)
                list.add(jsonToSale(arr.getJSONObject(i)));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Equivale a db.getAllSales() */
    public static List<FuelSale> getAllSales() {
        List<FuelSale> list = new ArrayList<>();
        try {
            String resp = get("/sales");
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++)
                list.add(jsonToSale(arr.getJSONObject(i)));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Eliminar venta */
    public static boolean deleteSale(int saleId) {
        try { delete("/sales/" + saleId); return true; }
        catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // ═══════════════════════════════════════════════════════
    //  RECIBOS
    // ═══════════════════════════════════════════════════════

    /** Equivale a db.insertReceipt(...) */
    public static long insertReceipt(long saleId, String fuelType, double volume,
                                     double pricePerGal, double total,
                                     String plate, String date, int stationId) {
        try {
            JSONObject body = new JSONObject();
            body.put("saleId", saleId);
            body.put("fuelType", fuelType);
            body.put("volumeGal", volume);
            body.put("pricePerGal", pricePerGal);
            body.put("total", total);
            body.put("clientPlate", plate != null ? plate : "");
            body.put("date", date);
            body.put("stationId", stationId);
            String resp = post("/receipts", body.toString());
            return new JSONObject(resp).optInt("id", -1);
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    /** Equivale a db.getReceiptsByStation(stationId) */
    public static List<Receipt> getReceiptsByStation(int stationId) {
        List<Receipt> list = new ArrayList<>();
        try {
            String resp = get("/receipts/station/" + stationId);
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++)
                list.add(jsonToReceipt(arr.getJSONObject(i)));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Equivale a db.getAllReceipts() */
    public static List<Receipt> getAllReceipts() {
        List<Receipt> list = new ArrayList<>();
        try {
            String resp = get("/receipts");
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++)
                list.add(jsonToReceipt(arr.getJSONObject(i)));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Equivale a db.getReceiptBySaleId(saleId) */
    public static Receipt getReceiptBySaleId(long saleId) {
        try {
            List<Receipt> all = getAllReceipts();
            for (Receipt r : all) if (r.getSaleId() == saleId) return r;
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ═══════════════════════════════════════════════════════
    //  ENTREGAS DISTRIBUIDOR
    // ═══════════════════════════════════════════════════════

    /** Equivale a db.insertDelivery(delivery) */
    public static long insertDelivery(Delivery delivery) {
        try {
            String resp = post("/deliveries", deliveryToJson(delivery).toString());
            return new JSONObject(resp).optInt("id", -1);
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    /** Equivale a db.getDeliveries(distributorId) */
    public static List<Delivery> getDeliveries(int distributorId) {
        List<Delivery> list = new ArrayList<>();
        try {
            String resp = get("/deliveries/distributor/" + distributorId);
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++)
                list.add(jsonToDelivery(arr.getJSONObject(i)));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Equivale a db.getAllDeliveries() */
    public static List<Delivery> getAllDeliveries() {
        List<Delivery> list = new ArrayList<>();
        try {
            String resp = get("/deliveries");
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++)
                list.add(jsonToDelivery(arr.getJSONObject(i)));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ═══════════════════════════════════════════════════════
    //  PRECIOS NORMATIVOS
    // ═══════════════════════════════════════════════════════

    /** Equivale a db.insertNormativePrice(price) */
    public static long insertNormativePrice(NormativePrice price) {
        try {
            JSONObject body = new JSONObject();
            body.put("fuelType", price.getFuelType());
            body.put("pricePerGallon", price.getPricePerGallon());
            body.put("effectiveDate", price.getEffectiveDate());
            body.put("source", price.getSource() != null ? price.getSource() : "");
            String resp = post("/normative-prices", body.toString());
            return new JSONObject(resp).optInt("id", -1);
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    /** Equivale a db.getNormativePrices() */
    public static List<NormativePrice> getNormativePrices() {
        List<NormativePrice> list = new ArrayList<>();
        try {
            String resp = get("/normative-prices");
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                NormativePrice p = new NormativePrice();
                p.setId(o.optInt("id"));
                p.setFuelType(o.optString("fuelType"));
                p.setPricePerGallon(o.optDouble("pricePerGallon"));
                p.setEffectiveDate(o.optString("effectiveDate"));
                p.setSource(o.optString("source"));
                list.add(p);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Equivale a db.clearNormativePrices() — borra uno por uno */
    public static void clearNormativePrices() {
        try {
            String resp = get("/normative-prices");
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++)
                delete("/normative-prices/" + arr.getJSONObject(i).optInt("id"));
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ═══════════════════════════════════════════════════════
    //  SUBSIDIOS
    // ═══════════════════════════════════════════════════════

    /** Equivale a db.insertSubsidy(subsidy) */
    public static long insertSubsidy(Subsidy s) {
        try {
            String resp = post("/subsidies", subsidyToJson(s).toString());
            return new JSONObject(resp).optInt("id", -1);
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    /** Equivale a db.getAllSubsidies() */
    public static List<Subsidy> getAllSubsidies() {
        List<Subsidy> list = new ArrayList<>();
        try {
            String resp = get("/subsidies");
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++)
                list.add(jsonToSubsidy(arr.getJSONObject(i)));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Equivale a db.deactivateSubsidy(id) */
    public static void deactivateSubsidy(int subsidyId) {
        try {
            Subsidy s = jsonToSubsidy(new JSONObject(get("/subsidies/" + subsidyId)));
            s.setActive(false);
            put("/subsidies/" + subsidyId, subsidyToJson(s).toString());
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** Equivale a db.getActiveSubsidyForUser(targetValue, fuelType) */
    public static Subsidy getActiveSubsidyForUser(String targetValue, String fuelType) {
        try {
            List<Subsidy> all = getAllSubsidies();
            for (Subsidy s : all) {
                if (s.isActive() && targetValue.equals(s.getTargetValue()) &&
                        (fuelType.equals(s.getFuelType()) || "TODOS".equals(s.getFuelType())))
                    return s;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** Equivale a db.getActiveSubsidyByZone(zone, fuelType) */
    public static Subsidy getActiveSubsidyByZone(String zone, String fuelType) {
        try {
            List<Subsidy> all = getAllSubsidies();
            for (Subsidy s : all) {
                if (s.isActive() && "REGION".equals(s.getTargetType()) &&
                        zone.equals(s.getTargetValue()) &&
                        (fuelType.equals(s.getFuelType()) || "TODOS".equals(s.getFuelType())))
                    return s;
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ═══════════════════════════════════════════════════════
    //  PRECIOS MAYORISTAS
    // ═══════════════════════════════════════════════════════

    /** Equivale a db.insertWholesalePrice(wp) */
    public static long insertWholesalePrice(WholesalePrice wp) {
        try {
            String resp = post("/wholesale-prices", wholesaleToJson(wp).toString());
            return new JSONObject(resp).optInt("id", -1);
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    /** Equivale a db.getLatestWholesalePrice(stationId, fuelType) */
    public static WholesalePrice getLatestWholesalePrice(int stationId, String fuelType) {
        try {
            String resp = get("/wholesale-prices");
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                if (o.optInt("stationId") == stationId && fuelType.equals(o.optString("fuelType")))
                    return jsonToWholesale(o);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    /** Equivale a db.getWholesalePricesByDistributor(distributorId) */
    public static List<WholesalePrice> getWholesalePricesByDistributor(int distributorId) {
        List<WholesalePrice> list = new ArrayList<>();
        try {
            String resp = get("/wholesale-prices");
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                if (o.optInt("distributorId") == distributorId)
                    list.add(jsonToWholesale(o));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Equivale a db.getAllWholesalePrices() */
    public static List<WholesalePrice> getAllWholesalePrices() {
        List<WholesalePrice> list = new ArrayList<>();
        try {
            String resp = get("/wholesale-prices");
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++)
                list.add(jsonToWholesale(arr.getJSONObject(i)));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ═══════════════════════════════════════════════════════
    //  CONVERSORES JSON ↔ MODELO
    // ═══════════════════════════════════════════════════════

    private static User jsonToUser(JSONObject o) {
        User u = new User(
                o.optInt("id"),
                o.optString("name"),
                o.optString("email"),
                o.optString("password"),
                o.optString("role"));
        u.setVehicleType(o.optString("vehicleType"));
        return u;
    }

    private static JSONObject userToJson(User u) throws Exception {
        JSONObject o = new JSONObject();
        o.put("name", u.getName());
        o.put("email", u.getEmail());
        o.put("password", PasswordUtil.hash(u.getPassword()));
        o.put("role", u.getRole() != null ? u.getRole() : "CLIENTE");
        o.put("vehicleType", u.getVehicleType() != null ? u.getVehicleType() : "");
        return o;
    }

    private static Station jsonToStation(JSONObject o) {
        Station s = new Station();
        s.setId(o.optInt("id"));
        s.setName(o.optString("name"));
        s.setAddress(o.optString("address"));
        s.setZone(o.optString("zone"));
        s.setPriceCorriente(o.optDouble("priceCorriente"));
        s.setPriceExtra(o.optDouble("priceExtra"));
        s.setPriceAcpm(o.optDouble("priceAcpm"));
        return s;
    }

    private static JSONObject stationToJson(Station s) throws Exception {
        JSONObject o = new JSONObject();
        o.put("name", s.getName());
        o.put("address", s.getAddress());
        o.put("zone", s.getZone());
        o.put("priceCorriente", s.getPriceCorriente());
        o.put("priceExtra", s.getPriceExtra());
        o.put("priceAcpm", s.getPriceAcpm());
        return o;
    }

    private static FuelSale jsonToSale(JSONObject o) {
        FuelSale s = new FuelSale();
        s.setId(o.optInt("id"));
        s.setFuelType(o.optString("fuelType"));
        s.setVolumeGal(o.optDouble("volumeGal"));
        s.setPricePerGal(o.optDouble("pricePerGal"));
        s.setTotalPrice(o.optDouble("totalPrice"));
        s.setClientPlate(o.optString("clientPlate"));
        s.setDate(o.optString("date"));
        s.setStationId(o.optInt("stationId"));
        return s;
    }

    private static JSONObject saleToJson(FuelSale s) throws Exception {
        JSONObject o = new JSONObject();
        o.put("fuelType", s.getFuelType());
        o.put("volumeGal", s.getVolumeGal());
        o.put("pricePerGal", s.getPricePerGal());
        o.put("totalPrice", s.getTotalPrice());
        o.put("clientPlate", s.getClientPlate() != null ? s.getClientPlate() : "");
        o.put("date", s.getDate());
        o.put("stationId", s.getStationId());
        return o;
    }

    private static Receipt jsonToReceipt(JSONObject o) {
        Receipt r = new Receipt();
        r.setId(o.optInt("id"));
        r.setSaleId(o.optLong("saleId"));
        r.setFuelType(o.optString("fuelType"));
        r.setVolumeGal(o.optDouble("volumeGal"));
        r.setPricePerGal(o.optDouble("pricePerGal"));
        r.setTotal(o.optDouble("total"));
        r.setClientPlate(o.optString("clientPlate"));
        r.setDate(o.optString("date"));
        r.setStationId(o.optInt("stationId"));
        return r;
    }

    private static Delivery jsonToDelivery(JSONObject o) {
        Delivery d = new Delivery();
        d.setId(o.optInt("id"));
        d.setStationId(o.optInt("stationId"));
        d.setStationName(o.optString("stationName"));
        d.setFuelType(o.optString("fuelType"));
        d.setVolumeGal(o.optDouble("volumeGal"));
        d.setDate(o.optString("date"));
        d.setNotes(o.optString("notes"));
        d.setDistributorId(o.optInt("distributorId"));
        return d;
    }

    private static JSONObject deliveryToJson(Delivery d) throws Exception {
        JSONObject o = new JSONObject();
        o.put("stationId", d.getStationId());
        o.put("stationName", d.getStationName());
        o.put("fuelType", d.getFuelType());
        o.put("volumeGal", d.getVolumeGal());
        o.put("date", d.getDate());
        o.put("notes", d.getNotes() != null ? d.getNotes() : "");
        o.put("distributorId", d.getDistributorId());
        return o;
    }

    private static Subsidy jsonToSubsidy(JSONObject o) {
        Subsidy s = new Subsidy();
        s.setId(o.optInt("id"));
        s.setTargetType(o.optString("targetType"));
        s.setTargetValue(o.optString("targetValue"));
        s.setFuelType(o.optString("fuelType"));
        s.setDiscountPct(o.optDouble("discountPct"));
        s.setStartDate(o.optString("startDate"));
        s.setEndDate(o.optString("endDate"));
        s.setNotes(o.optString("notes"));
        s.setActive(o.optBoolean("active"));
        s.setAuthorityId(o.optInt("authorityId"));
        return s;
    }

    private static JSONObject subsidyToJson(Subsidy s) throws Exception {
        JSONObject o = new JSONObject();
        o.put("targetType", s.getTargetType());
        o.put("targetValue", s.getTargetValue());
        o.put("fuelType", s.getFuelType());
        o.put("discountPct", s.getDiscountPct());
        o.put("startDate", s.getStartDate());
        o.put("endDate", s.getEndDate());
        o.put("notes", s.getNotes() != null ? s.getNotes() : "");
        o.put("active", s.isActive());
        o.put("authorityId", s.getAuthorityId());
        return o;
    }

    private static WholesalePrice jsonToWholesale(JSONObject o) {
        WholesalePrice wp = new WholesalePrice();
        wp.setId(o.optInt("id"));
        wp.setStationId(o.optInt("stationId"));
        wp.setStationName(o.optString("stationName"));
        wp.setFuelType(o.optString("fuelType"));
        wp.setPricePerGallon(o.optDouble("pricePerGallon"));
        wp.setEffectiveDate(o.optString("effectiveDate"));
        wp.setDistributorId(o.optInt("distributorId"));
        return wp;
    }

    /** Expone post() como público para uso desde InventoryRepository */
    public static String postRaw(String endpoint, String jsonBody) throws Exception {
        return post(endpoint, jsonBody);
    }

    /** Equivale a db.getMovementsByStation(stationId) */
    public static List<InventoryMovement> getInventoryMovements(int stationId) {
        List<InventoryMovement> list = new ArrayList<>();
        try {
            String resp = get("/inventory/station/" + stationId);
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                InventoryMovement m = new InventoryMovement();
                m.setId(o.optInt("id"));
                m.setFuelType(o.optString("fuelType"));
                m.setMovType(o.optString("movType"));
                m.setVolumeGal(o.optDouble("volumeGal"));
                m.setNote(o.optString("note"));
                m.setDate(o.optString("date"));
                m.setStationId(o.optInt("stationId"));
                list.add(m);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Equivale a db.getStockByStation(stationId) */
    public static InventoryStock getInventoryStock(int stationId) {
        try {
            String resp = get("/inventory/stock/" + stationId);
            JSONObject o = new JSONObject(resp);
            return new InventoryStock(
                    o.optDouble("Corriente"),
                    o.optDouble("Extra"),
                    o.optDouble("ACPM")
            );
        } catch (Exception e) { e.printStackTrace(); }
        return new InventoryStock(0, 0, 0);
    }


    /** Equivale a db.insertPriceUpdate(pu) */
    public static long insertPriceUpdate(PriceUpdate pu) {
        try {
            JSONObject o = new JSONObject();
            o.put("stationId",     pu.getStationId());
            o.put("stationName",   pu.getStationName());
            o.put("oldCorriente",  pu.getOldCorriente());
            o.put("newCorriente",  pu.getNewCorriente());
            o.put("oldExtra",      pu.getOldExtra());
            o.put("newExtra",      pu.getNewExtra());
            o.put("oldAcpm",       pu.getOldAcpm());
            o.put("newAcpm",       pu.getNewAcpm());
            o.put("date",          pu.getDate());
            o.put("distributorId", pu.getDistributorId());
            String resp = post("/price-updates", o.toString());
            return new JSONObject(resp).optInt("id", -1);
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    /** Equivale a db.getAllPriceUpdates() */
    public static List<PriceUpdate> getAllPriceUpdates() {
        List<PriceUpdate> list = new ArrayList<>();
        try {
            String resp = get("/price-updates");
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                PriceUpdate pu = new PriceUpdate();
                pu.setId(o.optInt("id"));
                pu.setStationId(o.optInt("stationId"));
                pu.setStationName(o.optString("stationName"));
                pu.setOldCorriente(o.optDouble("oldCorriente"));
                pu.setNewCorriente(o.optDouble("newCorriente"));
                pu.setOldExtra(o.optDouble("oldExtra"));
                pu.setNewExtra(o.optDouble("newExtra"));
                pu.setOldAcpm(o.optDouble("oldAcpm"));
                pu.setNewAcpm(o.optDouble("newAcpm"));
                pu.setDate(o.optString("date"));
                pu.setDistributorId(o.optInt("distributorId"));
                list.add(pu);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    private static JSONObject wholesaleToJson(WholesalePrice wp) throws Exception {
        JSONObject o = new JSONObject();
        o.put("stationId", wp.getStationId());
        o.put("stationName", wp.getStationName());
        o.put("fuelType", wp.getFuelType());
        o.put("pricePerGallon", wp.getPricePerGallon());
        o.put("effectiveDate", wp.getEffectiveDate());
        o.put("distributorId", wp.getDistributorId());
        return o;
    }
    /** Equivale a db.getActiveAlerts(userId) */
    public static List<PriceAlert> getActiveAlerts(int userId) {
        List<PriceAlert> list = new ArrayList<>();
        try {
            String resp = get("/alerts/user/" + userId);
            JSONArray arr = new JSONArray(resp);
            for (int i = 0; i < arr.length(); i++)
                list.add(jsonToAlert(arr.getJSONObject(i)));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /** Equivale a db.isAlertActive(stationId, fuelType, userId) */
    public static boolean isAlertActive(int stationId, String fuelType, int userId) {
        try {
            String resp = get("/alerts/check?stationId=" + stationId
                    + "&fuelType=" + fuelType + "&userId=" + userId);
            return Boolean.parseBoolean(resp.trim());
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    /** Equivale a db.upsertAlert(alert) */
    public static long upsertAlert(PriceAlert alert) {
        try {
            JSONObject body = new JSONObject();
            body.put("stationId",      alert.getStationId());
            body.put("stationName",    alert.getStationName());
            body.put("fuelType",       alert.getFuelType());
            body.put("lastKnownPrice", alert.getLastKnownPrice());
            body.put("active",         alert.isActive());
            body.put("userId",         alert.getUserId());
            String resp = post("/alerts/upsert", body.toString());
            return new JSONObject(resp).optInt("id", -1);
        } catch (Exception e) { e.printStackTrace(); }
        return -1;
    }

    /** Equivale a db.deactivateAlert(stationId, fuelType, userId) */
    public static void deactivateAlert(int stationId, String fuelType, int userId) {
        try {
            put("/alerts/deactivate?stationId=" + stationId
                    + "&fuelType=" + fuelType + "&userId=" + userId, "{}");
        } catch (Exception e) { e.printStackTrace(); }
    }

    // Conversor privado
    private static PriceAlert jsonToAlert(JSONObject o) {
        PriceAlert a = new PriceAlert();
        a.setId(o.optInt("id"));
        a.setStationId(o.optInt("stationId"));
        a.setStationName(o.optString("stationName"));
        a.setFuelType(o.optString("fuelType"));
        a.setLastKnownPrice(o.optDouble("lastKnownPrice"));
        a.setActive(o.optBoolean("active"));
        a.setUserId(o.optInt("userId"));
        return a;
    }

}
