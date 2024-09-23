package estoque.service;

import estoque.dao.EstoqueDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class EstoqueService {

    private EstoqueDAO estoqueDAO;

    public EstoqueService(EstoqueDAO estoqueDAO) {
        this.estoqueDAO = estoqueDAO;
    }

    // Método para buscar funcionários por cargo (ID e Nome)
    public Map<Integer, String> buscarFuncionariosPorCargo(String cargo) throws SQLException {
        return estoqueDAO.buscarFuncionariosPorCargo(cargo);
    }

    // Método para registrar a baixa do estoque
    public void registrarBaixa(String produtoNome, String descricaoProduto, int conexFuncionarioId, int fieldFuncionarioId, int quantidade, String descricao, int fga) throws SQLException {
        int produtoId = estoqueDAO.buscarProdutoIdPeloNome(produtoNome);

        // Buscar a descrição do produto pelo ID antes de registrar a baixa
        String descricaoProdutoObtida = estoqueDAO.buscarDescricaoProdutoPeloId(produtoId);

        // Registrar a baixa com a descrição do produto obtida
        estoqueDAO.registrarBaixa(produtoId, descricaoProdutoObtida, conexFuncionarioId, fieldFuncionarioId, quantidade, descricao, fga);
    }


    // Novo método para registrar o retorno ao estoque
    public void registrarRetorno(String produtoNome, int conexFuncionarioId, int fieldFuncionarioId, int quantidade, String descricao, int fga) throws SQLException {
        // Obter o produtoId como int
        int produtoId = estoqueDAO.buscarProdutoIdPeloNome(produtoNome);

        // Buscar a descrição do produto pelo ID antes de registrar o retorno
        String descricaoProdutoObtida = estoqueDAO.buscarDescricaoProdutoPeloId(produtoId);

        // Registrar o retorno com a descrição do produto obtida
        estoqueDAO.registrarRetorno(produtoId, descricaoProdutoObtida, conexFuncionarioId, fieldFuncionarioId, quantidade, descricao, fga);
    }


    
    public List<String> buscarTodosProdutos() throws SQLException {
        return estoqueDAO.buscarTodosProdutos();
    }

    
    public List<Object[]> listarEstoque() throws SQLException {
        return estoqueDAO.buscarTodosProdutosComDetalhes();
    }

    
    public List<String> buscarTodasCategorias() throws SQLException {
        return estoqueDAO.buscarTodasCategorias();
    }

    
    public List<String> buscarProdutosPorCategoria(String categoria) throws SQLException {
        return estoqueDAO.buscarProdutosPorCategoria(categoria);
    }
}

