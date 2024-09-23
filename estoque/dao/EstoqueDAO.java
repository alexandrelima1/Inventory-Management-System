package estoque.dao;

import estoque.config.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EstoqueDAO {

    // Método para buscar funcionários por cargo, retorna um Map de <ID, Nome>
    public Map<Integer, String> buscarFuncionariosPorCargo(String cargo) throws SQLException {
        Map<Integer, String> funcionarios = new HashMap<>();
        String query = "SELECT id, nome FROM funcionario WHERE cargo = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, cargo);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                funcionarios.put(resultSet.getInt("id"), resultSet.getString("nome"));
            }
        }
        return funcionarios;
    }

    // Método para registrar uma baixa no estoque usando os IDs dos funcionários e a descrição do produto
    public void registrarBaixa(int produtoId, String descricaoProduto, int conexFuncionarioId, int fieldFuncionarioId, int quantidade, String descricao, int fga) throws SQLException {
        String baixaQuery = "INSERT INTO baixa (produto_id, quantidade_baixada, data_baixa, descricao_baixa, conex_funcionario_id, field_funcionario_id, fga, descricao_produto) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String updateEstoqueQuery = "UPDATE estoque SET quantidade = quantidade - ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement baixaStatement = connection.prepareStatement(baixaQuery);
             PreparedStatement updateEstoqueStatement = connection.prepareStatement(updateEstoqueQuery)) {

            // Registrar a baixa
            baixaStatement.setInt(1, produtoId);
            baixaStatement.setInt(2, quantidade);
            baixaStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            baixaStatement.setString(4, descricao);
            baixaStatement.setInt(5, conexFuncionarioId);
            baixaStatement.setInt(6, fieldFuncionarioId);
            baixaStatement.setInt(7, fga);
            baixaStatement.setString(8, descricaoProduto);  
            baixaStatement.executeUpdate();

            // Atualizar o estoque
            updateEstoqueStatement.setInt(1, quantidade);
            updateEstoqueStatement.setInt(2, produtoId);
            updateEstoqueStatement.executeUpdate();
        }
    }


    public String buscarDescricaoProdutoPeloId(int produtoId) throws SQLException {
        String query = "SELECT descricao FROM estoque WHERE id = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, produtoId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("descricao");  // Retorna a descrição do produto
            } else {
                throw new SQLException("Produto com ID " + produtoId + " não encontrado.");
            }
        }
    }



    // Método para registrar o retorno ao estoque
    public void registrarRetorno(int produtoId, String descricaoProduto, int conexFuncionarioId, int fieldFuncionarioId, int quantidade, String descricao, int fga) throws SQLException {
        String retornoQuery = "INSERT INTO retorno (produto_id, quantidade_retorno, data_retorno, descricao_retorno, conex_funcionario_id, field_funcionario_id, fga, descricao_produto) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String updateEstoqueQuery = "UPDATE estoque SET quantidade = quantidade + ? WHERE id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement retornoStatement = connection.prepareStatement(retornoQuery);
             PreparedStatement updateEstoqueStatement = connection.prepareStatement(updateEstoqueQuery)) {

            // Registrar o retorno
            retornoStatement.setInt(1, produtoId);
            retornoStatement.setInt(2, quantidade);
            retornoStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            retornoStatement.setString(4, descricao);
            retornoStatement.setInt(5, conexFuncionarioId);
            retornoStatement.setInt(6, fieldFuncionarioId);
            retornoStatement.setInt(7, fga);  // Adicionando o valor de FGA
            retornoStatement.setString(8, descricaoProduto);  // Inserindo a descrição do produto
            retornoStatement.executeUpdate();

            // Atualizar o estoque
            updateEstoqueStatement.setInt(1, quantidade);
            updateEstoqueStatement.setInt(2, produtoId);
            updateEstoqueStatement.executeUpdate();
        }
    }




    // Método auxiliar para buscar o produto_id baseado no nome do produto
    public int buscarProdutoIdPeloNome(String nomeProduto) throws SQLException {
        String query = "SELECT id FROM estoque WHERE nome = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, nomeProduto);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");  // Retorna o ID do produto
            }
        }
        return -1;  
    }

    // Método para buscar todos os produtos no estoque
    public List<String> buscarTodosProdutos() throws SQLException {
        List<String> produtos = new ArrayList<>();
        String query = "SELECT nome FROM estoque";

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                produtos.add(resultSet.getString("nome"));
            }
        }

        return produtos;
    }

    // Método para buscar todas as categorias de produtos no estoque
    public List<String> buscarTodasCategorias() throws SQLException {
        List<String> categorias = new ArrayList<>();
        String query = "SELECT DISTINCT categoria FROM estoque";

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                categorias.add(resultSet.getString("categoria"));
            }
        }
        return categorias;
    }

    // Método para buscar produtos de acordo com a categoria selecionada
    public List<String> buscarProdutosPorCategoria(String categoria) throws SQLException {
        List<String> produtos = new ArrayList<>();
        String query = "SELECT nome FROM estoque WHERE categoria = ?";  

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, categoria);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                produtos.add(resultSet.getString("nome"));  // Adiciona o nome do produto à lista
            }
        }
        return produtos;
    }

    // Método para buscar todos os produtos no estoque com detalhes: nome, descrição, quantidade, categoria, local (sem ID)
    public List<Object[]> buscarTodosProdutosComDetalhes() throws SQLException {
        List<Object[]> produtos = new ArrayList<>();
        String query = "SELECT nome, descricao, quantidade, categoria, local FROM estoque";

        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String nome = resultSet.getString("nome");
                String descricao = resultSet.getString("descricao");
                int quantidade = resultSet.getInt("quantidade");
                String categoria = resultSet.getString("categoria");
                String local = resultSet.getString("local");

                // Adiciona os dados coletados como um array de objetos
                produtos.add(new Object[]{nome, descricao, quantidade, categoria, local});
            }
        }

        return produtos;
    }
}
