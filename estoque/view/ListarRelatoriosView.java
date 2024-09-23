package estoque.view;

import com.formdev.flatlaf.FlatLightLaf;
import estoque.model.Relatorio;
import estoque.service.ListarRelatoriosService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListarRelatoriosView extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JTextField filterField;  
    private TableRowSorter<DefaultTableModel> rowSorter;  

    public ListarRelatoriosView() {
        // Configuração do Look and Feel da interface para ficar mais consistente com o sistema operacional
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Aplicar o tema FlatLaf
        FlatLightLaf.setup();

        // Definir layout para o painel principal (com imagem de fundo)
        setLayout(new BorderLayout(10, 10));

        // Criar painel com a imagem de fundo
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout(10, 10));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Criando o título
        JLabel titleLabel = new JLabel("Relatórios de Movimentações");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        // Adicionar o painel de título ao painel de fundo
        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        // Campo de filtro
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterField = new JTextField();
        filterPanel.setOpaque(false);  
        filterField.setPreferredSize(new Dimension(150, 23));  
        filterField.setToolTipText("Filtrar por Conex Func. ,Field Func. ou  FGA");
        JLabel filterLabel = new JLabel("Filtro:");
        filterPanel.add(filterLabel);
        filterPanel.add(filterField);

        backgroundPanel.add(filterPanel, BorderLayout.NORTH);

        // Inicializando a JTable e o modelo
        String[] colunas = {"Produto", "Descrição do Produto", "Tipo", "Quantidade", "Data", "Descrição", "Conex Func.", "Field Func.", "FGA"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Criando a tabela com o efeito "zebra"
        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    // Alterna entre branco e cinza escuro
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(190, 190, 190));
                }
                return c;
            }
        };

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);  
        rowSorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(rowSorter);

        // Centralizar o conteúdo das células
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Centralizar o cabeçalho das colunas
        JTableHeader header = table.getTableHeader();
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) header.getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setReorderingAllowed(false);

        // ScrollPane para a tabela
        scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Adicionar a tabela ao painel de fundo
        backgroundPanel.add(scrollPane, BorderLayout.CENTER);

        // Adicionar o painel de fundo à tela principal
        add(backgroundPanel, BorderLayout.CENTER);

        // Carregar os relatórios na tabela
        carregarRelatorios();

        // Adicionar funcionalidade ao filtro
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarTabela();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarTabela();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarTabela();
            }
        });
    }

    // Método para carregar os relatórios na JTable
    public void carregarRelatorios() {
        ListarRelatoriosService listarRelatoriosService = new ListarRelatoriosService();
        List<Relatorio> relatorios = listarRelatoriosService.listarTodosRelatorios();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        tableModel.setRowCount(0);

        for (Relatorio relatorio : relatorios) {
            Object[] rowData = {
                    relatorio.getProdutoNome(),
                    relatorio.getDescricaoProduto(), 
                    relatorio.getTipo(),
                    relatorio.getQuantidade(),
                    relatorio.getDataMovimentacao().format(formatter),
                    relatorio.getDescricao(),
                    relatorio.getConexFuncionario(),
                    relatorio.getFieldFuncionario(),
                    relatorio.getFga()  
            };
            tableModel.addRow(rowData);
        }
    }

    // Método para filtrar a tabela com base no texto do campo de filtro
    private void filtrarTabela() {
        String text = filterField.getText();
        if (text.trim().length() == 0) {
            rowSorter.setRowFilter(null);  
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));  
        }
    }

    // Método para ajustar a largura das colunas conforme o tamanho da janela
    public void ajustarColunasTabela(Dimension tamanhoJanela) {
        TableColumn column;
        int totalWidth = tamanhoJanela.width;

        // Ajustar largura proporcional para cada coluna
        int[] columnWidths = {
                (int) (totalWidth * 0.15),  // Produto
                (int) (totalWidth * 0.13),  // Descrição do Produto
                (int) (totalWidth * 0.04),  // Tipo
                (int) (totalWidth * 0.08),  // Quantidade
                (int) (totalWidth * 0.11),  // Data Movimentação
                (int) (totalWidth * 0.17),  // Descrição
                (int) (totalWidth * 0.10),  // Conex Func.
                (int) (totalWidth * 0.10),  // Field Func.
                (int) (totalWidth * 0.05)   // FGA
        };

        for (int i = 0; i < columnWidths.length; i++) {
            column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(columnWidths[i]);
        }

        revalidate();
        repaint();
    }

    // Classe interna para gerenciar o fundo com a imagem
    private class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel() {
            try (InputStream is = getClass().getResourceAsStream("/resources/TELA_DE_FUNDO.bmp")) {
                if (is != null) {
                    backgroundImage = ImageIO.read(is); 
                } else {
                    System.err.println("Erro ao carregar a imagem de fundo");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
