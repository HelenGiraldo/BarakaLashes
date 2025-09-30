package co.edu.uniquindio.BarakaLashes.servicio.Implementaciones;

import co.edu.uniquindio.BarakaLashes.DTO.Cita.CitaActualizadaDTO;
import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.repositorio.CitaRepositorio;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import co.edu.uniquindio.BarakaLashes.mappers.CitaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CitaServicioImpl implements CitaServicio {

    private final CitaRepositorio repositorio;
    private final CitaMapper citaMapper;

    @Override
    public List<CitaDTO> listarCitas() {
        List<Cita> citas = repositorio.findAll();
        return citaMapper.listCitaToListCitaDTO(citas);
    }

    @Override
    public CitaDTO obtenerCitaPorId(Integer idCita) {
        Optional<Cita> citaOptional = repositorio.findById(idCita);
        if (citaOptional.isEmpty()) {
            throw new RuntimeException("Cita no encontrada con ID: " + idCita);
        }
        Cita cita = citaOptional.get();
        return citaMapper.citaToCitaDTO(cita);
    }

    @Override
    public CitaDTO crearCita(CitaDTO citaDTO) {
        Cita nuevaCita = citaMapper.citaDTOToCita(citaDTO);

        // Lógica de mapeo de Usuario y Empleado (debe implementarse aquí)

        nuevaCita.setEstadoCita(EstadoCita.PENDIENTE);

        Cita citaGuardada = repositorio.save(nuevaCita);
        return citaMapper.citaToCitaDTO(citaGuardada);
    }

    @Override
    public CitaActualizadaDTO actualizarCita(Integer id, CitaDTO citaDTO) {

        Optional<Cita> citaExistenteOptional = repositorio.findById(id);

        if (citaExistenteOptional.isEmpty()) {
            throw new RuntimeException("Cita a actualizar no encontrada con ID: " + id);
        }

        Cita citaExistente = citaExistenteOptional.get();

        if (citaDTO.getFechaCita() != null) {
            citaExistente.setFechaCita(citaDTO.getFechaCita());
        }

        // El bloque de getEstadoCita() ha sido eliminado para la compilación

        if (citaDTO.getIdEmpleado() != null) {
            // Lógica para buscar el Empleado y asignarlo
        }
        if (citaDTO.getNombreCita() != null) {
            citaExistente.setNombreCita(citaDTO.getNombreCita());
        }
        if (citaDTO.getDescripcionCita() != null) {
            citaExistente.setDescripcionCita(citaDTO.getDescripcionCita());
        }

        Cita citaActualizada = repositorio.save(citaExistente);

        return new CitaActualizadaDTO(
                citaActualizada.getIdCita(),
                citaActualizada.getUsuario() != null ? citaActualizada.getUsuario().getNombre() : null,
                citaActualizada.getListaServicios() != null ? citaActualizada.getListaServicios().toString() : null,
                citaActualizada.getFechaCita(),
                citaActualizada.getEstadoCita().name(),
                "Cita ID " + id + " actualizada correctamente mediante API."
        );
    }

    @Override
    public void eliminarCita(Integer id) {
        if (!repositorio.existsById(id)) {
            throw new RuntimeException("Cita a eliminar no encontrada.");
        }
        repositorio.deleteById(id);
    }
}