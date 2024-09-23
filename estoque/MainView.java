package estoque;

import estoque.dao.EstoqueDAO;
import estoque.service.EstoqueService;
import estoque.view.ListarRelatoriosView;
import estoque.view.RegistroBaixaEstoqueView;
import estoque.view.RegistroRetornoEstoqueView; 
import estoque.view.ListarEstoqueView;
import estoque.view.EmprestimoView; 

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class MainView extends JFrame {

    private EstoqueService estoqueService;
    private ListarRelatoriosView listarRelatoriosView;
    private ListarEstoqueView listarEstoqueView;
    private RegistroBaixaEstoqueView registroBaixaEstoqueView;
    private RegistroRetornoEstoqueView registroRetornoEstoqueView; 
    private EmprestimoView emprestimoView; 
    private JTabbedPane tabbedPane;
    public MainView() {
        // Configuração do Look and Feel da interface para ficar mais consistente com o sistema operacional
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Inicializa o serviço de estoque passando o EstoqueDAO
        EstoqueDAO estoqueDAO = new EstoqueDAO(); 
        estoqueService = new EstoqueService(estoqueDAO); 

        
        setTitle("Sistema de Estoque");

        
        setSize(1300, 700);
        setLocationRelativeTo(null);  // Centraliza a janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        setResizable(true);

        
        setLayout(new BorderLayout());

        
        tabbedPane = new JTabbedPane();

        
        listarEstoqueView = new ListarEstoqueView(estoqueService);
        tabbedPane.addTab("Listar Estoque", listarEstoqueView);

        
        registroBaixaEstoqueView = new RegistroBaixaEstoqueView(estoqueService);
        tabbedPane.addTab("Registrar Baixa", registroBaixaEstoqueView);

        
        registroRetornoEstoqueView = new RegistroRetornoEstoqueView(estoqueService); 
        tabbedPane.addTab("Registrar Retorno", registroRetornoEstoqueView); 

        
        emprestimoView = new EmprestimoView();
        tabbedPane.addTab("Gerenciamento de Empréstimos", emprestimoView);

        
        listarRelatoriosView = new ListarRelatoriosView();
        tabbedPane.addTab("Listar Movimentações", listarRelatoriosView);

        
        Start startPanel = new Start(this);
        add(startPanel, BorderLayout.CENTER);

        // Adiciona o ChangeListener para detectar mudanças de abas
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                
                int selectedIndex = tabbedPane.getSelectedIndex();

                
                if (selectedIndex == 0) {
                    listarEstoqueView.carregarEstoque();
                }

                
                if (selectedIndex == 4) {
                    listarRelatoriosView.carregarRelatorios();
                }
            }
        });

        // Ajusta os componentes quando a janela for redimensionada
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                listarRelatoriosView.ajustarColunasTabela(getSize());
                listarEstoqueView.ajustarColunasTabela(getSize());
                revalidate();
                repaint();
            }
        });

        setVisible(true);
    }

    // Método para exibir o tabbedPane e ocultar a tela de apresentação
    public void exibirTabbedPane() {
        remove(getContentPane().getComponent(0)); 
        add(tabbedPane, BorderLayout.CENTER);    
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainView::new);
    }
}
