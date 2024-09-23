package estoque.view;

import com.formdev.flatlaf.FlatLightLaf;
import com.github.lgooddatepicker.components.DatePicker;
import estoque.service.MovimentacaoService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmprestimoView extends JPanel {
    private JComboBox<String> comboBoxProduto;
    private JComboBox<String> comboBoxConex;
    private JComboBox<String> comboBoxTipo;
    private JTextField usuarioField;
    private JTextField fgaField;
    private JTextField descricaoField;
    private DatePicker datePickerEmprestimo;
    private DatePicker datePickerRetorno;
    private JTable disponiveisTable;
    private JTable movimentacoesTable;
    private JButton btnEmprestimo;
    private MovimentacaoService movimentacaoService;
    private Map<String, Integer> produtosMap;
    private Map<String, Integer> funcionariosMap;
    private Border defaultBorder;

    public EmprestimoView() {
        movimentacaoService = new MovimentacaoService();

        FlatLightLaf.setup();
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(new Color(240, 240, 240));
        inputPanel.setOpaque(false);
        setupInputFields(inputPanel);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weightx = 0.2;
        gbc.weighty = 1;
        backgroundPanel.add(inputPanel, gbc);

        adicionarTabelas(gbc, backgroundPanel);
        add(backgroundPanel, gbc);
    }

    private void setupInputFields(JPanel panel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        comboBoxTipo = new JComboBox<>(new String[]{"Empréstimo", "Retorno", "Baixa"});
        comboBoxTipo.addActionListener(e -> atualizarProdutos());
        addField(panel, "Tipo de Movimentação:", comboBoxTipo, gbc);

        comboBoxProduto = new JComboBox<>();
        addField(panel, "Equipamento:", comboBoxProduto, gbc);

        try {
            funcionariosMap = movimentacaoService.getFuncionariosByCargo("Conex");
            comboBoxConex = new JComboBox<>(funcionariosMap.keySet().toArray(new String[0]));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar funcionários: " + e.getMessage());
            comboBoxConex = new JComboBox<>();
        }
        addField(panel, "Conex Funcionário:", comboBoxConex, gbc);

        usuarioField = new JTextField(15);
        usuarioField.setPreferredSize(new Dimension(200, 21));
        addField(panel, "Usuário:", usuarioField, gbc);

        datePickerEmprestimo = new DatePicker();
        addField(panel, "Data do Empréstimo:", datePickerEmprestimo, gbc);

        datePickerRetorno = new DatePicker();
        addField(panel, "Data de Retorno:", datePickerRetorno, gbc);

        fgaField = new JTextField(15);
        fgaField.setPreferredSize(new Dimension(200, 21));
        addField(panel, "FGA:", fgaField, gbc);

        descricaoField = new JTextField(15);
        descricaoField.setPreferredSize(new Dimension(200, 21));
        addField(panel, "Descrição:", descricaoField, gbc);

        btnEmprestimo = new JButton("Executar");
        btnEmprestimo.setPreferredSize(new Dimension(200, 26));

        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.fill = GridBagConstraints.HORIZONTAL;
        gbcBtn.weightx = 1;
        gbcBtn.insets = new Insets(10, 0, 10, 0);
        panel.add(btnEmprestimo, gbcBtn);

        btnEmprestimo.addActionListener(e -> validarEExecutarMovimentacao());

        atualizarProdutos();
        limparCampos();
    }

    private void validarEExecutarMovimentacao() {
        // Validação dos campos
        if (comboBoxTipo.getSelectedItem() == null ||
                comboBoxProduto.getSelectedItem() == null ||
                comboBoxConex.getSelectedItem() == null ||
                usuarioField.getText().trim().isEmpty() ||
                datePickerEmprestimo.getDate() == null ||
                datePickerRetorno.getDate() == null ||
                fgaField.getText().trim().isEmpty() ||
                descricaoField.getText().trim().isEmpty()) { 

            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos corretamente.");
            return;
        }

        // Verificar se FGA é um número
        try {
            Integer.parseInt(fgaField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "O campo FGA deve conter apenas números.");
            return;
        }

        executarMovimentacao();
    }

    private void limparCampos() {
        comboBoxTipo.setSelectedIndex(-1);
        comboBoxProduto.removeAllItems();  
        comboBoxConex.setSelectedIndex(-1);
        usuarioField.setText("");
        fgaField.setText("");
        datePickerEmprestimo.clear();
        datePickerRetorno.clear();
        descricaoField.setText(""); 

        // Reseta as bordas para o estado padrão
        comboBoxTipo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        comboBoxProduto.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        comboBoxConex.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        usuarioField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        fgaField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        descricaoField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    private void atualizarProdutos() {
        // Cria o JDialog para exibir o "Loading"
        JDialog loadingDialog = new JDialog((Frame) SwingUtilities.getAncestorOfClass(Window.class, this), "Carregando...", true);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        loadingDialog.add(BorderLayout.CENTER, progressBar);
        loadingDialog.add(BorderLayout.NORTH, new JLabel("\n\n\n"));
        loadingDialog.setSize(300, 75);
        loadingDialog.setLocationRelativeTo(this);

        // SwingWorker para carregar os dados em background
        SwingWorker<List<String>, Void> worker = new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                List<String> produtos = new ArrayList<>();
                try {
                    String tipoMovimentacao = (String) comboBoxTipo.getSelectedItem();

                    if ("Empréstimo".equalsIgnoreCase(tipoMovimentacao)) {
                        produtos = movimentacaoService.getEquipamentos(true);
                    } else if ("Retorno".equalsIgnoreCase(tipoMovimentacao)) {
                        produtos = movimentacaoService.getEquipamentos(false);
                    } else {
                        produtos = movimentacaoService.getEquipamentos(true); 
                    }

                    produtosMap = new HashMap<>();  
                    for (String produto : produtos) {
                        int produtoId = movimentacaoService.buscarIdProdutoPorNome(produto);  
                        produtosMap.put(produto, produtoId);  
                    }

                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(EmprestimoView.this, "Erro ao carregar dados: " + e.getMessage());
                }
                return produtos;
            }

            @Override
            protected void done() {
                try {
                    List<String> produtos = get();  
                    comboBoxProduto.removeAllItems();  

                    for (String produto : produtos) {
                        comboBoxProduto.addItem(produto);  
                    }

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(EmprestimoView.this, "Erro ao processar produtos: " + e.getMessage());
                } finally {
                    loadingDialog.dispose();  
                }
            }
        };

        worker.execute();// Exibe a tela de "Loading" enquanto o worker está em execução
    }



    private void adicionarTabelas(GridBagConstraints gbc, JPanel backgroundPanel) {
        // Tabela de Movimentações
        JLabel labelMovimentacoes = new JLabel("Movimentações de Equipamentos");
        labelMovimentacoes.setHorizontalAlignment(JLabel.CENTER);
        labelMovimentacoes.setPreferredSize(new Dimension(0, 30)); 
        String[] movimentacoesColumnNames = {
                "Nome do Equipamento", "Tipo", "FGA", "Conex", "Data Empréstimo", "Data Retorno", "Usuário", "Descrição"
        };
        movimentacoesTable = new JTable(new DefaultTableModel(movimentacoesColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela não editável
            }
        }) {
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
        movimentacoesTable.getTableHeader().setReorderingAllowed(false);
        movimentacoesTable.setBackground(new Color(240, 240, 240)); 
        movimentacoesTable.setRowHeight(25); 
        JScrollPane movimentacoesScrollPane = new JScrollPane(movimentacoesTable) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 200); 
            }
        };
        movimentacoesScrollPane.setOpaque(false);
        movimentacoesScrollPane.getViewport().setOpaque(false);
        movimentacoesScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 0.8;
        gbc.weighty = 0.07; 
        backgroundPanel.add(labelMovimentacoes, gbc);
        gbc.gridy = 1;
        gbc.weighty = 1.0; 
        backgroundPanel.add(movimentacoesScrollPane, gbc);

        // Tabela de Estoque Disponível
        JLabel labelEstoque = new JLabel("Estoque de Equipamentos");
        labelEstoque.setHorizontalAlignment(JLabel.CENTER);
        labelEstoque.setPreferredSize(new Dimension(0, 30)); 
        String[] disponiveisColumnNames = {
                "Nome do Produto", "Descrição do Produto", "Em Estoque"
        };
        disponiveisTable = new JTable(new DefaultTableModel(disponiveisColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela não editável
            }
        }) {
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
        disponiveisTable.getTableHeader().setReorderingAllowed(false);
        disponiveisTable.setBackground(new Color(240, 240, 240));
        disponiveisTable.setRowHeight(25); 
        JScrollPane disponiveisScrollPane = new JScrollPane(disponiveisTable) {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(0, 200); 
            }
        };
        disponiveisScrollPane.setOpaque(false);
        disponiveisScrollPane.getViewport().setOpaque(false);
        disponiveisScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        gbc.gridy = 2;
        gbc.weighty = 0.1;
        backgroundPanel.add(labelEstoque, gbc);
        gbc.gridy = 3;
        gbc.weighty = 1.0; 
        backgroundPanel.add(disponiveisScrollPane, gbc);

        carregarEstoqueDisponivel();  
        carregarMovimentacoes();  
    }

    private void carregarEstoqueDisponivel() {
        DefaultTableModel model = (DefaultTableModel) disponiveisTable.getModel();
        model.setRowCount(0);

        try {
            List<Object[]> equipamentos = movimentacaoService.getEstoqueDisponivel();
            for (Object[] equipamento : equipamentos) {
                model.addRow(equipamento);  
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar estoque: " + e.getMessage());
        }
    }

    private void carregarMovimentacoes() {
        DefaultTableModel model = (DefaultTableModel) movimentacoesTable.getModel();
        model.setRowCount(0); 

        try {
            List<Object[]> movimentacoes = movimentacaoService.getMovimentacoes();
            for (Object[] movimentacao : movimentacoes) {
                model.addRow(movimentacao);  
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar movimentações: " + e.getMessage());
        }
    }

    // Método para executar a movimentação
    private void executarMovimentacao() {
        try {
            // Coletando dados do formulário
            String produtoNome = (String) comboBoxProduto.getSelectedItem();
            String funcionarioNome = (String) comboBoxConex.getSelectedItem();
            String usuario = usuarioField.getText();
            String tipoMovimentacao = (String) comboBoxTipo.getSelectedItem();
            String fga = fgaField.getText(); 
            String descricao = descricaoField.getText();

            // Recupera o ID do funcionário pelo nome selecionado
            int conexFuncionarioId = funcionariosMap.get(funcionarioNome);

            // Obtém o ID do produto usando o mapa
            int produtoId = produtosMap.get(produtoNome);

            if ("Empréstimo".equalsIgnoreCase(tipoMovimentacao)) {
                // Processar empréstimo
                LocalDate dataEmprestimo = datePickerEmprestimo.getDate();
                LocalDate dataRetorno = datePickerRetorno.getDate();
                movimentacaoService.realizarEmprestimo(produtoId, conexFuncionarioId, usuario, dataEmprestimo.atStartOfDay(), dataRetorno.atStartOfDay(), Integer.parseInt(fga), descricaoField.getText());
                JOptionPane.showMessageDialog(this, "Empréstimo realizado com sucesso!");
            } else if ("Retorno".equalsIgnoreCase(tipoMovimentacao)) {
                // Buscar data de empréstimo do banco de dados
                LocalDate dataEmprestimo = datePickerEmprestimo.getDate();
                LocalDate dataRetorno = datePickerRetorno.getDate();
                if (dataEmprestimo == null || dataRetorno == null) {
                    JOptionPane.showMessageDialog(this, "As datas de empréstimo e retorno devem ser preenchidas.");
                    return;
                }
                descricaoField.getText();
                movimentacaoService.realizarRetorno(produtoId, conexFuncionarioId, usuario, dataEmprestimo.atStartOfDay(), dataRetorno.atStartOfDay(), Integer.parseInt(fga), descricao);
                JOptionPane.showMessageDialog(this, "Item retornado com sucesso!");
            } else if ("Baixa".equalsIgnoreCase(tipoMovimentacao)) {
                // Processar baixa
                LocalDate dataEmprestimo = datePickerEmprestimo.getDate(); 
                LocalDate dataRetorno = datePickerRetorno.getDate();       
                movimentacaoService.realizarBaixa(produtoId, conexFuncionarioId, usuario, Integer.parseInt(fga), descricaoField.getText(), dataEmprestimo, dataRetorno);
                JOptionPane.showMessageDialog(this, "Baixa realizada com sucesso!");
            }


            // Atualizar as tabelas de estoque e movimentações após a ação
            carregarEstoqueDisponivel(); 
            carregarMovimentacoes();  

            limparCampos();  
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao processar movimentação: " + ex.getMessage());
        }
    }

    private void addField(JPanel panel, String label, JComponent field, GridBagConstraints gbc) {
        JLabel jLabel = new JLabel(label);
        panel.add(jLabel, gbc);
        panel.add(field, gbc);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new EmprestimoView();
        });
    }
}
