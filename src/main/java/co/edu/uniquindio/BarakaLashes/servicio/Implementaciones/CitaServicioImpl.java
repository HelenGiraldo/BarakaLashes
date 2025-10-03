package co.edu.uniquindio.BarakaLashes.servicio.Implementaciones;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.repositorio.CitaRepositorio;
import co.edu.uniquindio.BarakaLashes.mappers.CitaMapper;

import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CitaServicioImpl implements CitaServicio {

    private final CitaRepositorio citaRepo;
    private final CitaMapper citaMapper;

    @Override
    public int crearCita(CitaDTO citaDTO) throws Exception {
        // Validar datos básicos
        if (citaDTO.getNombreCita() == null || citaDTO.getNombreCita().trim().isEmpty()) {
            throw new Exception("El nombre de la cita es obligatorio");
        }

        if (citaDTO.getFechaCita() == null) {
            throw new Exception("La fecha de la cita es obligatoria");
        }

        // Convertir DTO a entidad
        Cita cita = citaMapper.citaDTOToCita(citaDTO);

        // Asignar estado por defecto si no viene
        if (cita.getEstadoCita() == null) {
            cita.setEstadoCita(EstadoCita.PENDIENTE);
        }

        // Guardar la cita
        Cita citaGuardada = citaRepo.save(cita);
        return citaGuardada.getIdCita();
    }

    @Override
    public int actualizarCita(int idCita, CitaDTO citaDTO) throws Exception {
        // Verificar que la cita existe
        Optional<Cita> citaOptional = citaRepo.findById(idCita);
        if (citaOptional.isEmpty()) {
            throw new Exception("Cita no encontrada con ID: " + idCita);
        }

        Cita cita = citaOptional.get();

        // Actualizar campos
        cita.setNombreCita(citaDTO.getNombreCita());
        cita.setDescripcionCita(citaDTO.getDescripcionCita());
        cita.setFechaCita(citaDTO.getFechaCita());
        cita.setEstadoCita(citaDTO.getEstadoCita());
        cita.setListaServicios(citaDTO.getListaServicios());

        // Guardar cambios
        citaRepo.save(cita);
        return cita.getIdCita();
    }

    @Override
    public int eliminarCita(int idCita) throws Exception {
        // Verificar que la cita existe
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
        List<Cita> todasLasCitas = citaRepo.findAll();
        List<Cita> citasFiltradas = todasLasCitas.stream()
                .filter(cita -> cita.getUsuario() != null && cita.getUsuario().getIdUsuario().equals(idUsuario))
                .toList();
        return citaMapper.citasToCitasDTO(citasFiltradas);
    }

    @Override
    public List<CitaDTO> listarCitasPorEmpleado(int idEmpleado) {
        List<Cita> todasLasCitas = citaRepo.findAll();
        List<Cita> citasFiltradas = todasLasCitas.stream()
                .filter(cita -> cita.getEmpleado() != null && cita.getEmpleado().getIdEmpleado().equals(idEmpleado))
                .toList();
        return citaMapper.citasToCitasDTO(citasFiltradas);
    }
}