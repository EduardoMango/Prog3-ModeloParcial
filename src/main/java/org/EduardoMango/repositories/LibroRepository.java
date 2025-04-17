package org.EduardoMango.repositories;

import org.EduardoMango.database.DatabaseConnection;
import org.EduardoMango.entities.LibroEntity;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibroRepository implements IRepository<LibroEntity> {

    private static LibroRepository instance;

    private LibroRepository() {}

    public static LibroRepository getInstance() {
        if (instance == null) {
            instance = new LibroRepository();
        }
        return instance;
    }

    public void updateStock(LibroEntity libroEntity) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE libros SET unidades_disponibles = ? WHERE id = ?")) {
            ps.setInt(1, libroEntity.getUnidades_disponibles());
            ps.setInt(2, libroEntity.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public Optional<LibroEntity> findById(int id) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM libros WHERE ID = ?")) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(LibroEntity.builder()
                            .id(rs.getInt("id"))
                            .titulo(rs.getString("titulo"))
                            .autor(rs.getString("autor"))
                            .anio_publicacion(rs.getInt("anio_publicacion"))
                            .unidades_disponibles(rs.getInt("unidades_disponibles"))
                            .build());
                }
                return Optional.empty();
            }
        }
    }

    @Override
    public List<LibroEntity> findAll() throws SQLException {
        List<LibroEntity> libros = new ArrayList<>();

        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM libros");
        ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                libros.add(LibroEntity.builder()
                        .id(rs.getInt("id"))
                        .titulo(rs.getString("titulo"))
                        .autor(rs.getString("autor"))
                        .anio_publicacion(rs.getInt("anio_publicacion"))
                        .unidades_disponibles(rs.getInt("unidades_disponibles"))
                        .build());
            }
        }
        return libros;
    }

    @Override
    public void save(LibroEntity libroEntity) throws SQLException {
        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("INSERT INTO libros (titulo, autor, anio_publicacion, unidades_disponibles) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, libroEntity.getTitulo());
            ps.setString(2, libroEntity.getAutor());
            ps.setInt(3, libroEntity.getAnio_publicacion());
            ps.setInt(4, libroEntity.getUnidades_disponibles());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException  {
        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("DELETE FROM libros WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
