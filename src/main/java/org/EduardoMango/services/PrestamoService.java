package org.EduardoMango.services;

import lombok.Getter;
import org.EduardoMango.entities.LibroEntity;
import org.EduardoMango.entities.PrestamoEntity;
import org.EduardoMango.entities.UsuarioEntity;
import org.EduardoMango.repositories.LibroRepository;
import org.EduardoMango.repositories.PrestamoRepository;
import org.EduardoMango.repositories.UsuarioRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class PrestamoService implements IService<PrestamoEntity> {

    @Getter
    private static final PrestamoService instance = new PrestamoService();
    private final PrestamoRepository prestamoRepository;
    private final LibroRepository libroRepository;
    private final UsuarioRepository usuarioRepository;

    private PrestamoService() {
        libroRepository = LibroRepository.getInstance();
        prestamoRepository = PrestamoRepository.getInstance();
        usuarioRepository = UsuarioRepository.getInstance();
    }

    @Override
    public List<PrestamoEntity> findAll() {
        try {
            return prestamoRepository.findAll();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return List.of();
        }
    }
    /**
     * Obtiene una lista de todos los préstamos que aún están activos,
     * es decir, aquellos que no tienen una fecha de devolución registrada.
     * <p>
     * Este método realiza los siguientes pasos:
     * <ol>
     * <li>Obtiene una lista de todos los préstamos registrados en el sistema.</li>
     * <li>Filtra esta lista, manteniendo solo aquellos préstamos donde el campo
     * 'fecha_devolucion' es nulo (indicando que aún no han sido devueltos).</li>
     * <li>Convierte el resultado del filtro en una nueva lista de entidades PrestamoEntity.</li>
     * </ol>
     * En caso de ocurrir una SQLException al intentar obtener todos los préstamos,
     * imprime el mensaje de error en la consola y devuelve una lista vacía.
     *
     * @return Una lista de objetos PrestamoEntity que representan los préstamos activos.
     */
    public List<PrestamoEntity> findAllActivos() {
        try {
           return prestamoRepository.findAll()
                   .stream()
                   .filter(p -> p.getFecha_devolucion() == null)
                   .toList();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return List.of();
        }
    }

    @Override
    public PrestamoEntity findById(int id) {
        try {
            return prestamoRepository.findById(id).orElseThrow(NoSuchElementException::new);
        } catch (SQLException e) {
            throw new NoSuchElementException(e.getMessage());
        }
    }

    @Override
    public void save(PrestamoEntity prestamoEntity) {
        try {
            LibroEntity prestado = libroRepository.findById(prestamoEntity.getLibro_id())
                    .orElseThrow(NoSuchElementException::new);

            if(prestado.getUnidades_disponibles()>0){
                prestado.setUnidades_disponibles(prestado.getUnidades_disponibles() - 1);
                libroRepository.updateStock(prestado);
                prestamoRepository.save(prestamoEntity);
            } else {
                System.out.println("El libro no está disponible");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Registra la devolución de un préstamo, actualizando el stock del libro
     * y marcando el préstamo como devuelto en la base de datos.
     * <p>
     * Este método realiza los siguientes pasos:
     * <ol>
     * <li>Busca el préstamo por su ID. Si no se encuentra, lanza una NoSuchElementException.</li>
     * <li>Busca el libro asociado al préstamo a través del ID del libro almacenado en la entidad del préstamo.
     * Si no se encuentra el libro, lanza una NoSuchElementException.</li>
     * <li>Incrementa la cantidad de unidades disponibles del libro prestado en uno.</li>
     * <li>Actualiza el stock del libro en la base de datos.</li>
     * <li>Marca el préstamo como devuelto en la base de datos, actualizando la fecha de devolución.</li>
     * </ol>
     * En caso de ocurrir una SQLException durante cualquiera de estas operaciones,
     * imprime el mensaje de error en la consola.
     *
     * @param id El ID del préstamo que se está devolviendo.
     * @throws NoSuchElementException Si no se encuentra el préstamo o el libro asociado.
     */
    public void returnPrestamo(int id) {
        try {


            PrestamoEntity prestamoEntity = prestamoRepository.findById(id)
                    .orElseThrow(NoSuchElementException::new);
            LibroEntity prestado = libroRepository.findById(prestamoEntity.getLibro_id())
                    .orElseThrow(NoSuchElementException::new);

            prestado.setUnidades_disponibles(prestado.getUnidades_disponibles() + 1);
            libroRepository.updateStock(prestado);

            prestamoRepository.returnPrestamo(id);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Calcula el promedio de préstamos por cada usuario que ha realizado al menos un préstamo.
     * <p>
     * Este método realiza los siguientes pasos:
     * <ol>
     * <li>Obtiene una lista de todos los préstamos registrados en el sistema.</li>
     * <li>Agrupa estos préstamos por el ID del usuario que los realizó y cuenta
     * cuántos préstamos tiene cada usuario. El resultado se almacena en un
     * mapa donde la clave es el ID del usuario y el valor es la cantidad de préstamos.</li>
     * <li>Calcula el total de préstamos sumando la cantidad de préstamos de todos los usuarios
     * que aparecen en el mapa (es decir, todos los usuarios que han realizado al menos un préstamo).</li>
     * <li>Determina la cantidad de usuarios distintos que tienen préstamos, que es el tamaño del mapa
     * de préstamos por usuario.</li>
     * <li>Finalmente, si hay al menos un usuario con préstamos, calcula el promedio dividiendo el
     * total de préstamos entre la cantidad de usuarios con préstamos. Si no hay usuarios
     * con préstamos, devuelve 0.0.</li>
     * </ol>
     *
     * @return El promedio de préstamos por usuario que ha realizado al menos un préstamo.
     * Devuelve 0.0 si no hay ningún usuario con préstamos.
     */
    public double promedioPrestamoPorUsuarioConPrestamos(){
        List<PrestamoEntity> prestamos = findAll();

        Map<Integer, Long> prestamosPorUsuario = prestamos.stream()
                .collect(Collectors.groupingBy(
                        PrestamoEntity::getUsuario_id,
                        Collectors.counting()
                ));

        long totalPrestamos = prestamosPorUsuario.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        int cantidadUsuariosConPrestamos = prestamosPorUsuario.size();

        if (cantidadUsuariosConPrestamos == 0) {
            return 0.0;
        }

        return (double) totalPrestamos / cantidadUsuariosConPrestamos;
    }

    @Override
    public void delete(int id) {
        try {
            prestamoRepository.delete(id);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
