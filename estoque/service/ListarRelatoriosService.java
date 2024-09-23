package estoque.service;

import estoque.dao.RelatorioDAO;
import estoque.model.Relatorio;

import java.util.List;

public class ListarRelatoriosService {

    private RelatorioDAO relatorioDAO;

    public ListarRelatoriosService() {
        relatorioDAO = new RelatorioDAO();
    }

    public List<Relatorio> listarTodosRelatorios() {
        return relatorioDAO.listarTodosRelatorios();
    }
}
