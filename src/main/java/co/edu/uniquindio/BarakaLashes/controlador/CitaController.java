package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;


import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaServicio citaServicio;

    @GetMapping("/nueva")
    public String mostrarFormularioCita(Model model) {
        model.addAttribute("cita", new CitaDTO());
        return "crearCita";
    }

    @PostMapping("/nueva")
    public String crearCita(@ModelAttribute("cita")  CitaDTO citaDTO,
                            jakarta.servlet.http.HttpSession session,
                            RedirectAttributes redirectAttributes) {
        try {
            int idCita = citaServicio.crearCita(citaDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Cita creada exitosamente con ID: " + idCita);
            // Guardar email en sesión para mostrar historial correctamente
            if (citaDTO.getEmailCliente() != null) {
                session.setAttribute("email", citaDTO.getEmailCliente());
            }
            // Redirigir al historial de citas del usuario
            return "redirect:/citas/historial";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la cita: " + e.getMessage());
            return "redirect:/citas/nueva?error";
        }
    }

    @GetMapping
    public String listarCitas(Model model) {
        try {
            List<CitaDTO> citas = citaServicio.listarCitas();
            model.addAttribute("citas", citas);
            return "listarCitas";
        } catch (Exception e) {
            model.addAttribute("error", "Error al listar citas: " + e.getMessage());
            return "listarCitas";
        }
    }

    @GetMapping("/{idCita}")
    public String obtenerCita(@PathVariable int idCita, Model model) {
        try {
            CitaDTO cita = citaServicio.obtenerCita(idCita);
            model.addAttribute("cita", cita);
            return "detalleCita";
        } catch (Exception e) {
            model.addAttribute("error", "Error al obtener cita: " + e.getMessage());
            return "redirect:/citas";
        }
    }

    @GetMapping("/{idCita}/editar")
    public String mostrarFormularioEdicion(@PathVariable int idCita, Model model) {
        try {
            CitaDTO cita = citaServicio.obtenerCita(idCita);
            model.addAttribute("cita", cita);
            return "editarCita";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar cita para edición: " + e.getMessage());
            return "redirect:/citas";
        }
    }

    @PostMapping("/{idCita}/editar")
    public String actualizarCita(@PathVariable int idCita,
                                 @ModelAttribute("cita")  CitaDTO citaDTO,
                                 RedirectAttributes redirectAttributes) {
        try {
            int idActualizado = citaServicio.actualizarCita(idCita, citaDTO);
            redirectAttributes.addFlashAttribute("mensaje", "Cita actualizada exitosamente");
            return "redirect:/citas/" + idActualizado;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar cita: " + e.getMessage());
            return "redirect:/citas/" + idCita + "/editar?error";
        }
    }

    @PostMapping("/{idCita}/eliminar")
    public String eliminarCita(@PathVariable int idCita, RedirectAttributes redirectAttributes) {
        try {
            int idEliminado = citaServicio.eliminarCita(idCita);
            redirectAttributes.addFlashAttribute("mensaje", "Cita eliminada exitosamente");
            return "redirect:/citas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar cita: " + e.getMessage());
            return "redirect:/citas/" + idCita;
        }
    }

    @GetMapping("/usuario/{idUsuario}")
    public String listarCitasPorUsuario(@PathVariable int idUsuario, Model model) {
        try {
            List<CitaDTO> citas = citaServicio.listarCitasPorUsuario(idUsuario);
            model.addAttribute("citas", citas);
            model.addAttribute("titulo", "Citas del Usuario " + idUsuario);
            return "listarCitas";
        } catch (Exception e) {
            model.addAttribute("error", "Error al listar citas del usuario: " + e.getMessage());
            return "listarCitas";
        }
    }


    @GetMapping("/historial")
    public String historialCitas(Model model, jakarta.servlet.http.HttpSession session) {

        // 1. Obtener el email del cliente de la sesión (establecido en /citas/nueva)
        String emailUsuario = (String) session.getAttribute("email");

        if (emailUsuario == null) {
            // Si no hay email, mostramos la vista con un error, pero no fallamos.
            model.addAttribute("error", "No se encontró un cliente asociado a la sesión. Por favor, inicie sesión o cree una cita.");
            model.addAttribute("citas", Collections.emptyList());
            return "historialCitas";
        }

        try {
            // 2. Usar el email de la sesión para buscar las citas.
            List<CitaDTO> citas = citaServicio.listarCitasPorEmail(emailUsuario);
            model.addAttribute("citas", citas);
        } catch (Exception e) {
            // 3. Si falla el servicio, capturamos la excepción y la mostramos.
            model.addAttribute("error", "Error al cargar las citas: " + e.getMessage());
            model.addAttribute("citas", Collections.emptyList());
        }

        return "historialCitas";
    }


    @PostMapping("/{idCita}/cancelar")
    public String cancelarCita(@PathVariable int idCita, RedirectAttributes redirectAttributes) {
        try {
            citaServicio.cancelarCita(idCita);
            redirectAttributes.addFlashAttribute("mensaje", "Cita cancelada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/citas/historial"; // Correctamente redirige al historial
    }
}

