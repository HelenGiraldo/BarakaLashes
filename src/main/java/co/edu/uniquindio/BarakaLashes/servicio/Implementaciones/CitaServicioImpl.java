package co.edu.uniquindio.BarakaLashes.servicio.Implementaciones;

import co.edu.uniquindio.BarakaLashes.DTO.Cita.CitaActualizadaDTO;
import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import co.edu.uniquindio.BarakaLashes.repositorio.CitaRepositorio;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;

import java.io.IOException;
import java.util.List;
public class CitaServicioImpl implements CitaServicio {
    public CitaRepositorio repositorio;


    @Override
    public List<CitaDTO> listarCitas() {
        return List.of();
    }

    @Override
    public CitaDTO obtenerCitaPorId(Integer idCita) {

        return null;
    }

    @Override
    public CitaDTO crearCita(CitaDTO cita) throws IOException {

        return null;//repositorio.save(cita)    -Necesita el mapper para guardar un objeto
    }

    @Override
    public CitaActualizadaDTO actualizarCita(Integer id, Cita cita) {
        return null;
    }

    @Override
    public void eliminarCita(Integer id) {

    }
}
