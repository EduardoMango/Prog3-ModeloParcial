package org.EduardoMango.services;

import java.util.List;

public interface IService <T>{

    List<T> findAll();

    T findById(int id);

    void save(T t);

    void delete(int id);

}
