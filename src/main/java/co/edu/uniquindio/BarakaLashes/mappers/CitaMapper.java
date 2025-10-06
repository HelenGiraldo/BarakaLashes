package co.edu.uniquindio.BarakaLashes.mappers;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CitaMapper {

    @Mappings({
            @Mapping(source = "listaServicios", target = "serviciosSeleccionados"),
            @Mapping(source = "usuario.email", target = "emailCliente")
    })
    CitaDTO citaToCitaDTO(Cita cita);

    List<CitaDTO> citasToCitasDTO(List<Cita> citas);

    @Mappings({
            @Mapping(source = "serviciosSeleccionados", target = "listaServicios"),
            @Mapping(target = "usuario", ignore = true),
            @Mapping(target = "negocio", ignore = true)
    })
    Cita citaDTOToCita(CitaDTO citaDTO);
}