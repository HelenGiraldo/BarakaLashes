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

/**
 * Repositorio de Citas: Interfaz para interactuar con la base de datos.
 * Extiende JpaRepository para obtener operaciones CRUD básicas.
 */
@Repository
public interface CitaRepositorio extends JpaRepository<Cita, Integer> {

    // Buscar citas por usuario
    List<Cita> findByUsuarioEmail(String emailUsuario);

    // Buscar cita por ID y email de usuario (para seguridad)
    Optional<Cita> findByIdCitaAndUsuarioEmail(Integer idCita, String emailUsuario);

    // Actualizar estado de una cita
    @Modifying
    @Query("UPDATE Cita c SET c.estadoCita = :estado WHERE c.idCita = :idCita")
    void actualizarEstadoCita(@Param("idCita") Integer idCita, @Param("estado") EstadoCita estado);

    // Verificar si una cita puede ser cancelada (no está completada ni ya cancelada)
    @Query("SELECT c FROM Cita c WHERE c.idCita = :idCita AND c.estadoCita IN (co.edu.uniquindio.BarakaLashes.modelo.EstadoCita.PENDIENTE, co.edu.uniquindio.BarakaLashes.modelo.EstadoCita.CONFIRMADA)")
    Optional<Cita> findCancelableCita(@Param("idCita") Integer idCita);
}