package br.edu.bancormi;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ClienteGUI extends JFrame {

    private static final String IP_SERVIDOR = "10.0.0.126";
    private static final int PORTA_REGISTRY = 1099;

    private Banco banco;

    private int contaLogada;
    private String senhaLogada;

    private JLabel labelTitular;
    private JLabel labelSaldo;

    public ClienteGUI() {
        conectarServidor();
        mostrarLogin();
    }

    private void conectarServidor() {
        try {
            Registry registry = LocateRegistry.getRegistry(IP_SERVIDOR, PORTA_REGISTRY);

            banco = (Banco) registry.lookup("BancoRMI");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Não foi possível conectar ao servidor RMI.\n\n" + "Servidor: " + IP_SERVIDOR + "\nPorta: " + PORTA_REGISTRY, "Erro de conexão", JOptionPane.ERROR_MESSAGE);

            System.exit(1);
        }
    }

    private void configurarJanela(String titulo, int largura, int altura) {
        setTitle(titulo);
        setSize(largura, altura);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void limparJanela() {
        getContentPane().removeAll();

        revalidate();
        repaint();
    }

    private JLabel criarTitulo(String texto) {
        JLabel titulo = new JLabel(texto, SwingConstants.CENTER);

        titulo.setFont(new Font("Arial", Font.BOLD, 26));

        return titulo;
    }

    private JPanel criarCampo(String texto, JTextField campo) {
        JPanel painel = new JPanel(new BorderLayout(5, 5));

        painel.add(new JLabel(texto), BorderLayout.WEST);

        painel.add(campo, BorderLayout.CENTER);

        return painel;
    }

    private void mostrarLogin() {
        limparJanela();

        configurarJanela("Banco RMI - Login", 400, 350);

        JPanel principal = new JPanel(new GridLayout(5, 1, 10, 10));

        principal.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titulo = criarTitulo("BANCO RMI");

        JTextField campoConta = new JTextField();

        JPasswordField campoSenha = new JPasswordField();

        JButton botaoEntrar = new JButton("Entrar");

        JButton botaoCriarConta = new JButton("Criar conta");

        principal.add(titulo);

        principal.add(criarCampo("Número da conta", campoConta));

        principal.add(criarCampo("Senha", campoSenha));

        principal.add(botaoEntrar);
        principal.add(botaoCriarConta);

        add(principal);

        botaoEntrar.addActionListener(e -> {

            try {
                int conta = Integer.parseInt(campoConta.getText());

                String senha = new String(campoSenha.getPassword());

                if (banco.autenticar(conta, senha)) {

                    contaLogada = conta;
                    senhaLogada = senha;

                    mostrarBanco();

                } else {

                    JOptionPane.showMessageDialog(this, "Conta ou senha inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(this, "Digite um número de conta válido.", "Erro", JOptionPane.ERROR_MESSAGE);

            } catch (Exception ex) {

                mostrarErro(ex);
            }
        });

        botaoCriarConta.addActionListener(e -> mostrarCriarConta());

        setVisible(true);
    }

    private void mostrarCriarConta() {
        limparJanela();

        configurarJanela("Banco RMI - Criar Conta", 450, 350);

        JPanel principal = new JPanel(new GridLayout(5, 1, 10, 10));

        principal.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JTextField campoNome = new JTextField();

        JPasswordField campoSenha = new JPasswordField();

        JPasswordField campoConfirmacao = new JPasswordField();

        JButton botaoCriar = new JButton("Criar conta");

        JButton botaoVoltar = new JButton("Voltar");

        principal.add(criarTitulo("CRIAR CONTA"));

        principal.add(criarCampo("Nome", campoNome));

        principal.add(criarCampo("Senha", campoSenha));

        principal.add(criarCampo("Confirmar", campoConfirmacao));

        JPanel botoes = new JPanel(new GridLayout(1, 2, 10, 0));

        botoes.add(botaoCriar);
        botoes.add(botaoVoltar);

        principal.add(botoes);

        add(principal);

        botaoCriar.addActionListener(e -> {

            String nome = campoNome.getText().trim();

            String senha = new String(campoSenha.getPassword());

            String confirmacao = new String(campoConfirmacao.getPassword());

            if (nome.isEmpty()) {

                JOptionPane.showMessageDialog(this, "Digite o nome do titular.", "Erro", JOptionPane.ERROR_MESSAGE);

                return;
            }

            if (senha.isEmpty()) {

                JOptionPane.showMessageDialog(this, "Digite uma senha.", "Erro", JOptionPane.ERROR_MESSAGE);

                return;
            }

            if (!senha.equals(confirmacao)) {

                JOptionPane.showMessageDialog(this, "As senhas não são iguais.", "Erro", JOptionPane.ERROR_MESSAGE);

                return;
            }

            try {

                int numeroConta = banco.criarConta(nome, senha);

                if (numeroConta == -1) {

                    JOptionPane.showMessageDialog(this, "Não foi possível criar a conta.", "Erro", JOptionPane.ERROR_MESSAGE);

                    return;
                }

                JOptionPane.showMessageDialog(this, "Conta criada com sucesso!\n\n" + "Número da conta: " + numeroConta, "Conta criada", JOptionPane.INFORMATION_MESSAGE);

                mostrarLogin();

            } catch (Exception ex) {

                mostrarErro(ex);
            }
        });

        botaoVoltar.addActionListener(e -> mostrarLogin());

        setVisible(true);
    }

    private void mostrarBanco() {
        limparJanela();

        configurarJanela("Banco RMI", 500, 450);

        JPanel principal = new JPanel(new BorderLayout(10, 10));

        principal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        labelTitular = new JLabel("Conta: " + contaLogada, SwingConstants.CENTER);

        labelTitular.setFont(new Font("Arial", Font.BOLD, 22));

        labelSaldo = new JLabel("R$ 0,00", SwingConstants.CENTER);

        labelSaldo.setFont(new Font("Arial", Font.BOLD, 30));

        JPanel topo = new JPanel(new GridLayout(2, 1));

        topo.add(labelTitular);
        topo.add(labelSaldo);

        principal.add(topo, BorderLayout.NORTH);

        JPanel botoes = new JPanel(new GridLayout(3, 2, 10, 10));

        JButton botaoSaldo = new JButton("Atualizar saldo");

        JButton botaoDeposito = new JButton("Depositar");

        JButton botaoSaque = new JButton("Sacar");

        JButton botaoTransferencia = new JButton("Transferir");

        JButton botaoSair = new JButton("Sair da conta");

        botoes.add(botaoSaldo);
        botoes.add(botaoDeposito);
        botoes.add(botaoSaque);
        botoes.add(botaoTransferencia);
        botoes.add(botaoSair);

        principal.add(botoes, BorderLayout.CENTER);

        add(principal);

        botaoSaldo.addActionListener(e -> atualizarSaldo());

        botaoDeposito.addActionListener(e -> realizarDeposito());

        botaoSaque.addActionListener(e -> realizarSaque());

        botaoTransferencia.addActionListener(e -> realizarTransferencia());

        botaoSair.addActionListener(e -> {

            contaLogada = 0;
            senhaLogada = null;

            mostrarLogin();
        });

        atualizarSaldo();

        setVisible(true);
    }

    private void atualizarSaldo() {

        try {

            double saldo = banco.consultarSaldo(contaLogada, senhaLogada);

            if (saldo < 0) {

                JOptionPane.showMessageDialog(this, "Não foi possível consultar o saldo.", "Erro", JOptionPane.ERROR_MESSAGE);

                return;
            }

            labelSaldo.setText(String.format("R$ %.2f", saldo));

        } catch (Exception e) {

            mostrarErro(e);
        }
    }

    private void realizarDeposito() {

        String entrada = JOptionPane.showInputDialog(this, "Digite o valor do depósito:");

        if (entrada == null) {
            return;
        }

        try {

            double valor = Double.parseDouble(entrada.replace(",", "."));

            if (banco.depositar(contaLogada, valor)) {

                JOptionPane.showMessageDialog(this, "Depósito realizado com sucesso!");

                atualizarSaldo();

            } else {

                JOptionPane.showMessageDialog(this, "Valor de depósito inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(this, "Digite um valor válido.", "Erro", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {

            mostrarErro(e);
        }
    }

    private void realizarSaque() {

        String entrada = JOptionPane.showInputDialog(this, "Digite o valor do saque:");

        if (entrada == null) {
            return;
        }

        try {

            double valor = Double.parseDouble(entrada.replace(",", "."));

            if (banco.sacar(contaLogada, senhaLogada, valor)) {

                JOptionPane.showMessageDialog(this, "Saque realizado com sucesso!");

                atualizarSaldo();

            } else {

                JOptionPane.showMessageDialog(this, "Saque não realizado.\n" + "Verifique o saldo e o valor informado.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(this, "Digite um valor válido.", "Erro", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {

            mostrarErro(e);
        }
    }

    private void realizarTransferencia() {

        JPanel painel = new JPanel(new GridLayout(2, 2, 5, 5));

        JTextField campoDestino = new JTextField();

        JTextField campoValor = new JTextField();

        painel.add(new JLabel("Conta destino:"));

        painel.add(campoDestino);

        painel.add(new JLabel("Valor:"));

        painel.add(campoValor);

        int resultado = JOptionPane.showConfirmDialog(this, painel, "Transferência", JOptionPane.OK_CANCEL_OPTION);

        if (resultado != JOptionPane.OK_OPTION) {
            return;
        }

        try {

            int destino = Integer.parseInt(campoDestino.getText());

            double valor = Double.parseDouble(campoValor.getText().replace(",", "."));

            if (banco.transferir(contaLogada, senhaLogada, destino, valor)) {

                JOptionPane.showMessageDialog(this, "Transferência realizada com sucesso!");

                atualizarSaldo();

            } else {

                JOptionPane.showMessageDialog(this, "Transferência não realizada.\n" + "Verifique a conta e o saldo.", "Erro", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {

            JOptionPane.showMessageDialog(this, "Informe valores válidos.", "Erro", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {

            mostrarErro(e);
        }
    }

    private void mostrarErro(Exception e) {

        JOptionPane.showMessageDialog(this, "Ocorreu um erro na comunicação " + "com o servidor.\n\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(ClienteGUI::new);
    }
}