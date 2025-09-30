package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;

import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CitaController {

    private final CitaServicio citaServicio;

    public CitaController(CitaServicio citaServicio) {
        this.citaServicio = citaServicio;
    }

    /**
     * Muestra el formulario para crear una nueva cita.
     * Mapea a la URL: http://localhost:8080/citas/nueva
     */
    @GetMapping("/citas/nueva")
    public String mostrarFormularioCita(Model model) {
        model.addAttribute("cita", new CitaDTO());
        return "crearCita";
    }

    /**
     * Procesa el envío del formulario.
     */
    @PostMapping("/citas/nueva")
    public String crearCita(CitaDTO citaDTO) {
        try {
            citaServicio.crearCita(citaDTO);
            return "redirect:/";
        } catch (Exception e) {
            System.err.println("Error al crear la cita: " + e.getMessage());
            // TODO: Añadir manejo de errores en la vista
            return "redirect:/citas/nueva?error";
        }
    }
}