package estoque.view;

import estoque.service.EstoqueService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class RegistroRetornoEstoqueView extends JPanel {

    private JComboBox<String> comboCategoria;
    private JComboBox<String> comboProduto;
    private JComboBox<String> comboConexFuncionario;
    private JComboBox<String> comboFieldFuncionario;
    private JTextField quantidadeField;
    private JTextField descricaoField;
    private JTextField fgaField;

    private EstoqueService estoqueService;
    private Map<Integer, String> conexFuncionarios;
    private Map<Integer, String> fieldFuncionarios;

    // Definir bordas e estilo de texto
    private Border defaultBorder = new LineBorder(new Color(169, 169, 169)); 
    private Border errorBorder = new LineBorder(Color.RED);

    // Definir cor e fonte padrão para os rótulos
    private Color textColor = new Color(50, 50, 50); 
    private Font textFont = new Font("SansSerif", Font.PLAIN, 16);  

    public RegistroRetornoEstoqueView(EstoqueService estoqueService) {
        // Configuração do Look and Feel da interface para ficar mais consistente com o sistema operacional
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.estoqueService = estoqueService;

        setLayout(new BorderLayout());

        // Criar painel de fundo com imagem
        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Definir largura dos campos
        Dimension fieldSize = new Dimension(300, 30);

        // Texto estilizado para o título "Registrar Retorno"
        JLabel tituloLabel = new JLabel("Registrar Retorno");
        tituloLabel.setFont(new Font("SansSerif", Font.PLAIN, 32));  
        tituloLabel.setForeground(textColor);  
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER); 
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; 
        backgroundPanel.add(tituloLabel, gbc);  

        // Reiniciar gridwidth para outros componentes
        gbc.gridwidth = 1;

        // Configurando os labels com o novo estilo
        JLabel categoriaLabel = new JLabel("Categoria:");
        categoriaLabel.setFont(textFont);
        categoriaLabel.setForeground(textColor);
        comboCategoria = new JComboBox<>();
        comboCategoria.setPreferredSize(fieldSize);
        carregarCategorias(); 
        comboCategoria.setBorder(errorBorder);

        JLabel produtoLabel = new JLabel("Produto:");
        produtoLabel.setFont(textFont);
        produtoLabel.setForeground(textColor);
        comboProduto = new JComboBox<>();
        comboProduto.setPreferredSize(fieldSize);
        comboProduto.setBorder(errorBorder);

        JLabel conexLabel = new JLabel("Conex Funcionário:");
        conexLabel.setFont(textFont);
        conexLabel.setForeground(textColor);
        comboConexFuncionario = new JComboBox<>();
        comboConexFuncionario.setPreferredSize(fieldSize);
        carregarFuncionariosConex(); 
        comboConexFuncionario.setBorder(errorBorder);

        JLabel fieldLabel = new JLabel("Field Funcionário:");
        fieldLabel.setFont(textFont);
        fieldLabel.setForeground(textColor);
        comboFieldFuncionario = new JComboBox<>();
        comboFieldFuncionario.setPreferredSize(fieldSize);
        carregarFuncionariosField();  
        comboFieldFuncionario.setBorder(errorBorder);

        JLabel quantidadeLabel = new JLabel("Quantidade:");
        quantidadeLabel.setFont(textFont);
        quantidadeLabel.setForeground(textColor);
        quantidadeField = new JTextField();
        quantidadeField.setPreferredSize(fieldSize);
        quantidadeField.setBorder(errorBorder);
        adicionarFocusListener(quantidadeField);

        JLabel descricaoLabel = new JLabel("Descrição:");
        descricaoLabel.setFont(textFont);
        descricaoLabel.setForeground(textColor);
        descricaoField = new JTextField();
        descricaoField.setPreferredSize(fieldSize);
        descricaoField.setBorder(errorBorder);
        adicionarFocusListener(descricaoField);

        JLabel fgaLabel = new JLabel("FGA:");
        fgaLabel.setFont(textFont);
        fgaLabel.setForeground(textColor);
        fgaField = new JTextField();
        fgaField.setPreferredSize(fieldSize);
        fgaField.setBorder(errorBorder);
        adicionarFocusListener(fgaField);

        JButton registrarRetornoButton = new JButton("Registrar Retorno");
        registrarRetornoButton.setPreferredSize(new Dimension(150, 40));
        registrarRetornoButton.addActionListener(e -> registrarRetorno());

        // Adicionar componentes ao painel de fundo
        gbc.gridx = 0;
        gbc.gridy = 1;
        backgroundPanel.add(categoriaLabel, gbc);
        gbc.gridx = 1;
        backgroundPanel.add(comboCategoria, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        backgroundPanel.add(produtoLabel, gbc);
        gbc.gridx = 1;
        backgroundPanel.add(comboProduto, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        backgroundPanel.add(conexLabel, gbc);
        gbc.gridx = 1;
        backgroundPanel.add(comboConexFuncionario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        backgroundPanel.add(fieldLabel, gbc);
        gbc.gridx = 1;
        backgroundPanel.add(comboFieldFuncionario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        backgroundPanel.add(quantidadeLabel, gbc);
        gbc.gridx = 1;
        backgroundPanel.add(quantidadeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        backgroundPanel.add(descricaoLabel, gbc);
        gbc.gridx = 1;
        backgroundPanel.add(descricaoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        backgroundPanel.add(fgaLabel, gbc);
        gbc.gridx = 1;
        backgroundPanel.add(fgaField, gbc);

        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(registrarRetornoButton, gbc);

        // Adicionar painel de fundo à interface principal
        add(backgroundPanel, BorderLayout.CENTER);

        comboCategoria.addActionListener(e -> carregarProdutosPorCategoria());

        // Adicionar validação para ComboBox
        adicionarFocusListenerComboBox(comboCategoria);
        adicionarFocusListenerComboBox(comboProduto);
        adicionarFocusListenerComboBox(comboConexFuncionario);
        adicionarFocusListenerComboBox(comboFieldFuncionario);

        // Limpar os campos quando a view é inicializada
        limparCampos(); 

    private void carregarCategorias() {
        try {
            List<String> categorias = estoqueService.buscarTodasCategorias();
            for (String categoria : categorias) {
                comboCategoria.addItem(categoria);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar categorias: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarProdutosPorCategoria() {
        try {
            String categoriaSelecionada = (String) comboCategoria.getSelectedItem();
            comboProduto.removeAllItems(); 

            if (categoriaSelecionada != null) {
                List<String> produtos = estoqueService.buscarProdutosPorCategoria(categoriaSelecionada);

                // Popula o comboProduto com o nome do produto
                for (String produto : produtos) {
                    comboProduto.addItem(produto);
                }

                // Exibe uma mensagem se nenhum produto for encontrado para a categoria selecionada
                if (produtos.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nenhum produto encontrado para a categoria selecionada.", "Atenção", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarFuncionariosConex() {
        try {
            conexFuncionarios = estoqueService.buscarFuncionariosPorCargo("Conex");
            for (String nome : conexFuncionarios.values()) {
                comboConexFuncionario.addItem(nome);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar funcionários Conex: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarFuncionariosField() {
        try {
            fieldFuncionarios = estoqueService.buscarFuncionariosPorCargo("Field Service");
            for (String nome : fieldFuncionarios.values()) {
                comboFieldFuncionario.addItem(nome);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar funcionários Field Service: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparCampos() {
        comboCategoria.setSelectedIndex(-1);
        comboProduto.removeAllItems(); 
        comboConexFuncionario.setSelectedIndex(-1);
        comboFieldFuncionario.setSelectedIndex(-1);
        quantidadeField.setText("");
        descricaoField.setText("");
        fgaField.setText("");

        // Reseta as bordas para o estado padrão
        comboCategoria.setBorder(defaultBorder);
        comboProduto.setBorder(defaultBorder);
        comboConexFuncionario.setBorder(defaultBorder);
        comboFieldFuncionario.setBorder(defaultBorder);
        quantidadeField.setBorder(defaultBorder);
        descricaoField.setBorder(defaultBorder);
        fgaField.setBorder(defaultBorder);
    }

    private void registrarRetorno() {
        if (!validarCampos()) {
            return;  
        }

        try {
            String produtoSelecionado = (String) comboProduto.getSelectedItem();
            int conexFuncionarioId = getFuncionarioId(comboConexFuncionario.getSelectedItem().toString(), conexFuncionarios);
            int fieldFuncionarioId = getFuncionarioId(comboFieldFuncionario.getSelectedItem().toString(), fieldFuncionarios);
            int quantidade = Integer.parseInt(quantidadeField.getText());
            String descricao = descricaoField.getText();
            int fga = Integer.parseInt(fgaField.getText());

            estoqueService.registrarRetorno(produtoSelecionado, conexFuncionarioId, fieldFuncionarioId, quantidade, descricao, fga);
            JOptionPane.showMessageDialog(this, "Retorno registrado com sucesso!");

            limparCampos(); 

        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro ao registrar retorno: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        boolean valido = true;

        if (quantidadeField.getText().isEmpty() || !quantidadeField.getText().matches("\\d+") || Integer.parseInt(quantidadeField.getText()) <= 0) {
            quantidadeField.setBorder(errorBorder);
            valido = false;
        }

        if (fgaField.getText().isEmpty() || !fgaField.getText().matches("\\d+")) {
            fgaField.setBorder(errorBorder);
            valido = false;
        }

        if (descricaoField.getText().isEmpty()) {
            descricaoField.setBorder(errorBorder);
            valido = false;
        }

        if (comboCategoria.getSelectedIndex() == -1) {
            comboCategoria.setBorder(errorBorder);
            valido = false;
        }

        if (comboProduto.getSelectedIndex() == -1) {
            comboProduto.setBorder(errorBorder);
            valido = false;
        }

        if (comboConexFuncionario.getSelectedIndex() == -1) {
            comboConexFuncionario.setBorder(errorBorder);
            valido = false;
        }

        if (comboFieldFuncionario.getSelectedIndex() == -1) {
            comboFieldFuncionario.setBorder(errorBorder);
            valido = false;
        }

        return valido;
    }

    private void adicionarFocusListener(JTextField textField) {
        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (!textField.getText().isEmpty()) {
                    textField.setBorder(defaultBorder);
                }
            }
        });
    }

    private void adicionarFocusListenerComboBox(JComboBox<String> comboBox) {
        comboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (comboBox.getSelectedIndex() != -1) {
                    comboBox.setBorder(defaultBorder);
                }
            }
        });
    }

    private int getFuncionarioId(String nome, Map<Integer, String> funcionarios) {
        for (Map.Entry<Integer, String> entry : funcionarios.entrySet()) {
            if (entry.getValue().equals(nome)) {
                return entry.getKey();
            }
        }
        return -1;
    }

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
