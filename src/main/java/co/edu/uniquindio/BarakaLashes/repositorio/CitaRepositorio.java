package co.edu.uniquindio.BarakaLashes.repositorio;

import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de Citas: Interfaz para interactuar con la base de datos.
 * Extiende JpaRepository para obtener operaciones CRUD básicas.
 */
@Repository
public interface CitaRepositorio extends JpaRepository<Cita, Integer> {

}
