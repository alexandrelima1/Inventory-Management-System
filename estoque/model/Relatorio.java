package estoque.model;

import java.time.LocalDateTime;

public class Relatorio {
    private int id;
    private String produtoNome;
    private String descricaoProduto;  
    private String tipo;
    private int quantidade;
    private LocalDateTime dataMovimentacao;
    private String descricao;
    private String conexFuncionario;
    private String fieldFuncionario;
    private int fga;  

    // Construtores, getters e setters
    public Relatorio(int id, String produtoNome, String descricaoProduto, String tipo, int quantidade, LocalDateTime dataMovimentacao,
                     String descricao, String conexFuncionario, String fieldFuncionario, int fga) {
        this.id = id;
        this.produtoNome = produtoNome;
        this.descricaoProduto = descricaoProduto;  
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.dataMovimentacao = dataMovimentacao;
        this.descricao = descricao;
        this.conexFuncionario = conexFuncionario;
        this.fieldFuncionario = fieldFuncionario;
        this.fga = fga;  
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public String getTipo() {
        return tipo;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public LocalDateTime getDataMovimentacao() {
        return dataMovimentacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getConexFuncionario() {
        return conexFuncionario;
    }

    public String getFieldFuncionario() {
        return fieldFuncionario;
    }

    public int getFga() {
        return fga;
    }

    public void setFga(int fga) {
        this.fga = fga;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;  
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto; 
    }
}
