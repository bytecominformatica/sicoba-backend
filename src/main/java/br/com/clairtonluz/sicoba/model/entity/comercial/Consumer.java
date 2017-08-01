package br.com.clairtonluz.sicoba.model.entity.comercial;

import br.com.clairtonluz.sicoba.model.entity.extra.BaseEntity;
import br.com.clairtonluz.sicoba.util.StringUtil;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author clairton
 */
@Entity
@Table(name = "cliente")
public class Consumer extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "cliente_id_seq")
    @SequenceGenerator(name = "cliente_id_seq", sequenceName = "cliente_id_seq")
    private Integer id;
    @Column(name = "nome")
    @NotNull(message = "name é obrigatório")
    private String name;
    private String rg;
    @Enumerated
    private StatusConsumer status;

    @Column(name = "cpf_cnpj")
    private String cpfCnpj;
    @Column(name = "dt_nascimento")
    @Temporal(TemporalType.TIMESTAMP)
    private Date birthday;
    @Email(message = "Email inválido")
    private String email;
    @NotNull(message = "Fone titular é obrigatório")
    @Column(name = "fone_titular")
    private String foneTitular;
    private String contato;
    @Column(name = "fone_contato")
    private String foneContato;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id")
    private Endereco endereco;

    public Consumer() {
        this.createdAt = new Date();
        this.endereco = new Endereco();
        this.status = StatusConsumer.ATIVO;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.toUpperCase() : name;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg != null && rg.isEmpty() ? null : rg;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email != null && email.isEmpty() ? null : email;
    }

    public String getFoneTitular() {
        return foneTitular;
    }

    public void setFoneTitular(String foneTitular) {
        this.foneTitular = StringUtil.removerFormatacaoFone(foneTitular);
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato != null ? contato.toUpperCase() : contato;
    }

    public String getFoneContato() {
        return foneContato;
    }

    public void setFoneContato(String foneContato) {
        this.foneContato = StringUtil.removerFormatacaoFone(foneContato);
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        cpfCnpj = StringUtil.removerFormatacaoCpfCnpj(cpfCnpj);
        this.cpfCnpj = cpfCnpj != null && cpfCnpj.isEmpty() ? null : cpfCnpj;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public StatusConsumer getStatus() {
        return status;
    }

    public void setStatus(StatusConsumer status) {
        this.status = status;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
