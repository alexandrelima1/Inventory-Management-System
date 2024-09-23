package estoque.service;

import estoque.config.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovimentacaoService {

    public List<String> getEquipamentos(boolean emEstoque) throws SQLException {
        String sql = "SELECT nome FROM equipamentos WHERE em_estoque = ?";
        List<String> equipamentos = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setBoolean(1, emEstoque);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                equipamentos.add(rs.getString("nome"));
            }
        }

        return equipamentos;
    }

    public Map<String, Integer> getFuncionariosByCargo(String cargo) throws SQLException {
        String sql = "SELECT id, nome FROM funcionario WHERE cargo = ?";
        Map<String, Integer> funcionarios = new HashMap<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, cargo);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                funcionarios.put(rs.getString("nome"), rs.getInt("id"));
            }
        }

        return funcionarios;
    }

    public List<Object[]> getEstoqueDisponivel() throws SQLException {
        String sql = "SELECT nome, descricao, em_estoque FROM equipamentos";
        List<Object[]> estoque = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                estoque.add(new Object[]{
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getBoolean("em_estoque") ? "Sim" : "Não"
                });
            }
        }
        return estoque;
    }

    public List<Object[]> getMovimentacoes() throws SQLException {
        String sql = "SELECT e.nome AS equipamento_nome, m.tipo, m.fga, f.nome AS funcionario_nome, " +
                "m.data_emprestimo, m.data_retorno, m.usuario, m.descricao " +
                "FROM movimentacoes m " +
                "JOIN equipamentos e ON m.equipamento_id = e.id " +
                "JOIN funcionario f ON m.conex_funcionario_id = f.id";
        List<Object[]> movimentacoes = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                movimentacoes.add(new Object[]{
                        rs.getString("equipamento_nome"),
                        rs.getString("tipo"),
                        rs.getInt("fga"),
                        rs.getString("funcionario_nome"),
                        rs.getTimestamp("data_emprestimo"),
                        rs.getTimestamp("data_retorno"),
                        rs.getString("usuario"),
                        rs.getString("descricao")
                });
            }
        }
        return movimentacoes;
    }

    public LocalDateTime buscarDataEmprestimo(int equipamentoId) throws SQLException {
        String sql = "SELECT data_emprestimo FROM movimentacoes WHERE equipamento_id = ? AND tipo = 'EMPRESTIMO' ORDER BY data_emprestimo DESC LIMIT 1";
        LocalDateTime dataEmprestimo = null;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setInt(1, equipamentoId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                dataEmprestimo = rs.getTimestamp("data_emprestimo").toLocalDateTime();
            }
        }

        return dataEmprestimo;
    }

    public void realizarEmprestimo(int equipamentoId, int conexFuncionarioId, String usuario, LocalDateTime dataEmprestimo, LocalDateTime dataRetorno, int fga, String descricao) throws SQLException {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            String sqlMovimentacao = "INSERT INTO movimentacoes (equipamento_id, tipo, data_movimentacao, fga, conex_funcionario_id, data_emprestimo, data_retorno, usuario, descricao) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(sqlMovimentacao);
            pstmt.setInt(1, equipamentoId);
            pstmt.setString(2, "EMPRESTIMO");
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(4, fga);
            pstmt.setInt(5, conexFuncionarioId);
            pstmt.setTimestamp(6, Timestamp.valueOf(dataEmprestimo));
            pstmt.setTimestamp(7, Timestamp.valueOf(dataRetorno));
            pstmt.setString(8, usuario);
            pstmt.setString(9, descricao);
            pstmt.executeUpdate();

            String sqlUpdateEstoque = "UPDATE equipamentos SET em_estoque = false WHERE id = ?";
            pstmt = connection.prepareStatement(sqlUpdateEstoque);
            pstmt.setInt(1, equipamentoId);
            pstmt.executeUpdate();

            connection.commit();
        } catch (SQLException ex) {
            if (connection != null) {
                connection.rollback();
            }
            throw ex;
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    public void realizarRetorno(int equipamentoId, int conexFuncionarioId, String usuario, LocalDateTime dataEmprestimo, LocalDateTime dataRetorno, int fga, String descricao) throws SQLException {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            String sqlMovimentacao = "INSERT INTO movimentacoes (equipamento_id, tipo, data_movimentacao, fga, conex_funcionario_id, data_emprestimo, data_retorno, usuario, descricao) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(sqlMovimentacao);
            pstmt.setInt(1, equipamentoId);
            pstmt.setString(2, "RETORNO");
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now())); 
            pstmt.setInt(4, fga);
            pstmt.setInt(5, conexFuncionarioId);
            pstmt.setTimestamp(6, Timestamp.valueOf(dataEmprestimo)); 
            pstmt.setTimestamp(7, Timestamp.valueOf(dataRetorno)); 
            pstmt.setString(8, usuario);
            pstmt.setString(9, descricao); 
            pstmt.executeUpdate();

            String sqlUpdateEstoque = "UPDATE equipamentos SET em_estoque = true WHERE id = ?";
            pstmt = connection.prepareStatement(sqlUpdateEstoque);
            pstmt.setInt(1, equipamentoId);
            pstmt.executeUpdate();

            connection.commit(); 
        } catch (SQLException ex) {
            if (connection != null) {
                connection.rollback(); 
            }
            throw ex;
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }


    public void realizarBaixa(int equipamentoId, int conexFuncionarioId, String usuario, int fga, String descricao, LocalDate dataEmprestimo, LocalDate dataRetorno) throws SQLException {
        Connection connection = null;
        PreparedStatement pstmt = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            String sqlMovimentacao = "INSERT INTO movimentacoes (equipamento_id, tipo, data_movimentacao, fga, conex_funcionario_id, descricao, data_emprestimo, data_retorno, usuario) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = connection.prepareStatement(sqlMovimentacao);
            pstmt.setInt(1, equipamentoId);
            pstmt.setString(2, "BAIXA");
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(4, fga);
            pstmt.setInt(5, conexFuncionarioId);
            pstmt.setString(6, descricao);
            pstmt.setDate(7, Date.valueOf(dataEmprestimo));
            pstmt.setDate(8, Date.valueOf(dataRetorno));
            pstmt.setString(9, usuario);
            pstmt.executeUpdate();

            String sqlUpdateEstoque = "UPDATE equipamentos SET em_estoque = false WHERE id = ?";
            pstmt = connection.prepareStatement(sqlUpdateEstoque);
            pstmt.setString(1, descricao);
            pstmt.setInt(2, equipamentoId);
            pstmt.executeUpdate();

            connection.commit();
        } catch (SQLException ex) {
            if (connection != null) {
                connection.rollback();
            }
            throw ex;
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    public int buscarIdProdutoPorNome(String nome) throws SQLException {
        String sql = "SELECT id FROM equipamentos WHERE nome = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            throw new SQLException("Produto não encontrado: " + nome);
        }
    }
}
