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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public List<CitaDTO> listarCitasPorUsuario(String emailUsuario) {
        List<Cita> citas = repositorio.findByUsuarioEmail(emailUsuario);
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
    public CitaDTO obtenerCitaPorIdYUsuario(Integer idCita, String emailUsuario) {
        Optional<Cita> citaOptional = repositorio.findByIdCitaAndUsuarioEmail(idCita, emailUsuario);
        if (citaOptional.isEmpty()) {
            throw new RuntimeException("Cita no encontrada o no pertenece al usuario");
        }
        Cita cita = citaOptional.get();
        return citaMapper.citaToCitaDTO(cita);
    }


    @Override
    public CitaDTO crearCita(CitaDTO citaDTO) {
        // Validaciones de fecha antes de crear
        validarFechaCita(citaDTO.getFechaCita());

        Cita nuevaCita = citaMapper.citaDTOToCita(citaDTO);
        nuevaCita.setEstadoCita(EstadoCita.PENDIENTE);

        Cita citaGuardada = repositorio.save(nuevaCita);
        return citaMapper.citaToCitaDTO(citaGuardada);
    }
    @Override
    @Transactional
    public void cancelarCita(Integer idCita, String emailUsuario) {
        // Buscar cita verificando que pertenece al usuario
        Optional<Cita> citaOptional = repositorio.findByIdCitaAndUsuarioEmail(idCita, emailUsuario);
        if (citaOptional.isEmpty()) {
            throw new RuntimeException("Cita no encontrada o no pertenece al usuario");
        }

        Cita cita = citaOptional.get();

        // Validar que la cita se puede cancelar
        if (cita.getEstadoCita() == EstadoCita.CANCELADA) {
            throw new RuntimeException("La cita ya está cancelada");
        }

        if (cita.getEstadoCita() == EstadoCita.COMPLETADA) {
            throw new RuntimeException("No se puede cancelar una cita completada");
        }

        // Validar tiempo mínimo de cancelación (2 horas antes)
        validarTiempoCancelacion(cita.getFechaCita());

        // Cancelar cita
        cita.setEstadoCita(EstadoCita.CANCELADA);
        repositorio.save(cita);
    }

    // MÉTODOS DE VALIDACIÓN
    private void validarFechaCita(LocalDateTime fechaCita) {
        if (fechaCita.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("No se pueden agendar citas en fechas pasadas");
        }

        if (fechaCita.isBefore(LocalDateTime.now().plusHours(24))) {
            throw new RuntimeException("Las citas deben agendarse con al menos 24 horas de anticipación");
        }
    }

    private void validarTiempoCancelacion(LocalDateTime fechaCita) {
        if (fechaCita.isBefore(LocalDateTime.now().plusHours(24))) {
            throw new RuntimeException("Solo se pueden cancelar citas con al menos 24 horas de anticipación");
        }
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