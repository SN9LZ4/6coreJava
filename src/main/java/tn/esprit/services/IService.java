package tn.esprit.services;

import java.util.List;

public interface IService<T> {

    void add(T t) throws ServiceException;
    void edit(T t) throws ServiceException;
    void remove(T t) throws ServiceException;
    List<T> recuperer() throws ServiceException;
}
