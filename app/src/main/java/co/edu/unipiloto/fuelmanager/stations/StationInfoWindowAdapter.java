package co.edu.unipiloto.fuelmanager.stations;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.text.NumberFormat;
import java.util.Locale;

import co.edu.unipiloto.fuelmanager.R;
import co.edu.unipiloto.fuelmanager.data.model.Station;


public class StationInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final Context        context;
    private final LayoutInflater inflater;

    private static final NumberFormat COP =
            NumberFormat.getInstance(new Locale("es", "CO"));

    public StationInfoWindowAdapter(Context context) {
        this.context  = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return buildView(marker);
    }


    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }


    private View buildView(Marker marker) {
        View view = inflater.inflate(R.layout.item_map_info_window, null);

        TextView tvName      = view.findViewById(R.id.tvInfoName);
        TextView tvZone      = view.findViewById(R.id.tvInfoZone);
        TextView tvCorriente = view.findViewById(R.id.tvInfoCorriente);
        TextView tvExtra     = view.findViewById(R.id.tvInfoExtra);
        TextView tvAcpm      = view.findViewById(R.id.tvInfoAcpm);

        Object tag = marker.getTag();
        if (tag instanceof Station) {
            Station s = (Station) tag;
            tvName.setText(s.getName());
            tvZone.setText(s.getZone());
            tvCorriente.setText("$" + COP.format(s.getPriceCorriente()) + "/gal");
            tvExtra.setText("$"     + COP.format(s.getPriceExtra())     + "/gal");
            tvAcpm.setText("$"      + COP.format(s.getPriceAcpm())      + "/gal");
        } else {
            tvName.setText(marker.getTitle() != null ? marker.getTitle() : "Estación");
            tvZone.setText("");
            tvCorriente.setText("—");
            tvExtra.setText("—");
            tvAcpm.setText("—");
        }

        return view;
    }
}