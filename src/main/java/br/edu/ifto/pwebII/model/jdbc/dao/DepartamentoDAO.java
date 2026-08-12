package br.edu.ifto.pwebII.model.jdbc.dao;

import br.edu.ifto.pwebII.model.entity.Departamento;
import br.edu.ifto.pwebII.model.jdbc.conexao.MinhaConexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DepartamentoDAO {

    //criar um objeto Connection para receber a conexão
    Connection con;

    public DepartamentoDAO(){
        con = MinhaConexao.conexao();
    }

    public List<Departamento> buscarDepartamento() {
        try {
            //comando sql
            String sql = "select * from tb_departamento";
            PreparedStatement ps = con.prepareStatement(sql);
            //ResultSet, representa o resultado do comando SQL
            ResultSet rs = ps.executeQuery();
            //cria uma lista de departamentos para retornar
            List<Departamento> departamentos = new ArrayList();
            //laço para buscar todos os departamentos do banco
            while (rs.next()) {
                Departamento d = new Departamento();
                d.setId(rs.getLong("id"));
                d.setNome(rs.getString("nome"));
                //add departamento na lista
                departamentos.add(d);
            }
            //retorna a lista de departamentos
            return departamentos;
        } catch (SQLException ex) {
            Logger.getLogger(DepartamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean remove(Long id) {
        try {
            //comando sql
            String sql = "delete from tb_departamento where id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            //referênciar o parâmetro do método para a ?
            ps.setLong(1, id);
            if(ps.executeUpdate()==1)
                return true;

        } catch (SQLException ex) {
            Logger.getLogger(DepartamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean save(Departamento departamento) {
        try {
            //comando sql
            String sql = "insert into tb_departamento (nome) values (?)";
            PreparedStatement ps = con.prepareStatement(sql);
            //referênciar o parâmetro do método para a ?
            ps.setString(1, departamento.getNome());

            if(ps.executeUpdate()==1)
                return true;

        } catch (SQLException ex) {
            Logger.getLogger(DepartamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean update(Departamento departamento) {
        try {
            //comando sql
            String sql = "update tb_departamento set nome=? where id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            //referênciar o parâmetro do método para a ?
            ps.setString(1, departamento.getNome());
            ps.setLong(2, departamento.getId());

            if (ps.executeUpdate()==1)
                return true;

        } catch (SQLException ex) {
            Logger.getLogger(DepartamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Departamento buscarDepartamento(Long id) {
        try {
            //comando sql
            String sql = "select * from tb_departamento where id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            //referênciar o parâmetro do método para a ?
            ps.setLong(1, id);
            //ResultSet, representa o resultado do comando SQL
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Departamento d = new Departamento();
                d.setId(rs.getLong("id"));
                d.setNome(rs.getString("nome"));
                return d;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DepartamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int contarFuncionarios(Long departamentoId) {
        try {
            // Comando SQL para contar quantos funcionários estão associados ao departamento
            String sql = "select count(*) from tb_funcionario where departamento_id = ?"; // Ajuste o nome da tabela/coluna se necessário
            PreparedStatement ps = con.prepareStatement(sql);

            // Referenciar o parâmetro da consulta
            ps.setLong(1, departamentoId);

            // Executar a consulta
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1); // Retorna o valor da primeira coluna (a contagem)
            }
        } catch (SQLException ex) {
            Logger.getLogger(DepartamentoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }
}