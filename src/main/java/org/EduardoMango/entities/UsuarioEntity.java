package org.EduardoMango.entities;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class UsuarioEntity {
    
    private int id;
    private String nombre;
    private String email;
}
