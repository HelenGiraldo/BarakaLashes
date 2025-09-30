package co.edu.uniquindio.BarakaLashes.modelo;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "Usuario")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "listaCitas")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String apellido;

    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 15)
    private String telefono;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> listaCitas;


    @ManyToOne
    @JoinColumn(name = "negocio_id")
    private Negocio negocio;
}
