package co.edu.uniquindio.BarakaLashes.DTO;

import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.modelo.Servicio;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class CitaDTO {
    private Integer idCita;
    private String nombreCita;
    private String descripcionCita;
    private LocalDateTime fechaCita;
    private EstadoCita estadoCita;
    private Set<Servicio> listaServicios;
    private String emailCliente;
    private Integer idEmpleado;
}