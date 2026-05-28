package co.edu.unipiloto.fuelmanager.stations;

import android.content.Context;

import java.util.List;

import co.edu.unipiloto.fuelmanager.data.model.Station;
import co.edu.unipiloto.fuelmanager.utils.ApiClient;

public class StationRepository {

    public StationRepository(Context context) {
    }

    public List<Station> getStationsSortedByPrice(String zone) {
        if (zone == null || zone.equals("Todas")) {
            return getAllOrderedByPrice();
        } else {
            return getByZone(zone);
        }
    }

    public List<Station> getAllOrderedByPrice() {

        List<Station> list = ApiClient.getAllStations();
        list.sort((a, b) -> Double.compare(a.getPriceCorriente(), b.getPriceCorriente()));
        return list;
    }

    public List<Station> getByZone(String zone) {
        List<Station> list = ApiClient.getStationsByZone(zone);
        list.sort((a, b) -> Double.compare(a.getPriceCorriente(), b.getPriceCorriente()));
        return list;
    }

    public boolean updatePrices(int stationId, double corriente, double extra, double acpm) {
        return ApiClient.updateStationPrices(stationId, corriente, extra, acpm);
    }
}