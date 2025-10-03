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

    // Buscar citas por usuario
    List<Cita> findByUsuarioEmail(String emailUsuario);

    // Buscar cita por ID y email de usuario (para seguridad)
    Optional<Cita> findByIdCitaAndUsuarioEmail(Integer idCita, String emailUsuario);

    // Buscar citas por estado
    List<Cita> findByEstadoCita(EstadoCita estadoCita);

    // Actualizar estado de una cita
    @Modifying
    @Query("UPDATE Cita c SET c.estadoCita = :estado WHERE c.idCita = :idCita")
    void actualizarEstadoCita(@Param("idCita") Integer idCita, @Param("estado") EstadoCita estado);

    // Verificar si una cita puede ser cancelada (no está completada ni ya cancelada)
    @Query("SELECT c FROM Cita c WHERE c.idCita = :idCita AND c.estadoCita IN (co.edu.uniquindio.BarakaLashes.modelo.EstadoCita.PENDIENTE, co.edu.uniquindio.BarakaLashes.modelo.EstadoCita.CONFIRMADA)")
    Optional<Cita> findCancelableCita(@Param("idCita") Integer idCita);

    // Buscar por nombre de cita (opcional)
    Optional<Cita> findByNombreCita(String nombreCita);

    // Buscar citas por usuario (por ID en lugar de email)
    @Query("SELECT c FROM Cita c WHERE c.usuario.idUsuario = :idUsuario")
    List<Cita> findByUsuarioIdUsuario(@Param("idUsuario") Integer idUsuario);

    // Buscar citas por empleado
    @Query("SELECT c FROM Cita c WHERE c.empleado.idEmpleado = :idEmpleado")
    List<Cita> findByEmpleadoIdEmpleado(@Param("idEmpleado") Integer idEmpleado);
}