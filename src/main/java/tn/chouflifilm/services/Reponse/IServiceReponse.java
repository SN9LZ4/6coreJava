package tn.chouflifilm.services.Reponse;

import tn.chouflifilm.entities.Reponse;

public interface IServiceReponse<T> {
    public Reponse getReponse(T t);
}
