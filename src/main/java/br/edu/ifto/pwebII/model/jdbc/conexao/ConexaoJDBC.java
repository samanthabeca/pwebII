package br.edu.ifto.pwebII.model.jdbc.conexao;

import java.sql.Connection;

public interface ConexaoJDBC{

    public Connection criarConexao();

}