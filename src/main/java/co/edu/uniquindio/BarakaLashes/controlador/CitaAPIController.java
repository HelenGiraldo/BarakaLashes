package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.Cita.CitaActualizadaDTO;
import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;

import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
@RequiredArgsConstructor
public class CitaAPIController {

    private final CitaServicio citaServicio;

    @PostMapping
    public ResponseEntity<Integer> crearCita(@RequestBody CitaDTO citaDTO) {
        try {
            int idCita = citaServicio.crearCita(citaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(idCita);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1);
        }
    }

    @GetMapping("/{idCita}")
    public ResponseEntity<CitaDTO> obtenerCita(@PathVariable int idCita) {
        try {
            CitaDTO cita = citaServicio.obtenerCita(idCita);
            return ResponseEntity.ok(cita);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{idCita}")
    public ResponseEntity<Integer> actualizarCita(@PathVariable int idCita, @RequestBody CitaDTO citaDTO) {
        try {
            int idActualizado = citaServicio.actualizarCita(idCita, citaDTO);
            return ResponseEntity.ok(idActualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(-1);
        }
    }

    @PatchMapping("/{idCita}")
    public ResponseEntity<?> modificarCita(
            @PathVariable int idCita,
            @RequestBody CitaActualizadaDTO citaActualizadaDTO) {

        try {
            // Llamamos al servicio para actualizar la cita
            int idActualizada = citaServicio.modificarCita(idCita, citaActualizadaDTO);

            // Respondemos con el ID actualizado y un mensaje
            return ResponseEntity.ok("Cita actualizada correctamente con ID: " + idActualizada);

        } catch (Exception e) {
            // Si algo falla, devolvemos un 400 con el mensaje de error
            return ResponseEntity
                    .badRequest()
                    .body("Error al actualizar la cita: " + e.getMessage());
        }
    }


    @DeleteMapping("/{idCita}")
    public ResponseEntity<Integer> eliminarCita(@PathVariable int idCita) {
        try {
            int idEliminado = citaServicio.eliminarCita(idCita);
            return ResponseEntity.ok(idEliminado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(-1);
        }
    }

    @GetMapping
    public ResponseEntity<List<CitaDTO>> listarCitas() {
        try {
            List<CitaDTO> citas = citaServicio.listarCitas();
            return ResponseEntity.ok(citas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
