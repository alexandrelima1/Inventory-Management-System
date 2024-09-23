package estoque.dao;

import estoque.config.DBConnection;
import estoque.model.Relatorio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RelatorioDAO {

    // Consulta SQL para buscar os dados da view 'relatorios', incluindo 'descricao_produto' e FGA
    private static final String QUERY = "SELECT id_baixa AS id, produto_nome, descricao_produto, tipo, quantidade, data_movimentacao, " +
            "descricao, conex_funcionario, field_funcionario, fga FROM relatorios ORDER BY data_movimentacao DESC";

    public List<Relatorio> listarTodosRelatorios() {
        List<Relatorio> relatorios = new ArrayList<>();

        // Conexão com o banco de dados e execução da consulta
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(QUERY);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            // Iteração sobre o resultado da consulta
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String produtoNome = resultSet.getString("produto_nome");
                String descricaoProduto = resultSet.getString("descricao_produto"); 
                String tipo = resultSet.getString("tipo");
                int quantidade = resultSet.getInt("quantidade");
                LocalDateTime dataMovimentacao = resultSet.getTimestamp("data_movimentacao").toLocalDateTime();
                String descricao = resultSet.getString("descricao");
                String conexFuncionario = resultSet.getString("conex_funcionario");
                String fieldFuncionario = resultSet.getString("field_funcionario");
                int fga = resultSet.getInt("fga");  

                // Criação de um objeto Relatorio com os dados obtidos
                Relatorio relatorio = new Relatorio(id, produtoNome, descricaoProduto, tipo, quantidade, dataMovimentacao, descricao,
                        conexFuncionario, fieldFuncionario, fga);

                // Adiciona o objeto Relatorio à lista
                relatorios.add(relatorio);
            }

        } catch (SQLException e) {
            // Log de erro descritivo
            System.err.println("Erro ao listar os relatórios: " + e.getMessage());
            e.printStackTrace();
        }

        // Retorna a lista de relatórios
        return relatorios;
    }
}
