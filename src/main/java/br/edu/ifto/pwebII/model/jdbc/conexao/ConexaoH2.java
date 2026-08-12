package br.edu.ifto.pwebII.model.jdbc.conexao;

//public class ConexaoH2 {
//    /*
//    * Criar essa classe de conexão usando o exemplo de ConexaoPostgre ou ConexaoMysql
//    *
//    * Implementar conforme material da aula, usando informações do application.properties
//    * URL, DRIVER, USER, PASSWORD
//    * */
//}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexaoH2 implements ConexaoJDBC{

    public static void main(String[] args) {

        //testar conexão
        System.out.println(new ConexaoH2().criarConexao());

    }

    /**
     * método que vai retornar uma conexão
     * @return
     */
    @Override
    public Connection criarConexao(){
        try {
            //carregar o driver de conexão
            Class.forName("org.h2.Driver");
            //parâmetros
            String url = "jdbc:h2:mem:dbname";
            String usuario = "admin";
            String senha = "123456";
            //retorna a conexão com o banco de dados
            return DriverManager.getConnection(url, usuario, senha);

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ConexaoH2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}