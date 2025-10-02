package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.Cita.CitaActualizadaDTO;
import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controlador REST para la gestión de citas.
 * Usa /api/citas para evitar conflictos con el controlador de vistas web.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/citas")
public class CitaAPIController {

    private final CitaServicio citaServicio;

    @PostMapping
    public ResponseEntity<CitaDTO> crearCita(@RequestBody CitaDTO citaDTO) {
        // El servicio devuelve CitaDTO, no la entidad Cita
        CitaDTO nuevaCita = citaServicio.crearCita(citaDTO);
        return ResponseEntity.ok(nuevaCita);
    }

    @GetMapping
    public ResponseEntity<List<CitaDTO>> listarCitas() {
        return ResponseEntity.ok(citaServicio.listarCitas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaDTO> obtenerCitaPorId(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(citaServicio.obtenerCitaPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<CitaActualizadaDTO> actualizarCita(@PathVariable Integer id, @RequestBody CitaDTO citaDTO) {
        try {

            return ResponseEntity.ok(citaServicio.actualizarCita(id, citaDTO));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Eliminar cita
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCita(@PathVariable Integer id) {
        try {
            citaServicio.eliminarCita(id);
            return ResponseEntity.ok("Cita eliminada correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
