package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.Cita.CitaActualizadaDTO;
import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import co.edu.uniquindio.BarakaLashes.servicio.Implementaciones.CitaServicioImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/citas")
public class CitaControlador {

    private final CitaServicioImpl citaServicio;

    // Crear cita
    @PostMapping
    public ResponseEntity<Cita> crearCita(@RequestBody CitaDTO cita) {
        Cita nuevaCita = citaServicio.crearCita(cita);
        return ResponseEntity.ok(nuevaCita);
    }

    // Listar todas las citas
    @GetMapping
    public ResponseEntity<List<CitaDTO>> listarCitas() {
        return ResponseEntity.ok(citaServicio.listarCitas());
    }

    // Obtener cita por id
    @GetMapping("/{id}")
    public ResponseEntity<Cita> obtenerCitaPorId(@PathVariable Integer id) {
        try {
            return null; //ResponseEntity.ok(citaServicio.obtenerCitaPorId(id)); ARREGLAR EN SERVICE
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Actualizar cita
    @PutMapping("/{id}")
    public ResponseEntity<CitaActualizadaDTO> actualizarCita(@PathVariable Integer id, @RequestBody Cita cita) {
        try {
            return ResponseEntity.ok(citaServicio.actualizarCita(id, cita));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar cita
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCita(@PathVariable Integer id) {
        try {
            citaServicio.eliminarCita(id);
            return ResponseEntity.ok("Cita eliminada correctamente");
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
