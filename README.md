Sistema de Gestão de Estoque - TI

Descrição

Este sistema de gestão de estoque é projetado para controlar as entradas e saídas de equipamentos, componentes, periféricos e outros itens de TI. Ele é ideal para organizações que precisam manter um controle detalhado do inventário de TI através de um sistema robusto e seguro de autenticação e gerenciamento.

Funcionalidades
-Login Seguro: Autenticação de usuários através de LDAPS com controle de acesso baseado em grupos do Active Directory.
-Interface Dividida em Abas: Facilita a navegação entre diferentes funções do sistema.
-Listar Estoque: Exibe todos os itens de TI, com exceção de computadores, em uma JTable filtrável.
-Registro de Baixa: Permite registrar a baixa de itens, especificando detalhes do produto, funcionário responsável, e descrição do atendimento.
-Registro de Retorno: Gerencia o retorno de itens, com campos para especificar detalhes semelhantes aos do registro de baixa.
-Gerência de Empréstimos: Duas tabelas mostram a movimentação e o estoque, atualizadas em tempo real, com campos para gerenciar o empréstimo.
-Listar Movimentações: Lista todas as movimentações de itens, exceto equipamentos, facilitando o rastreamento e a auditoria.

Tecnologias Utilizadas
-Back-end: Java com conexão a um banco de dados PostgreSQL.
-Front-end: Interface gráfica Java Swing = Flatlaf.

Dependências:
-JUnit
-JDBC PostgreSQL
-LGoodDatePicker
-FlatLaf

Autor
-Alexandre de Lima - 23/09/2024
