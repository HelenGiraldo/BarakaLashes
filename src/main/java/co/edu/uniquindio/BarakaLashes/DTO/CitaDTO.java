package co.edu.uniquindio.BarakaLashes.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaDTO {


    private String nombreCita;
    private String descripcionCita;
    private LocalDateTime fechaCita;
    private String estadoCita;
    private MultipartFile imagenReferencia;


    private Set<String> serviciosSeleccionados;

    // El cliente NO conoce su ID. Pedimos el email para identificarlo o crearlo.
    private String emailCliente;

    private Integer idEmpleado;


}