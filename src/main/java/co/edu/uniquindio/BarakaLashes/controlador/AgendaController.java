package co.edu.uniquindio.BarakaLashes.controlador;

import co.edu.uniquindio.BarakaLashes.DTO.CitaDTO;
import co.edu.uniquindio.BarakaLashes.modelo.EstadoCita;
import co.edu.uniquindio.BarakaLashes.modelo.Servicio;
import co.edu.uniquindio.BarakaLashes.servicio.CitaServicio;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/agenda")
@RequiredArgsConstructor
public class AgendaController {

    private final CitaServicio citaServicio;

    @GetMapping
    public String mostrarAgenda(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String servicio,
            Model model) {

        try {
            log.info("=== ACCESO A AGENDA ===");

            // Establecer rango de fechas por defecto (mes actual)
            LocalDateTime inicio = fechaInicio != null
                    ? LocalDate.parse(fechaInicio).atStartOfDay()
                    : LocalDate.now().withDayOfMonth(1).atStartOfDay();

            LocalDateTime fin = fechaFin != null
                    ? LocalDate.parse(fechaFin).atTime(LocalTime.MAX)
                    : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(LocalTime.MAX);

            log.info("Rango de fechas: {} - {}", inicio, fin);

            // Obtener citas con filtros
            List<CitaDTO> citas;

            if (cliente != null && !cliente.isEmpty()) {
                citas = citaServicio.buscarCitasPorCliente(cliente);
            } else if (servicio != null && !servicio.isEmpty()) {
                citas = citaServicio.buscarCitasPorServicio(Servicio.valueOf(servicio));
            } else if (estado != null && !estado.isEmpty()) {
                citas = citaServicio.obtenerCitasConFiltros(inicio, fin, null, EstadoCita.valueOf(estado));
            } else {
                citas = citaServicio.obtenerCitasPorRangoFechas(inicio, fin);
            }

            log.info("Citas encontradas: {}", citas.size());

            // Calcular estadísticas
            long totalCitas = citas.size();
            long citasPendientes = citas.stream().filter(c -> c.getEstadoCita() == EstadoCita.PENDIENTE).count();
            long citasConfirmadas = citas.stream().filter(c -> c.getEstadoCita() == EstadoCita.CONFIRMADA).count();

            // Convertir citas a JSON para el calendario
            ObjectMapper mapper = new ObjectMapper();
            mapper.findAndRegisterModules(); // Para LocalDateTime
            String citasJson = mapper.writeValueAsString(citas);

            // Pasar datos al modelo
            model.addAttribute("citas", citas);
            model.addAttribute("citasJson", citasJson);
            model.addAttribute("totalCitas", totalCitas);
            model.addAttribute("citasPendientes", citasPendientes);
            model.addAttribute("citasConfirmadas", citasConfirmadas);
            model.addAttribute("fechaInicio", fechaInicio != null ? fechaInicio : inicio.toLocalDate().toString());
            model.addAttribute("fechaFin", fechaFin != null ? fechaFin : fin.toLocalDate().toString());
            model.addAttribute("cliente", cliente);
            model.addAttribute("estadoSeleccionado", estado);
            model.addAttribute("servicioSeleccionado", servicio);

            return "agendaCitas";

        } catch (Exception e) {
            log.error(" Error al cargar agenda: {}", e.getMessage(), e);
            model.addAttribute("error", "Error al cargar la agenda: " + e.getMessage());
            model.addAttribute("citas", List.of());
            return "agendaCitas";
        }
    }
}