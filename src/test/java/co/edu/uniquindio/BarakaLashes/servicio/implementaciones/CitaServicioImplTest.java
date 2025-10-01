package co.edu.uniquindio.BarakaLashes.servicio.Implementaciones;


import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.Cita;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.repositorio.CitaRepositorio;
import co.edu.uniquindio.BarakaLashes.mappers.CitaMapper;
import co.edu.uniquindio.BarakaLashes.servicio.Implementaciones.CitaServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaServicioImplTest {

    @Mock
    private CitaRepositorio citaRepositorio;

    @Mock
    private CitaMapper citaMapper;

    @InjectMocks
    private CitaServicioImpl citaServicio;

    private CitaDTO citaDTO;
    private Cita cita;
    private LocalDateTime fechaCita;

    @BeforeEach
    void setUp() {
        fechaCita = LocalDateTime.of(2024, 1, 15, 10, 0);

        citaDTO = new CitaDTO();
        citaDTO.setNombreCita("Cita de prueba");
        citaDTO.setDescripcionCita("Descripción de prueba");
        citaDTO.setFechaCita(fechaCita);
        citaDTO.setEmailCliente("cliente@test.com");
        citaDTO.setIdEmpleado(1);
        citaDTO.setServiciosSeleccionados(Set.of("CLASICAS", "VOLUMEN"));

        cita = new Cita();
        cita.setIdCita(1);
        cita.setNombreCita("Cita de prueba");
        cita.setDescripcionCita("Descripción de prueba");
        cita.setFechaCita(fechaCita);
        cita.setEstadoCita(EstadoCita.PENDIENTE);
    }

    @Test
    void listarCitas_DebeRetornarListaVacia_CuandoNoHayCitas() {
        // Arrange
        when(citaRepositorio.findAll()).thenReturn(Collections.emptyList());
        when(citaMapper.listCitaToListCitaDTO(any())).thenReturn(Collections.emptyList());

        // Act
        List<CitaDTO> resultado = citaServicio.listarCitas();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(citaRepositorio, times(1)).findAll();
    }

    @Test
    void listarCitas_DebeRetornarListaCitas_CuandoExistenCitas() {
        // Arrange
        List<Cita> citas = Arrays.asList(cita, new Cita());
        List<CitaDTO> citasDTO = Arrays.asList(citaDTO, new CitaDTO());

        when(citaRepositorio.findAll()).thenReturn(citas);
        when(citaMapper.listCitaToListCitaDTO(citas)).thenReturn(citasDTO);

        // Act
        List<CitaDTO> resultado = citaServicio.listarCitas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(citaRepositorio, times(1)).findAll();
        verify(citaMapper, times(1)).listCitaToListCitaDTO(citas);
    }

    @Test
    void obtenerCitaPorId_DebeRetornarCita_CuandoExiste() {
        // Arrange
        Integer idCita = 1;
        when(citaRepositorio.findById(idCita)).thenReturn(Optional.of(cita));
        when(citaMapper.citaToCitaDTO(cita)).thenReturn(citaDTO);

        // Act
        CitaDTO resultado = citaServicio.obtenerCitaPorId(idCita);

        // Assert
        assertNotNull(resultado);
        assertEquals(citaDTO.getNombreCita(), resultado.getNombreCita());
        verify(citaRepositorio, times(1)).findById(idCita);
    }

    @Test
    void obtenerCitaPorId_DebeLanzarExcepcion_CuandoNoExiste() {
        // Arrange
        Integer idCita = 999;
        when(citaRepositorio.findById(idCita)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> citaServicio.obtenerCitaPorId(idCita));

        assertEquals("Cita no encontrada con ID: " + idCita, exception.getMessage());
        verify(citaRepositorio, times(1)).findById(idCita);
    }

    @Test
    void crearCita_DebeCrearCitaExitosamente() {
        // Arrange
        when(citaMapper.citaDTOToCita(citaDTO)).thenReturn(cita);
        when(citaRepositorio.save(cita)).thenReturn(cita);
        when(citaMapper.citaToCitaDTO(cita)).thenReturn(citaDTO);

        // Act
        CitaDTO resultado = citaServicio.crearCita(citaDTO);

        // Assert
        assertNotNull(resultado);
        assertEquals(EstadoCita.PENDIENTE, cita.getEstadoCita());
        verify(citaMapper, times(1)).citaDTOToCita(citaDTO);
        verify(citaRepositorio, times(1)).save(cita);
        verify(citaMapper, times(1)).citaToCitaDTO(cita);
    }

    @Test
    void eliminarCita_DebeEliminarCitaExitosamente() {
        // Arrange
        Integer idCita = 1;
        when(citaRepositorio.existsById(idCita)).thenReturn(true);

        // Act
        citaServicio.eliminarCita(idCita);

        // Assert
        verify(citaRepositorio, times(1)).existsById(idCita);
        verify(citaRepositorio, times(1)).deleteById(idCita);
    }

    @Test
    void eliminarCita_DebeLanzarExcepcion_CuandoNoExiste() {
        // Arrange
        Integer idCita = 999;
        when(citaRepositorio.existsById(idCita)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> citaServicio.eliminarCita(idCita));

        assertEquals("Cita a eliminar no encontrada.", exception.getMessage());
        verify(citaRepositorio, times(1)).existsById(idCita);
        verify(citaRepositorio, never()).deleteById(idCita);
    }
}