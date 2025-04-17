package org.EduardoMango.services;

import lombok.Getter;
import org.EduardoMango.entities.PrestamoEntity;
import org.EduardoMango.entities.UsuarioEntity;
import org.EduardoMango.repositories.PrestamoRepository;
import org.EduardoMango.repositories.UsuarioRepository;

import java.sql.SQLException;
import java.util.*;

public class UsuarioService implements IService<UsuarioEntity>{

    @Getter
    private static final UsuarioService instance = new UsuarioService();
    private final UsuarioRepository usuarioRepository;
    private final PrestamoRepository prestamoRepository;

    private UsuarioService() {
        this.usuarioRepository = UsuarioRepository.getInstance();
        this.prestamoRepository = PrestamoRepository.getInstance();
    }

    @Override
    public List<UsuarioEntity> findAll() {
        try {
            return usuarioRepository.findAll();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return List.of();
    }

    /**
     * Busca el usuario que tiene la mayor cantidad de préstamos registrados.
     * <p>
     * Este método realiza los siguientes pasos:
     * <ol>
     * <li>Obtiene una lista de todos los préstamos registrados en el sistema.</li>
     * <li>Obtiene una lista de todos los usuarios registrados en el sistema.</li>
     * <li>Para cada usuario, cuenta la cantidad de préstamos asociados a su ID.</li>
     * <li>Compara a los usuarios basándose en la cantidad de préstamos que tienen.</li>
     * <li>Devuelve el usuario con la mayor cantidad de préstamos.</li>
     * </ol>
     *
     * @return El usuario que tiene la mayor cantidad de préstamos registrados.
     * @throws NoSuchElementException Si no se encuentra ningún usuario en la base de datos
     * o si ocurre un error al acceder a la base de datos para obtener los préstamos y usuarios.
     */
    public UsuarioEntity findByMaxPrestamos() {
        try {
            List<PrestamoEntity> prestamos = prestamoRepository.findAll();
            List<UsuarioEntity> usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .max(Comparator.comparingLong(usuario ->
                        prestamos.stream()
                                .filter(p -> p.getUsuario_id() == usuario.getId())
                                .count()
                ))
                .orElseThrow(() -> new NoSuchElementException("No se encontraron libros en la base de datos."));
            } catch (SQLException e) {
            throw new NoSuchElementException("Error al acceder a la base de datos para obtener los libros y préstamos.", e);
        }
        }


    /**
     * Verifica si un usuario tiene menos del máximo permitido de préstamos activos.
     * El máximo permitido de préstamos activos se considera 5.
     * <p>
     * Este método realiza los siguientes pasos:
     * <ol>
     * <li>Consulta la base de datos para obtener todos los préstamos activos asociados a un usuario específico (aquellos sin fecha de devolución).</li>
     * <li>Cuenta la cantidad de préstamos activos encontrados para ese usuario.</li>
     * <li>Compara la cantidad de préstamos activos con el límite máximo (5).</li>
     * <li>Devuelve `true` si la cantidad de préstamos activos es menor que el límite, y `false` en caso contrario.</li>
     * </ol>
     *
     * @param idUsuario El ID del usuario a verificar.
     * @return `true` si el usuario tiene menos de 5 préstamos activos, `false` en caso contrario.
     * @throws RuntimeException Si ocurre un error al acceder a la base de datos.
     */
    public boolean isBelowMaxPrestamos(int idUsuario){
        try {
            int activos = prestamoRepository.findAllActiveByUsuario(idUsuario)
                    .size();

            return activos < 5;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Obtiene una lista de todos los usuarios que tienen al menos un préstamo activo.
     * <p>
     * Este método realiza los siguientes pasos:
     * <ol>
     * <li>Obtiene una lista de todos los usuarios registrados en el sistema.</li>
     * <li>Para cada usuario, consulta la base de datos para verificar si tiene algún préstamo activo (sin fecha de devolución).</li>
     * <li>Filtra la lista de usuarios, manteniendo solo aquellos para los cuales se encontraron préstamos activos.</li>
     * <li>Devuelve una lista de los usuarios que tienen préstamos activos.</li>
     * </ol>
     *
     * @return Una lista de objetos UsuarioEntity que tienen al menos un préstamo activo.
     * Devuelve una lista vacía si no hay usuarios con préstamos activos o si ocurre un error al acceder a la base de datos.
     */
    public List<UsuarioEntity> findAllConPrestamosActivos() {
        try {
            List<UsuarioEntity> usuarios = usuarioRepository.findAll();

            return usuarios.stream()
                    .filter(u -> {
                        try {
                            return !prestamoRepository.findAllActiveByUsuario(u.getId()).isEmpty();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return List.of();
    }

    @Override
    public UsuarioEntity findById(int id) {

        try {
            return usuarioRepository.findById(id).orElseThrow(NoSuchElementException::new);
        } catch (SQLException e) {
            throw new NoSuchElementException(e.getMessage());
        }
    }

    @Override
    public void save(UsuarioEntity user) {
        try {
            usuarioRepository.save(user);
            System.out.println("Usuario guardado correctamente");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            usuarioRepository.delete(id);
            System.out.println("Usuario eliminado correctamente");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
