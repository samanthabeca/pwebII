package br.edu.ifto.pwebII.model.jdbc.dao;

import br.edu.ifto.pwebII.model.entity.Departamento;
import br.edu.ifto.pwebII.model.entity.Funcionario;
import br.edu.ifto.pwebII.model.jdbc.conexao.MinhaConexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FuncionarioDAO {

    Connection con;

    public FuncionarioDAO(){
        con = MinhaConexao.conexao();
    }

    public List<Funcionario> buscarFuncionario() {
        try {
            // Consulta SQL com INNER JOIN para carregar os dados do Departamento
            String sql = "SELECT f.id AS f_id, f.nome AS f_nome, f.salario AS f_salario, " +
                    "d.id AS d_id, d.nome AS d_nome " +
                    "FROM tb_funcionario f " +
                    "INNER JOIN tb_departamento d ON f.departamento_id = d.id";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            List<Funcionario> funcionarios = new ArrayList<>();

            while (rs.next()) {
                Funcionario f = new Funcionario();
                f.setId(rs.getLong("f_id"));
                f.setNome(rs.getString("f_nome"));
                f.setSalario(rs.getDouble("f_salario"));

                // Instancia e popula o objeto Departamento
                Departamento d = new Departamento();
                d.setId(rs.getLong("d_id"));
                d.setNome(rs.getString("d_nome"));

                // Associa o Departamento ao Funcionario
                f.setDepartamento(d);

                funcionarios.add(f);
            }
            return funcionarios;
        } catch (SQLException ex) {
            Logger.getLogger(FuncionarioDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean remove(Long id) {
        try {
            String sql = "delete from tb_funcionario where id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, id);
            if(ps.executeUpdate() == 1)
                return true;

        } catch (SQLException ex) {
            Logger.getLogger(FuncionarioDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean save(Funcionario funcionario) {
        try {
            String sql = "insert into tb_funcionario (salario, departamento_id, nome) values (?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setDouble(1, funcionario.getSalario());

            // Verifica se o objeto departamento e o ID estão presentes
            if (funcionario.getDepartamento() != null && funcionario.getDepartamento().getId() != null) {
                ps.setLong(2, funcionario.getDepartamento().getId());
            } else {
                ps.setNull(2, java.sql.Types.BIGINT);
            }

            ps.setString(3, funcionario.getNome());

            if (ps.executeUpdate() == 1)
                return true;

        } catch (SQLException ex) {
            Logger.getLogger(FuncionarioDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean update(Funcionario funcionario) {
        try {
            String sql = "update tb_funcionario set salario=?, departamento_id=?, nome=? where id=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setDouble(1, funcionario.getSalario());

            // Trata o relacionamento de departamento evitando NullPointerException
            if (funcionario.getDepartamento() != null && funcionario.getDepartamento().getId() != null) {
                ps.setLong(2, funcionario.getDepartamento().getId());
            } else {
                ps.setNull(2, java.sql.Types.BIGINT);
            }

            ps.setString(3, funcionario.getNome());
            ps.setLong(4, funcionario.getId()); // ID do funcionário na cláusula WHERE

            if (ps.executeUpdate() == 1) {
                return true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(FuncionarioDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public Funcionario buscarFuncionario(Long id) {
        try {
            // Consulta por ID também faz o JOIN para trazer as informações do departamento
            String sql = "SELECT f.id AS f_id, f.nome AS f_nome, f.salario AS f_salario, " +
                    "d.id AS d_id, d.nome AS d_nome " +
                    "FROM tb_funcionario f " +
                    "INNER JOIN tb_departamento d ON f.departamento_id = d.id " +
                    "WHERE f.id = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Funcionario f = new Funcionario();
                f.setId(rs.getLong("f_id"));
                f.setNome(rs.getString("f_nome"));
                f.setSalario(rs.getDouble("f_salario"));

                Departamento d = new Departamento();
                d.setId(rs.getLong("d_id"));
                d.setNome(rs.getString("d_nome"));

                f.setDepartamento(d);

                return f;
            }
        } catch (SQLException ex) {
            Logger.getLogger(FuncionarioDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}