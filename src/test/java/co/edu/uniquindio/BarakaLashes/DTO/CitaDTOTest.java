package co.edu.uniquindio.BarakaLashes.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaDTOTest {
    private String nombreCita;
    private String descripcionCita;
    private LocalDateTime fechaCita;
    private Set<String> serviciosSeleccionados;
    private String emailCliente;
    private Integer idEmpleado;
}
