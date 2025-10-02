package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CitaController {

    private final CitaServicio citaServicio;

    /**
     * Muestra el formulario para crear una nueva cita.
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
    public String crearCita(CitaDTO citaDTO, RedirectAttributes redirectAttributes) {
        try {
            citaServicio.crearCita(citaDTO);
            redirectAttributes.addFlashAttribute("success", "Cita agendada exitosamente");
            return "redirect:/citas/mis-citas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la cita: " + e.getMessage());
            return "redirect:/citas/nueva";
        }
    }

    /**
     * Muestra el historial de citas del usuario.
     */
    @GetMapping("/citas/mis-citas")
    public String mostrarMisCitas(Model model, HttpSession session) {
        try {
            // Verificar si el usuario está autenticado
            String emailUsuario = (String) session.getAttribute("email");
            if (emailUsuario == null) {
                return "redirect:/auth/login";
            }

            List<CitaDTO> citas = citaServicio.listarCitasPorUsuario(emailUsuario);
            model.addAttribute("citas", citas);

            // Pasar información del usuario a la vista
            model.addAttribute("usuario", session.getAttribute("usuario"));

        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las citas: " + e.getMessage());
        }
        return "historialCitas";
    }

    /**
     * Endpoint para cancelar una cita
     */
    @PostMapping("/citas/{id}/cancelar")
    public String cancelarCita(@PathVariable("id") Integer id,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            // Para pruebas, usa un email fijo si no hay usuario autenticado
            String emailUsuario = (principal != null) ? principal.getName() : "ana@test.com";
            citaServicio.cancelarCita(id, emailUsuario);
            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar la cita: " + e.getMessage());
        }
        return "redirect:/citas/mis-citas";
    }

    /**
     * Página de inicio redirige al historial
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/citas/mis-citas";
    }
}