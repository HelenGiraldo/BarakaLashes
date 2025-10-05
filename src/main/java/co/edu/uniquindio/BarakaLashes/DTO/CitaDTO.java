package co.edu.uniquindio.BarakaLashes.DTO;

import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.modelo.Servicio;
import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Data
public class CitaDTO {
    private Integer idCita;
    private String nombreCita;
    private String descripcionCita;
    private LocalDateTime fechaCita;
    private EstadoCita estadoCita;
    private Set<Servicio> serviciosSeleccionados;
    private String emailCliente;
    private Usuario id_empleado;
}