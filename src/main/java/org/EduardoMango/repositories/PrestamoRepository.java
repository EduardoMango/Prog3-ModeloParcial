package org.EduardoMango.repositories;

import org.EduardoMango.database.DatabaseConnection;
import org.EduardoMango.entities.PrestamoEntity;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrestamoRepository implements IRepository<PrestamoEntity> {

    private static PrestamoRepository instance;

    private PrestamoRepository() {}

    public static PrestamoRepository getInstance() {
        if (instance == null) {
            instance = new PrestamoRepository();
        }
        return instance;
    }

    @Override
    public Optional<PrestamoEntity> findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM prestamos WHERE ID = ?")) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PrestamoEntity p = PrestamoEntity.builder()
                            .id(rs.getInt("id"))
                            .usuario_id(rs.getInt("usuario_id"))
                            .libro_id(rs.getInt("libro_id"))
                            .fecha_prestamo(LocalDate.parse(rs.getString("fecha_prestamo")))
                            .build();
                    String fechaDevolucion = rs.getString("fecha_devolucion");
                    if (fechaDevolucion != null) p.setFecha_devolucion(LocalDate.parse(fechaDevolucion));

                    return Optional.of(p);
                }
                return Optional.empty();
            }
        }
    }

    public void returnPrestamo(int id) throws SQLException {

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE prestamos SET fecha_devolucion = ? WHERE ID = ?")) {

            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = currentDate.format(formatter);


            ps.setString(1, formattedDate);
            ps.setInt(2, id);

            ps.executeUpdate();

        }
    }



    public List<PrestamoEntity> findAllActiveByUsuario(int usuario_id)throws SQLException{
        List<PrestamoEntity> prestamos = new ArrayList<>();

        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM prestamos WHERE usuario_id = ?")){
            ps.setInt(1, usuario_id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    if(rs.getString("fecha_devolucion") == null){
                        prestamos.add(PrestamoEntity.builder()
                                .id(rs.getInt("id"))
                                .usuario_id(rs.getInt("usuario_id"))
                                .libro_id(rs.getInt("libro_id"))
                                .fecha_prestamo(LocalDate.parse(rs.getString("fecha_prestamo")))
                                .build());
                    }
                }
            }
        }
        return prestamos;
    }

    @Override
    public List<PrestamoEntity> findAll() throws SQLException {
        List<PrestamoEntity> prestamos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM prestamos")) {


            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {

                    PrestamoEntity p = PrestamoEntity.builder()
                            .id(rs.getInt("id"))
                            .usuario_id(rs.getInt("usuario_id"))
                            .libro_id(rs.getInt("libro_id"))
                            .fecha_prestamo(LocalDate.parse(rs.getString("fecha_prestamo")))
                            .build();

                    String fechaDevolucion = rs.getString("fecha_devolucion");
                    if (fechaDevolucion != null) p.setFecha_devolucion(LocalDate.parse(fechaDevolucion));
                    prestamos.add(p);
                }
            }
        }

        return prestamos;
    }

    @Override
    public void save(PrestamoEntity prestamoEntity) throws SQLException {
        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO prestamos (usuario_id, libro_id, fecha_devolucion) VALUES (?, ?, ?)")) {

            ps.setInt(1, prestamoEntity.getUsuario_id());
            ps.setInt(2, prestamoEntity.getLibro_id());
            //Fecha de devolucion se deja como nula, ya que no ha sido devuelto al crearse.
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM prestamos WHERE id = ?")){
            ps.setInt(1,id);
            ps.executeUpdate();
        }
    }
}
