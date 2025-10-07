package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;

@Slf4j
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
    public String crearCita(@ModelAttribute("cita") CitaDTO citaDTO,
                            HttpSession session,
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
    public String listarCitas(Model model, HttpSession session) {
        try {
            String usuarioEmail = (String) session.getAttribute("usuarioEmail");
            log.info("=== LISTANDO CITAS PARA USUARIO: {} ===", usuarioEmail);

            if (usuarioEmail == null) {
                log.error("Usuario no autenticado");
                model.addAttribute("error", "Debes iniciar sesión para ver tus citas");
                return "listarCitas";
            }

            List<CitaDTO> citas = citaServicio.listarCitasPorUsuarioEmail(usuarioEmail);

            log.info("Citas encontradas: {}", citas.size());
            model.addAttribute("citas", citas);
            return "listarCitas";
        } catch (Exception e) {
            log.error("Error al listar citas: {}", e.getMessage());
            model.addAttribute("error", "Error al cargar las citas: " + e.getMessage());
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
                                 @ModelAttribute("cita") CitaDTO citaDTO,
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

    // SOLO UN método cancelarCita - ELIMINA EL DUPLICADO
    @PostMapping("/{idCita}/cancelar")
    public String cancelarCita(@PathVariable int idCita,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            String usuarioEmail = (String) session.getAttribute("usuarioEmail");
            log.info("=== CANCELANDO CITA {} PARA USUARIO: {} ===", idCita, usuarioEmail);

            if (usuarioEmail == null) {
                throw new Exception("Usuario no autenticado");
            }

            // Verificar que la cita pertenece al usuario
            CitaDTO cita = citaServicio.obtenerCita(idCita);
            if (!cita.getEmailCliente().equals(usuarioEmail)) {
                throw new Exception("No tienes permiso para cancelar esta cita");
            }

            // Actualizar estado a CANCELADA
            cita.setEstadoCita(EstadoCita.CANCELADA);
            citaServicio.actualizarCita(idCita, cita);

            log.info("✅ CITA CANCELADA EXITOSAMENTE");
            redirectAttributes.addFlashAttribute("success", "Cita cancelada exitosamente");

        } catch (Exception e) {
            log.error("❌ ERROR AL CANCELAR CITA: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al cancelar cita: " + e.getMessage());
        }

        return "redirect:/citas/historial"; // Redirige al historial
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
    public String historialCitas(Model model, HttpSession session) {
        // 1. Obtener el email del cliente de la sesión (establecido en /citas/nueva)
        String emailUsuario = (String) session.getAttribute("email");

        if (emailUsuario == null) {
            // Si no hay email, mostramos la vista con un error, pero no fallamos.
            model.addAttribute("error", "No se encontró un cliente asociado a la sesión. Por favor, inicie sesión o cree una cita.");
            model.addAttribute("citas", Collections.emptyList());
            return "historial";
        }

        try {
            // 2. Usar el email de la sesión para buscar las citas.
            List<CitaDTO> citas = citaServicio.listarCitasPorUsuarioEmail(emailUsuario);
            model.addAttribute("citas", citas);
        } catch (Exception e) {
            // 3. Si falla el servicio, capturamos la excepción y la mostramos.
            model.addAttribute("error", "Error al cargar las citas: " + e.getMessage());
            model.addAttribute("citas", Collections.emptyList());
        }

        return "historial";
    }
}