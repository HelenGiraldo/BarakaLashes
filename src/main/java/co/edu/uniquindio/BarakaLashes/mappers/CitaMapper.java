package co.edu.uniquindio.BarakaLashes.mappers;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Interfaz de mapeo para convertir entre la Entidad Cita y sus DTOs.
 * Usa MapStruct para generar el código de implementación automáticamente.
 */

@Mapper(componentModel = "spring")
public interface CitaMapper {

    CitaMapper INSTANCE = Mappers.getMapper(CitaMapper.class);

    CitaDTO citaToCitaDTO(Cita cita);

    List<CitaDTO> listCitaToListCitaDTO(List<Cita> citas);

    @InheritInverseConfiguration
    Cita citaDTOToCita(CitaDTO citaDTO);

}
