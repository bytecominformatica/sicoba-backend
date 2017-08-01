package br.com.clairtonluz.sicoba.repository.comercial;

import br.com.clairtonluz.sicoba.model.entity.comercial.Consumer;
import br.com.clairtonluz.sicoba.model.entity.comercial.StatusConsumer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by clairtonluz on 15/11/15.
 */

@Repository
public interface ConsumerRepository extends CrudRepository<Consumer, Integer> {

    String QUERY_CONSUMER_WITHOUT_TITULOS = "select c from Consumer c where c.status <> 2 " +
            "and (" +
            "(SELECT count(*) FROM Titulo t where t.consumer.id = c.id and t.dataVencimento > :date) " +
            "+ (SELECT count(*) FROM Charge t where t.consumer.id = c.id and t.status <> 'NEW' and (t.status <> 'CANCELED' or t.manualPayment = true) and t.expireAt > :date)" +
            ") < 2 " +
            "order by ( " +
            "(SELECT count(*) FROM Titulo t where t.consumer.id = c.id and t.dataVencimento > :date) + " +
            "(SELECT count(*) FROM Charge t where t.consumer.id = c.id and t.status <> 'NEW' and (t.status <> 'CANCELED' or t.manualPayment = true) and t.expireAt > :date) " +
            ")";

    List<Consumer> findByStatus(StatusConsumer status);

    Consumer findOptionalByEmail(String email);

    Consumer findOptionalByCpfCnpj(String cpfCnpj);

    Consumer findOptionalByRg(String rg);

    List<Consumer> findByUpdatedAtGreaterThan(Date data);

    List<Consumer> findByNameLike(String name);

    //    @Query("select c from Consumer c where c.status <> 2 and c.id not in(select DISTINCT(m.consumer.id) from Titulo m where m.dataVencimento > :date)")
    @Query(QUERY_CONSUMER_WITHOUT_TITULOS)
    List<Consumer> findBySemTitulosDepoisDe(@Param("date") Date date);

    List<Consumer> findByStatusAndUpdatedAtGreaterThanOrderByUpdatedAtDesc(StatusConsumer status, Date data);
}
