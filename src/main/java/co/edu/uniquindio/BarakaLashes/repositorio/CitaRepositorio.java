package co.edu.uniquindio.BarakaLashes.repositorio;

import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CitaRepositorio extends JpaRepository<Cita, Integer> {

    // Buscar citas por ID del usuario
    List<Cita> findByUsuarioIdUsuario(Integer idUsuario);

    // Buscar citas por email del usuario (a través de la relación)
    List<Cita> findByUsuarioEmail(String email);

    // Buscar citas por email del usuario ordenadas por fecha descendente
    List<Cita> findByUsuarioEmailOrderByFechaCitaDesc(String email);

    // Buscar citas por estado
    List<Cita> findByEstadoCita(EstadoCita estado);

    // Buscar citas por usuario y estado
    List<Cita> findByUsuarioEmailAndEstadoCita(String email, EstadoCita estado);

    // Query personalizada para buscar citas cancelables
    @Query("SELECT c FROM Cita c WHERE c.idCita = :idCita AND " +
            "(c.estadoCita = 'PENDIENTE' OR c.estadoCita = 'CONFIRMADA')")
    Optional<Cita> findCancelableCita(@Param("idCita") Integer idCita);

    // Actualizar estado de una cita
    @Modifying
    @Query("UPDATE Cita c SET c.estadoCita = :estado WHERE c.idCita = :idCita")
    void actualizarEstadoCita(@Param("idCita") Integer idCita, @Param("estado") EstadoCita estado);

    // Query para obtener todas las citas (útil para debug)
    @Query("SELECT c FROM Cita c LEFT JOIN FETCH c.usuario LEFT JOIN FETCH c.listaServicios")
    List<Cita> obtenerTodasLasCitasConRelaciones();
}