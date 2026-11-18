package br.edu.ifto.pwebII.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_administrador")
public class Administrador extends PessoaFisica {
}
