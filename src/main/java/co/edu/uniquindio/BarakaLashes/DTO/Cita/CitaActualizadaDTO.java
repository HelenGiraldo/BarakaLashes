package co.edu.uniquindio.BarakaLashes.DTO.Cita;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Data Transfer Object para la respuesta después de actualizar una cita.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaActualizadaDTO {

    private Integer id;
    private String nombreCliente;
    private String servicioRequerido;
    private LocalDateTime nuevaFechaHora;
    private String nuevoEstado;
    private String mensaje;
}
