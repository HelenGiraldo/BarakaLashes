package co.edu.uniquindio.BarakaLashes.servicio;

import co.edu.uniquindio.BarakaLashes.DTO.Cita.CitaActualizadaDTO;
import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;

import java.util.List;

public interface CitaServicio {

    /**
     * Crea una nueva cita
     * @param citaDTO datos de la cita a crear
     * @return ID de la cita creada
     * @throws Exception si hay algún error en la creación
     */
    int crearCita(CitaDTO citaDTO) throws Exception;

    /**
     * Actualiza una cita existente
     * @param idCita ID de la cita a actualizar
     * @param citaDTO nuevos datos de la cita
     * @return ID de la cita actualizada
     * @throws Exception si la cita no existe o hay error en la actualización
     */
    int actualizarCita(int idCita, CitaDTO citaDTO) throws Exception;

    /**
     * Elimina una cita
     * @param idCita ID de la cita a eliminar
     * @return ID de la cita eliminada
     * @throws Exception si la cita no existe
     */
    int eliminarCita(int idCita) throws Exception;


    int modificarCita(int idCita, CitaActualizadaDTO citaDTO) throws Exception;
    /**
     * Obtiene una cita por su ID
     * @param idCita ID de la cita
     * @return DTO con los datos de la cita
     * @throws Exception si la cita no existe
     */
    CitaDTO obtenerCita(int idCita) throws Exception;

    /**
     * Lista todas las citas del sistema
     * @return Lista de todas las citas
     */
    List<CitaDTO> listarCitas();

    /**
     * Lista las citas de un usuario específico por su ID
     * @param idUsuario ID del usuario
     * @return Lista de citas del usuario
     */
    List<CitaDTO> listarCitasPorUsuario(int idUsuario);

    /**
     * Lista las citas de un usuario por su email
     * @param emailUsuario Email del usuario
     * @return Lista de citas del usuario
     */
    List<CitaDTO> listarCitasPorUsuarioEmail(String emailUsuario);

    /**
     * Cancela una cita
     * @param idCita ID de la cita a cancelar
     * @return ID de la cita cancelada
     * @throws Exception si la cita no se puede cancelar
     */
    int cancelarCita(int idCita) throws Exception;

    /**
     * Obtiene el historial completo de citas de un usuario
     * @param emailUsuario Email del usuario
     * @return Lista completa de citas del usuario (todas los estados)
     * @throws Exception si el usuario no existe
     */
    List<CitaDTO> obtenerHistorialCitas(String emailUsuario) throws Exception;
}