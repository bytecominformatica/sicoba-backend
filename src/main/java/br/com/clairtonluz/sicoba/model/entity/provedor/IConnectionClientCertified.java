package br.com.clairtonluz.sicoba.model.entity.provedor;

public interface IConnectionClientCertified extends IConnectionClient {

    String getLogin();

    String getPass();

    boolean isDisabled();
}
