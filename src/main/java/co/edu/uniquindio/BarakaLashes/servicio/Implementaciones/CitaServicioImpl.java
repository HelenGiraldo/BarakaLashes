package co.edu.uniquindio.BarakaLashes.servicio.Implementaciones;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import co.edu.uniquindio.BarakaLashes.modelo.Empleado;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.modelo.Usuario;
import co.edu.uniquindio.BarakaLashes.repositorio.CitaRepositorio;
import co.edu.uniquindio.BarakaLashes.repositorio.UsuarioRepositorio;
import co.edu.uniquindio.BarakaLashes.mappers.CitaMapper;
import lombok.extern.slf4j.Slf4j;

import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CitaServicioImpl implements CitaServicio {

    private final CitaRepositorio citaRepo;
    private final CitaMapper citaMapper;
    private final UsuarioRepositorio usuarioRepositorio;

    @Override
    public int crearCita(CitaDTO citaDTO) throws Exception {
        log.info("=== INTENTANDO CREAR CITA ===");
        log.info("Email cliente: {}", citaDTO.getEmailCliente());
        log.info("Nombre cita: {}", citaDTO.getNombreCita());
        log.info("Fecha cita: {}", citaDTO.getFechaCita());
        log.info("Servicios seleccionados: {}", citaDTO.getServiciosSeleccionados());

        try {
            // Validar datos básicos
            if (citaDTO.getEmailCliente() == null || citaDTO.getEmailCliente().trim().isEmpty()) {
                throw new Exception("El email del cliente es obligatorio");
            }

            if (citaDTO.getNombreCita() == null || citaDTO.getNombreCita().trim().isEmpty()) {
                throw new Exception("El motivo de la cita es obligatorio");
            }

            if (citaDTO.getFechaCita() == null) {
                throw new Exception("La fecha de la cita es obligatoria");
            }

            if (citaDTO.getServiciosSeleccionados() == null || citaDTO.getServiciosSeleccionados().isEmpty()) {
                throw new Exception("Debe seleccionar al menos un servicio");
            }


            Cita cita = citaMapper.citaDTOToCita(citaDTO);
            log.info("Cita mapeada: {}", cita);

            // Asociar el usuario a partir del email del cliente
            Usuario usuario = usuarioRepositorio.findByEmail(citaDTO.getEmailCliente())
                    .orElseThrow(() -> new Exception("No existe un usuario registrado con el email proporcionado"));
            cita.setUsuario(usuario);

            // Estado inicial por defecto
            cita.setEstadoCita(EstadoCita.PENDIENTE);


            log.info("Guardando cita en la base de datos...");
            Cita citaGuardada = citaRepo.save(cita);
            log.info("CITA CREADA EXITOSAMENTE - ID: {}", citaGuardada.getIdCita());

            return citaGuardada.getIdCita();

        } catch (Exception e) {
            log.error(" ERROR AL CREAR CITA: {}", e.getMessage());
            log.error("Stack trace:", e);
            throw e;
        }
    }

    @Override
    public int actualizarCita(int idCita, CitaDTO citaDTO) throws Exception {
        // Verificar que la cita existe
        Optional<Cita> citaOptional = citaRepo.findById(idCita);
        if (citaOptional.isEmpty()) {
            throw new Exception("Cita no encontrada con ID: " + idCita);
        }

        Cita cita = citaOptional.get();

        cita.setNombreCita(citaDTO.getNombreCita());
        cita.setDescripcionCita(citaDTO.getDescripcionCita());
        cita.setFechaCita(citaDTO.getFechaCita());
        cita.setEstadoCita(citaDTO.getEstadoCita());
        cita.setListaServicios(citaDTO.getServiciosSeleccionados());


        citaRepo.save(cita);
        return cita.getIdCita();
    }

    @Override
    public int eliminarCita(int idCita) throws Exception {

        if (!citaRepo.existsById(idCita)) {
            throw new Exception("Cita a eliminar no encontrada.");
        }

        citaRepo.deleteById(idCita);
        return idCita;
    }

    @Override
    public CitaDTO obtenerCita(int idCita) throws Exception {
        Optional<Cita> citaOptional = citaRepo.findById(idCita);
        if (citaOptional.isEmpty()) {
            throw new Exception("Cita no encontrada con ID: " + idCita);
        }

        return citaMapper.citaToCitaDTO(citaOptional.get());
    }

    @Override
    public List<CitaDTO> listarCitas() {
        List<Cita> citas = citaRepo.findAll();
        return citaMapper.citasToCitasDTO(citas);
    }

    @Override
    public List<CitaDTO> listarCitasPorUsuario(int idUsuario) {
        // Busca directamente en la BD por el ID del usuario
        List<Cita> citas = citaRepo.findByUsuarioIdUsuario(idUsuario);
        return citaMapper.citasToCitasDTO(citas);
    }

    @Override
    public List<CitaDTO> listarCitasPorEmail(String emailUsuario) {
        List<Cita> citas = citaRepo.findByUsuarioEmail(emailUsuario);
        return citaMapper.citasToCitasDTO(citas);
    }

    @Override
    public int cancelarCita(int idCita) throws Exception {
        Cita cita = citaRepo.findCancelableCita(idCita)
                .orElseThrow(() -> new Exception("La cita no existe o no se puede cancelar en su estado actual"));

        // Regla: solo cancelar con 24h de anticipación
        if (!java.time.LocalDateTime.now().isBefore(cita.getFechaCita().minusHours(24))) {
            throw new Exception("Solo puedes cancelar con 24 horas de anticipación");
        }

        citaRepo.actualizarEstadoCita(idCita, EstadoCita.CANCELADA);
        return idCita;
    }
}