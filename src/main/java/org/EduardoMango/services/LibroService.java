package org.EduardoMango.services;

import lombok.Getter;
import org.EduardoMango.entities.LibroEntity;
import org.EduardoMango.entities.PrestamoEntity;
import org.EduardoMango.repositories.LibroRepository;
import org.EduardoMango.repositories.PrestamoRepository;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

public class LibroService implements IService<LibroEntity> {

    @Getter
    private static final LibroService instance = new LibroService();

    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;

    private LibroService() {
        libroRepository = LibroRepository.getInstance();
        prestamoRepository = PrestamoRepository.getInstance();
    }


    @Override
    public List<LibroEntity> findAll() {
        try {
            return libroRepository.findAll();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return List.of();
        }
    }

    /**
     * Obtiene todos los libros que tienen al menos una unidad disponible.
     * En caso de ocurrir una SQLException, imprime el mensaje de error
     * y devuelve una lista vacía.
     *
     * @return Una lista de objetos LibroEntity que tienen unidades disponibles.
     */
    public List<LibroEntity> findAllDisponible(){
        try {
            return libroRepository.findAll()
                    .stream()
                    .filter(l -> l.getUnidades_disponibles()>0)
                    .toList();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return List.of();
        }
    }

    @Override
    public LibroEntity findById(int id) {
        try {
            return libroRepository.findById(id).orElseThrow(NoSuchElementException::new);
        } catch (SQLException e) {
            throw new NoSuchElementException(e);
        }
    }

    @Override
    public void save(LibroEntity libroEntity) {
        try {
            libroRepository.save(libroEntity);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        try {
            libroRepository.delete(id);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Calcula el número total de unidades disponibles de todos los libros.
     * Este método obtiene todos los libros disponibles y suma sus unidades disponibles.
     *
     * @return El número total de unidades disponibles de todos los libros.
     */
    public long totalLibrosDisponibles() {
        return findAllDisponible()
                .stream()
                .mapToLong(LibroEntity::getUnidades_disponibles)
                .sum();
    }

    /**
     * Busca el libro que ha sido prestado el mayor número de veces.
     * Este método consulta todos los préstamos y todos los libros,
     * cuenta la cantidad de préstamos para cada libro y devuelve el libro
     * con el recuento máximo de préstamos.
     *
     * @return El libro que ha sido prestado el mayor número de veces.
     * @throws NoSuchElementException Si no se encuentra ningún libro en la base de datos
     * o si ocurre un error al acceder a la base de datos
     * para obtener los libros y préstamos.
     */
    public LibroEntity findByMaxPrestamos() {
        try {
            List<PrestamoEntity> prestamos = prestamoRepository.findAll();
            List<LibroEntity> libros = libroRepository.findAll();

            return libros.stream()
                    .max(Comparator.comparingLong(libro ->
                            prestamos.stream()
                                    .filter(p -> p.getLibro_id() == libro.getId())
                                    .count()
                    ))
                    .orElseThrow(() -> new NoSuchElementException("No se encontraron libros en la base de datos."));

        } catch (SQLException e) {
            throw new NoSuchElementException("Error al acceder a la base de datos para obtener los libros y préstamos.", e);
        }
    }
}
