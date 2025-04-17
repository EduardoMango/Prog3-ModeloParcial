package org.EduardoMango.repositories;

import org.EduardoMango.database.DatabaseConnection;
import org.EduardoMango.entities.UsuarioEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioRepository implements IRepository<UsuarioEntity> {

    private static UsuarioRepository instance;

    private UsuarioRepository() {}

    public static UsuarioRepository getInstance() {
        if (instance == null) {
            instance = new UsuarioRepository();
        }
        return instance;
    }
    @Override
    public Optional<UsuarioEntity> findById(int id) throws SQLException {
        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT * FROM usuarios WHERE ID = ?")){
            ps.setInt(1,id);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return Optional.of(new UsuarioEntity(rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("email")));
                }
            }
        }
        return Optional.empty();
    }
    @Override
    public List<UsuarioEntity> findAll() throws SQLException {
        List<UsuarioEntity> users = new ArrayList<>();
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement st = conn.prepareStatement("SELECT * FROM usuarios")){

            try(ResultSet rs = st.executeQuery()){
                while(rs.next()){
                    users.add(new UsuarioEntity(rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("email")));
                }
            }
        }

        return users;
    }

    @Override
    public void save(UsuarioEntity usuario) throws SQLException {
        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("INSERT INTO usuarios (nombre,email) VALUES (?, ?)")){

            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getEmail());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement("DELETE FROM usuarios WHERE id = ?")){
            ps.setInt(1,id);
            ps.executeUpdate();
        }
    }
}
