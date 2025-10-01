package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;

import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

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

    /**
     * end point para cancelar una cita
     * Mapea a la URL: http://localhost:8080/citas/{id}/cancelar
     * Donde {id} es el ID de la cita a cancelar
     */

    @PostMapping("/citas/{id}/cancelar")
    public String cancelarCita(@PathVariable Integer id,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            String emailUsuario = principal.getName();
            citaServicio.cancelarCita(id, emailUsuario);
            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar la cita: " + e.getMessage());
        }
        return "redirect:/citas/mis-citas";
    }
}