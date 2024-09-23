package estoque.view;

import com.formdev.flatlaf.FlatLightLaf;
import estoque.service.EstoqueService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

public class ListarEstoqueView extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;
    private JTextField filterField;  
    private TableRowSorter<DefaultTableModel> rowSorter;  
    private EstoqueService estoqueService;

    public ListarEstoqueView(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;

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

        // Campo de filtro
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
        filterField = new JTextField();
        filterPanel.setOpaque(false);  
        filterField.setPreferredSize(new Dimension(150, 23)); 
        filterField.setToolTipText("Filtrar por Produto, Descrição, Categoria ou Local");

        JLabel filterLabel = new JLabel("Filtro:");
        filterPanel.add(filterLabel);
        filterPanel.add(filterField);

        backgroundPanel.add(filterPanel, BorderLayout.NORTH);

        // Inicializando a JTable e o modelo
        String[] colunas = {"Produto", "Descrição", "Quantidade", "Categoria", "Local"};
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

        // Carregar o estoque na tabela
        carregarEstoque();

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

    // Método para carregar o estoque na JTable
    // Dentro da classe ListarEstoqueView
    public void carregarEstoque() {
        try {
            List<Object[]> estoque = estoqueService.listarEstoque();

            tableModel.setRowCount(0); 

            for (Object[] produto : estoque) {
                tableModel.addRow(produto); 
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar o estoque: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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
                (int) (totalWidth * 0.20),  // Produto
                (int) (totalWidth * 0.30),  // Descrição
                (int) (totalWidth * 0.10),  // Quantidade
                (int) (totalWidth * 0.20),  // Categoria
                (int) (totalWidth * 0.15)   // Local
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
