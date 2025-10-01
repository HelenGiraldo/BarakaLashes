package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CitaController.class)
class CitaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CitaServicio citaServicio;

    @Test
    void mostrarFormularioCita_DebeRetornarVistaFormulario() throws Exception {
        mockMvc.perform(get("/citas/nueva"))
                .andExpect(status().isOk())
                .andExpect(view().name("crearCita"))
                .andExpect(model().attributeExists("cita"));
    }

    @Test
    void crearCita_DebeRedirigirAlInicio_CuandoExitoso() throws Exception {
        // Arrange
        CitaDTO citaDTO = new CitaDTO();
        citaDTO.setNombreCita("Test Cita");
        citaDTO.setFechaCita(LocalDateTime.now().plusDays(1));
        citaDTO.setEmailCliente("test@test.com");
        citaDTO.setServiciosSeleccionados(Set.of("CLASICAS"));

        when(citaServicio.crearCita(any(CitaDTO.class))).thenReturn(citaDTO);

        // Act & Assert
        mockMvc.perform(post("/citas/nueva")
                        .param("nombreCita", "Test Cita")
                        .param("fechaCita", LocalDateTime.now().plusDays(1).toString())
                        .param("emailCliente", "test@test.com")
                        .param("serviciosSeleccionados", "CLASICAS"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void crearCita_DebeRedirigirConError_CuandoFalla() throws Exception {
        // Arrange
        when(citaServicio.crearCita(any(CitaDTO.class)))
                .thenThrow(new RuntimeException("Error de prueba"));

        // Act & Assert
        mockMvc.perform(post("/citas/nueva")
                        .param("nombreCita", "Test Cita")
                        .param("fechaCita", LocalDateTime.now().plusDays(1).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/citas/nueva?error"));
    }
}