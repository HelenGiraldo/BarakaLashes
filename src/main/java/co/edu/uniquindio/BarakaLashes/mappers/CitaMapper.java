package co.edu.uniquindio.BarakaLashes.mappers;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CitaMapper {
    CitaDTO citaToCitaDTO(Cita cita);
    List<CitaDTO> citasToCitasDTO(List<Cita> citas);
    Cita citaDTOToCita(CitaDTO citaDTO);
}