package co.edu.uniquindio.BarakaLashes.servicio;

import co.edu.uniquindio.BarakaLashes.DTO.Cita.CitaActualizadaDTO;
import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Service
public interface CitaServicio {
    List<CitaDTO> listarCitas();

    CitaDTO obtenerCitaPorId(Integer idCita);

    CitaDTO crearCita(CitaDTO cita) throws IOException;

    CitaActualizadaDTO actualizarCita(Integer id, Cita cita);

    void eliminarCita(Integer id);
}
