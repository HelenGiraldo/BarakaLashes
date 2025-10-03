package co.edu.uniquindio.BarakaLashes.servicio;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import java.util.List;

public interface CitaServicio {
    int crearCita(CitaDTO citaDTO) throws Exception;
    int actualizarCita(int idCita, CitaDTO citaDTO) throws Exception;
    int eliminarCita(int idCita) throws Exception;
    CitaDTO obtenerCita(int idCita) throws Exception;
    List<CitaDTO> listarCitas();
    List<CitaDTO> listarCitasPorUsuario(int idUsuario);
    List<CitaDTO> listarCitasPorEmpleado(int idEmpleado);
}