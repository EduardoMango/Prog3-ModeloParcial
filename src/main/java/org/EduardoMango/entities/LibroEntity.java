package org.EduardoMango.entities;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LibroEntity {

    private int id;
    private String titulo;
    private String autor;
    private Integer anio_publicacion;
    private Integer unidades_disponibles;
}
