//package co.edu.uniquindio.BarakaLashes.controlador;
//
//import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
//
//import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(CitaController.class)
//class CitaControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CitaServicio citaServicio;
//
//    @Test
//    void mostrarFormularioCita_DebeRetornarVistaFormulario() throws Exception {
//        mockMvc.perform(get("/citas/nueva"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("crearCita"))
//                .andExpect(model().attributeExists("cita"));
//    }
//
//    @Test
//    void crearCita_DebeRedirigirAlInicio_CuandoExitoso() throws Exception {
//        // Arrange
//        when(citaServicio.crearCita(any(CitaDTO.class))).thenReturn(1);
//
//        // Act & Assert
//        mockMvc.perform(post("/citas/nueva")
//                        .param("nombreCita", "Test Cita")
//                        .param("fechaCita", LocalDateTime.now().plusDays(1).toString())
//                        .param("emailCliente", "test@test.com")
//                        .param("descripcionCita", "Descripción de prueba"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/"))
//                .andExpect(flash().attributeExists("mensaje"));
//
//        verify(citaServicio, times(1)).crearCita(any(CitaDTO.class));
//    }
//
//    @Test
//    void crearCita_DebeRedirigirConError_CuandoFalla() throws Exception {
//        // Arrange
//        when(citaServicio.crearCita(any(CitaDTO.class)))
//                .thenThrow(new RuntimeException("Error de prueba"));
//
//        // Act & Assert
//        mockMvc.perform(post("/citas/nueva")
//                        .param("nombreCita", "Test Cita")
//                        .param("fechaCita", LocalDateTime.now().plusDays(1).toString()))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/citas/nueva?error"))
//                .andExpect(flash().attributeExists("error"));
//    }
//
//    @Test
//    void listarCitas_DebeRetornarListaCitas() throws Exception {
//        // Arrange
//        CitaDTO cita1 = new CitaDTO();
//        cita1.setIdCita(1);
//        cita1.setNombreCita("Cita 1");
//
//        CitaDTO cita2 = new CitaDTO();
//        cita2.setIdCita(2);
//        cita2.setNombreCita("Cita 2");
//
//        List<CitaDTO> citas = Arrays.asList(cita1, cita2);
//
//        when(citaServicio.listarCitas()).thenReturn(citas);
//
//        // Act & Assert
//        mockMvc.perform(get("/citas"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("listarCitas"))
//                .andExpect(model().attributeExists("citas"))
//                .andExpect(model().attribute("citas", citas));
//    }
//
//    @Test
//    void obtenerCita_DebeRetornarDetalleCita() throws Exception {
//        // Arrange
//        CitaDTO cita = new CitaDTO();
//        cita.setIdCita(1);
//        cita.setNombreCita("Cita Test");
//
//        when(citaServicio.obtenerCita(1)).thenReturn(cita);
//
//        // Act & Assert
//        mockMvc.perform(get("/citas/1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("detalleCita"))
//                .andExpect(model().attributeExists("cita"))
//                .andExpect(model().attribute("cita", cita));
//    }
//
//    @Test
//    void obtenerCita_DebeRedirigirCuandoNoExiste() throws Exception {
//        // Arrange
//        when(citaServicio.obtenerCita(999))
//                .thenThrow(new RuntimeException("Cita no encontrada"));
//
//        // Act & Assert
//        mockMvc.perform(get("/citas/999"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/citas"));
//    }
//
//    @Test
//    void eliminarCita_DebeEliminarExitosamente() throws Exception {
//        // Arrange
//        when(citaServicio.eliminarCita(1)).thenReturn(1);
//
//        // Act & Assert
//        mockMvc.perform(post("/citas/1/eliminar"))
//                .andExpect(status().is3xxRedirection())
//                .andExpect(redirectedUrl("/citas"))
//                .andExpect(flash().attributeExists("mensaje"));
//
//        verify(citaServicio, times(1)).eliminarCita(1);
//    }
//
//    @Test
//    void listarCitasPorUsuario_DebeRetornarCitasDelUsuario() throws Exception {
//        // Arrange
//        CitaDTO cita = new CitaDTO();
//        cita.setIdCita(1);
//        cita.setNombreCita("Cita Usuario");
//
//        List<CitaDTO> citas = Arrays.asList(cita);
//
//        when(citaServicio.listarCitasPorUsuario(1)).thenReturn(citas);
//
//        // Act & Assert
//        mockMvc.perform(get("/citas/usuario/1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("listarCitas"))
//                .andExpect(model().attributeExists("citas"))
//                .andExpect(model().attribute("citas", citas))
//                .andExpect(model().attributeExists("titulo"));
//    }
//
//    @Test
//    void listarCitasPorEmpleado_DebeRetornarCitasDelEmpleado() throws Exception {
//        // Arrange
//        CitaDTO cita = new CitaDTO();
//        cita.setIdCita(1);
//        cita.setNombreCita("Cita Empleado");
//
//        List<CitaDTO> citas = Arrays.asList(cita);
//
//        when(citaServicio.listarCitasPorEmpleado(1)).thenReturn(citas);
//
//        // Act & Assert
//        mockMvc.perform(get("/citas/empleado/1"))
//                .andExpect(status().isOk())
//                .andExpect(view().name("listarCitas"))
//                .andExpect(model().attributeExists("citas"))
//                .andExpect(model().attribute("citas", citas))
//                .andExpect(model().attributeExists("titulo"));
//    }
//}