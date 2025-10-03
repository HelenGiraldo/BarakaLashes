//package co.edu.uniquindio.BarakaLashes.servicio.Implementaciones;
//
//import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
//import co.edu.uniquindio.BarakaLashes.modelo.Cita;
//import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
//import co.edu.uniquindio.BarakaLashes.repositorio.CitaRepositorio;
//import co.edu.uniquindio.BarakaLashes.mappers.CitaMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CitaServicioImplTest {
//
//    @Mock
//    private CitaRepositorio citaRepositorio;
//
//    @Mock
//    private CitaMapper citaMapper;
//
//    @InjectMocks
//    private CitaServicioImpl citaServicio;
//
//    private CitaDTO citaDTO;
//    private Cita cita;
//    private LocalDateTime fechaCita;
//
//    @BeforeEach
//    void setUp() {
//        fechaCita = LocalDateTime.of(2024, 1, 15, 10, 0);
//
//        citaDTO = new CitaDTO();
//        citaDTO.setNombreCita("Cita de prueba");
//        citaDTO.setDescripcionCita("Descripción de prueba");
//        citaDTO.setFechaCita(fechaCita);
//        citaDTO.setEstadoCita(EstadoCita.PENDIENTE);
//
//        cita = new Cita();
//        cita.setIdCita(1);
//        cita.setNombreCita("Cita de prueba");
//        cita.setDescripcionCita("Descripción de prueba");
//        cita.setFechaCita(fechaCita);
//        cita.setEstadoCita(EstadoCita.PENDIENTE);
//    }
//
//    @Test
//    void listarCitas_DebeRetornarListaVacia_CuandoNoHayCitas() {
//        // Arrange
//        when(citaRepositorio.findAll()).thenReturn(Collections.emptyList());
//        when(citaMapper.citasToCitasDTO(any())).thenReturn(Collections.emptyList());
//
//        // Act
//        List<CitaDTO> resultado = citaServicio.listarCitas();
//
//        // Assert
//        assertNotNull(resultado);
//        assertTrue(resultado.isEmpty());
//        verify(citaRepositorio, times(1)).findAll();
//    }
//
//    @Test
//    void listarCitas_DebeRetornarListaCitas_CuandoExistenCitas() {
//        // Arrange
//        List<Cita> citas = Arrays.asList(cita, new Cita());
//        List<CitaDTO> citasDTO = Arrays.asList(citaDTO, new CitaDTO());
//
//        when(citaRepositorio.findAll()).thenReturn(citas);
//        when(citaMapper.citasToCitasDTO(citas)).thenReturn(citasDTO);
//
//        // Act
//        List<CitaDTO> resultado = citaServicio.listarCitas();
//
//        // Assert
//        assertNotNull(resultado);
//        assertEquals(2, resultado.size());
//        verify(citaRepositorio, times(1)).findAll();
//        verify(citaMapper, times(1)).citasToCitasDTO(citas);
//    }
//
//    @Test
//    void obtenerCita_DebeRetornarCita_CuandoExiste() throws Exception {
//        // Arrange
//        int idCita = 1;
//        when(citaRepositorio.findById(idCita)).thenReturn(Optional.of(cita));
//        when(citaMapper.citaToCitaDTO(cita)).thenReturn(citaDTO);
//
//        // Act
//        CitaDTO resultado = citaServicio.obtenerCita(idCita);
//
//        // Assert
//        assertNotNull(resultado);
//        assertEquals(citaDTO.getNombreCita(), resultado.getNombreCita());
//        verify(citaRepositorio, times(1)).findById(idCita);
//    }
//
//    @Test
//    void obtenerCita_DebeLanzarExcepcion_CuandoNoExiste() {
//        // Arrange
//        int idCita = 999;
//        when(citaRepositorio.findById(idCita)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        Exception exception = assertThrows(Exception.class,
//                () -> citaServicio.obtenerCita(idCita));
//
//        assertEquals("Cita no encontrada con ID: " + idCita, exception.getMessage());
//        verify(citaRepositorio, times(1)).findById(idCita);
//    }
//
//    @Test
//    void crearCita_DebeCrearCitaExitosamente() throws Exception {
//        // Arrange
//        when(citaMapper.citaDTOToCita(citaDTO)).thenReturn(cita);
//        when(citaRepositorio.save(cita)).thenReturn(cita);
//
//        // Act
//        int resultado = citaServicio.crearCita(citaDTO);
//
//        // Assert
//        assertEquals(1, resultado);
//        verify(citaMapper, times(1)).citaDTOToCita(citaDTO);
//        verify(citaRepositorio, times(1)).save(cita);
//    }
//
//    @Test
//    void eliminarCita_DebeEliminarCitaExitosamente() throws Exception {
//        // Arrange
//        int idCita = 1;
//        when(citaRepositorio.existsById(idCita)).thenReturn(true);
//
//        // Act
//        int resultado = citaServicio.eliminarCita(idCita);
//
//        // Assert
//        assertEquals(idCita, resultado);
//        verify(citaRepositorio, times(1)).existsById(idCita);
//        verify(citaRepositorio, times(1)).deleteById(idCita);
//    }
//
//    @Test
//    void eliminarCita_DebeLanzarExcepcion_CuandoNoExiste() {
//        // Arrange
//        int idCita = 999;
//        when(citaRepositorio.existsById(idCita)).thenReturn(false);
//
//        // Act & Assert
//        Exception exception = assertThrows(Exception.class,
//                () -> citaServicio.eliminarCita(idCita));
//
//        assertEquals("Cita a eliminar no encontrada.", exception.getMessage());
//        verify(citaRepositorio, times(1)).existsById(idCita);
//        verify(citaRepositorio, never()).deleteById(idCita);
//    }
//
//    @Test
//    void actualizarCita_DebeActualizarExitosamente() throws Exception {
//        // Arrange
//        int idCita = 1;
//        when(citaRepositorio.findById(idCita)).thenReturn(Optional.of(cita));
//        when(citaRepositorio.save(cita)).thenReturn(cita);
//
//        // Act
//        int resultado = citaServicio.actualizarCita(idCita, citaDTO);
//
//        // Assert
//        assertEquals(idCita, resultado);
//        verify(citaRepositorio, times(1)).findById(idCita);
//        verify(citaRepositorio, times(1)).save(cita);
//    }
//}