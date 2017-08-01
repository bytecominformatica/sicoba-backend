package br.com.clairtonluz.sicoba.service.comercial;

import br.com.clairtonluz.sicoba.exception.ConflitException;
import br.com.clairtonluz.sicoba.model.entity.comercial.*;
import br.com.clairtonluz.sicoba.model.entity.financeiro.Titulo;
import br.com.clairtonluz.sicoba.repository.comercial.ConsumerRepository;
import br.com.clairtonluz.sicoba.repository.comercial.ConexaoRepository;
import br.com.clairtonluz.sicoba.repository.comercial.ContratoRepository;
import br.com.clairtonluz.sicoba.service.comercial.conexao.ConexaoService;
import br.com.clairtonluz.sicoba.service.financeiro.TituloService;
import br.com.clairtonluz.sicoba.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class ConsumerService {

    @Autowired
    private ConsumerRepository consumerRepository;
    @Autowired
    private ConexaoRepository conexaoRepository;
    @Autowired
    private TituloService tituloService;
    @Autowired
    private ContratoRepository contratoRepository;
    @Autowired
    private ConexaoService conexaoService;
    @Autowired
    private BairroService bairroService;


    public List<Consumer> buscarUltimosAlterados() {
        Date data = DateUtil.toDate(LocalDateTime.now());
        return consumerRepository.findByUpdatedAtGreaterThan(data);
    }

    public Consumer buscarPorId(Integer id) {
        return consumerRepository.findOne(id);
    }

    public boolean rgAvaliable(Consumer c) {
        Consumer consumer = null;
        if (c.getRg() != null) {
            consumer = consumerRepository.findOptionalByRg(c.getRg());
        }
        return consumer == null || consumer.getId().equals(c.getId());
    }

    public boolean cpfCnpjAvaliable(Consumer c) {
        Consumer consumer = consumerRepository.findOptionalByCpfCnpj(c.getCpfCnpj());
        return consumer == null || consumer.getId().equals(c.getId());
    }

    public boolean emailAvaliable(Consumer c) {
        Consumer consumer = null;
        if (c.getEmail() != null) {
            consumer = consumerRepository.findOptionalByEmail(c.getEmail());
        }
        return consumer == null || consumer.getId().equals(c.getId());
    }

    @Transactional
    public Consumer save(Consumer consumer) throws Exception {
        if (consumer.getEndereco().getBairro().getId() == null) {
            Bairro bairro = bairroService.buscarOuCriarBairro(consumer.getEndereco());
            consumer.getEndereco().setBairro(bairro);
        }

        if (isAvaliable(consumer)) {
            consumerRepository.save(consumer);
            Conexao conexao = conexaoService.buscarOptionalPorConsumer(consumer);

            if (conexao != null) {
                conexaoService.save(conexao);
            }

            if (consumer.getStatus().equals(StatusConsumer.CANCELADO)) {
                efetuarCancelamento(consumer);
            }
        }

        return consumer;
    }

    private void efetuarCancelamento(Consumer consumer) {
        Contrato contrato = contratoRepository.findOptionalByConsumer_id(consumer.getId());
        if (contrato != null) {
            contrato.setEquipamento(null);
            contrato.setEquipamentoWifi(null);
            contratoRepository.save(contrato);
        }
        List<Titulo> titulosNaoVencidos = tituloService.buscarNaoVencidosPorConsumer(consumer);

        titulosNaoVencidos.forEach(it -> {
            tituloService.remove(it.getId());
        });
    }

    public boolean isAvaliable(Consumer consumer) {
        if (!rgAvaliable(consumer)) {
            throw new ConflitException("RG já Cadastrado");
        } else if (!cpfCnpjAvaliable(consumer)) {
            throw new ConflitException("CPF já Cadastrado");
        } else if (!emailAvaliable(consumer)) {
            throw new ConflitException("E-Mail já Cadastrado");
        }
        return true;
    }

    public List<Consumer> buscarSemTitulo() {
        Date data = DateUtil.toDate(LocalDate.now());
        List<Consumer> consumers = consumerRepository.findBySemTitulosDepoisDe(data);
        return consumers;
    }

    public void ativar(Consumer consumer) {
        consumer.setStatus(StatusConsumer.ATIVO);
        consumerRepository.save(consumer);
        Conexao conexao = conexaoService.buscarOptionalPorConsumer(consumer);
        conexaoService.save(conexao);
    }

    public void inativar(Consumer consumer) {
        consumer.setStatus(StatusConsumer.INATIVO);
        consumerRepository.save(consumer);
        Conexao conexao = conexaoService.buscarOptionalPorConsumer(consumer);
        conexaoService.save(conexao);
    }

    public List<Consumer> query(String name, StatusConsumer status) {
        List<Consumer> result;
        if (name != null && !name.isEmpty()) {
            result = consumerRepository.findByNameLike("%" + name.toUpperCase() + "%");
        } else if (status != null) {
            result = consumerRepository.findByStatus(status);
        } else {
            result = (List<Consumer>) consumerRepository.findAll();
        }
        return result;
    }

    public Conexao buscarPorConsumer(Integer consumerId) {
        return conexaoRepository.findOptionalByConsumer(buscarPorId(consumerId));
    }

    public List<Consumer> buscarUltimosCancelados() {
        Date data = DateUtil.toDate(LocalDate.now().minusMonths(2));
        return consumerRepository.findByStatusAndUpdatedAtGreaterThanOrderByUpdatedAtDesc(StatusConsumer.CANCELADO, data);
    }
}
