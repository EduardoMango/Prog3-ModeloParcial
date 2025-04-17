package org.EduardoMango.repositories;

import org.EduardoMango.entities.UsuarioEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface IRepository<T> {

    Optional<T> findById(int id) throws SQLException;

    List<T> findAll() throws SQLException;

    void save(T t) throws SQLException;

    void delete(int id) throws SQLException;
}
