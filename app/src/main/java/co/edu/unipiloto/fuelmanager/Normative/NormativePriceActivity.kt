package co.edu.unipiloto.fuelmanager.normative

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.edu.unipiloto.fuelmanager.Normative.NormativePriceAdapter
import co.edu.unipiloto.fuelmanager.R
import co.edu.unipiloto.fuelmanager.data.repository.NormativePriceRepository

class NormativePriceActivity : AppCompatActivity() {

    private lateinit var btnActualizarPrecios: Button
    private lateinit var normativeRepo: NormativePriceRepository
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: NormativePriceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normative_price)

        // Inicializar repository
        normativeRepo = NormativePriceRepository(this)

        // Conectar vistas
        btnActualizarPrecios = findViewById(R.id.btnActualizarPrecios)
        recycler = findViewById(R.id.recyclerNormative)

        // Configurar RecyclerView
        adapter = NormativePriceAdapter()
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        // Listener botón
        btnActualizarPrecios.setOnClickListener {
            actualizarPreciosNormativos()
        }
    }

    private fun actualizarPreciosNormativos() {
        Thread {
            val ok = normativeRepo.fetchAndSaveFromJson()
            val lista = normativeRepo.all

            runOnUiThread {
                if (ok) {
                    Toast.makeText(
                        this,
                        "Precios actualizados ✓",
                        Toast.LENGTH_SHORT
                    ).show()

                    adapter.setData(lista)

                } else {
                    Toast.makeText(
                        this,
                        "Sin conexión",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }.start()
    }
}