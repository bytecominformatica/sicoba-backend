package br.com.clairtonluz.sicoba.repository.financeiro;

import br.com.clairtonluz.sicoba.model.entity.comercial.Consumer;
import br.com.clairtonluz.sicoba.model.entity.comercial.StatusConsumer;
import br.com.clairtonluz.sicoba.model.entity.financeiro.StatusTitulo;
import br.com.clairtonluz.sicoba.model.entity.financeiro.Titulo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by clairtonluz on 09/01/16.
 */

@Repository
public interface TituloRepository extends CrudRepository<Titulo, Integer> {

    List<Titulo> findByConsumer_idOrderByDataVencimentoDesc(Integer consumerId);

    List<Titulo> findByNumeroBoletoBetween(Integer inicio, Integer fim);

    List<Titulo> findByStatusAndDataVencimentoLessThanAndConsumer_statusNotOrderByDataVencimentoAsc(StatusTitulo statusTitulo, Date date, StatusConsumer statusConsumer);

    Titulo findOptionalByNumeroBoleto(Integer numeroBoleto);

    List<Titulo> findByDataOcorrenciaBetween(Date inicio, Date fim);

    List<Titulo> findByDataVencimentoBetween(Date inicio, Date fim);

    List<Titulo> findByDataOcorrenciaBetweenAndStatus(Date inicio, Date fim, StatusTitulo status);

    List<Titulo> findByDataVencimentoBetweenAndStatus(Date inicio, Date fim, StatusTitulo status);

    List<Titulo> findByConsumerAndStatusAndDataVencimentoGreaterThan(Consumer consumer, StatusTitulo status, Date date);
}
