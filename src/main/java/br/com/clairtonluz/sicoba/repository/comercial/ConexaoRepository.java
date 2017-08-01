package br.com.clairtonluz.sicoba.repository.comercial;

import br.com.clairtonluz.sicoba.model.entity.comercial.Consumer;
import br.com.clairtonluz.sicoba.model.entity.comercial.Conexao;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by clairtonluz on 15/11/15.
 */

public interface ConexaoRepository extends CrudRepository<Conexao, Integer> {

    Conexao findOptionalByConsumer(Consumer consumer);

    Conexao findOptionalByNome(String nome);

    Conexao findOptionalByIp(String ip);

    List<Conexao> findAllByOrderByNomeAsc();
}
