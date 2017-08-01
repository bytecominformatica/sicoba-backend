package br.com.clairtonluz.sicoba.api.comercial;

import br.com.clairtonluz.sicoba.model.entity.comercial.Conexao;
import br.com.clairtonluz.sicoba.model.entity.comercial.Consumer;
import br.com.clairtonluz.sicoba.model.entity.comercial.StatusConsumer;
import br.com.clairtonluz.sicoba.service.comercial.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by clairtonluz on 19/12/15.
 */
@RestController
@RequestMapping("api/consumers")
public class ConsumerAPI {

    @Autowired
    private ConsumerService consumerService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Consumer getPorId(@PathVariable Integer id) {
        return consumerService.buscarPorId(id);
    }


    @RequestMapping(method = RequestMethod.POST)
    public Consumer save(@Valid @RequestBody Consumer consumer) throws Exception {
        return consumerService.save(consumer);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public Consumer update(@Valid @RequestBody Consumer consumer) throws Exception {
        return consumerService.save(consumer);
    }

    @RequestMapping(value = "/{id}/conexao", method = RequestMethod.GET)
    public Conexao getConexao(@PathVariable Integer id) {
        return consumerService.buscarPorConsumer(id);
    }

    @RequestMapping(value = "/sem_titulo", method = RequestMethod.GET)
    public List<Consumer> getSemTitulo() {
        return consumerService.buscarSemTitulo();
    }

    @RequestMapping(value = "/ultimos_alterados", method = RequestMethod.GET)
    public List<Consumer> getUltimosAlterados() {
        return consumerService.buscarUltimosAlterados();
    }


    @RequestMapping(value = "/ultimos_cancelados", method = RequestMethod.GET)
    public List<Consumer> getUltimosCancelados() {
        return consumerService.buscarUltimosCancelados();
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Consumer> query(@RequestParam(value = "name", required = false) String name,
                                @RequestParam(value = "status", required = false) StatusConsumer status) {

        return consumerService.query(name, status);
    }

}
