package co.edu.uniquindio.BarakaLashes.servicio;

import co.edu.uniquindio.BarakaLashes.DTO.Cita.CitaActualizadaDTO;
import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Cita; // Aunque no se usa aquí, puede ser necesario para otros métodos.
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interfaz para la capa de servicio de Citas.
 */
@Service
public interface CitaServicio {

    List<CitaDTO> listarCitas();

    List<CitaDTO> listarCitasPorUsuario(String emailUsuario);

    CitaDTO obtenerCitaPorId(Integer idCita);

    CitaDTO obtenerCitaPorIdYUsuario(Integer idCita, String emailUsuario);

    CitaDTO crearCita(CitaDTO cita);

    void cancelarCita(Integer idCita, String emailUsuario);

    CitaActualizadaDTO actualizarCita(Integer id, CitaDTO citaDTO);

    void eliminarCita(Integer id);
}
