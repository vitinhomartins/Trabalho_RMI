package br.edu.bancormi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;

public class BancoImpl extends UnicastRemoteObject implements Banco {

    private final Map<Integer, Conta> contas;
    private final RepositorioContas repositorio;
    private int proximaConta = 1004;

    public BancoImpl() throws RemoteException {
        super(5000);

        repositorio = new RepositorioContas();
        contas = repositorio.carregar();

        if (contas.isEmpty()) {

            contas.put(1001, new Conta(1001, "João", "1234", 1500.00));
            contas.put(1002, new Conta(1002, "Maria", "5678", 800.00));
            contas.put(1003, new Conta(1003, "Pedro", "9999", 2300.00));

            repositorio.salvar(contas);

            System.out.println("[BANCO] Nenhuma conta encontrada.");
            System.out.println("[BANCO] Contas iniciais criadas.");

        } else {

            proximaConta = contas.keySet()
                    .stream()
                    .max(Integer::compareTo)
                    .orElse(1003) + 1;

            System.out.println("[BANCO] Contas carregadas do arquivo.");
        }

        System.out.println("[BANCO] Banco inicializado.");
        System.out.println("[BANCO] Contas carregadas: " + contas.size());
    }

    @Override
    public boolean autenticar(int conta, String senha) {

        System.out.println("[LOGIN] Tentativa de login - Conta: " + conta);

        Conta c = contas.get(conta);

        if (c == null) {
            System.out.println("[LOGIN] Conta " + conta + " não encontrada.");
            return false;
        }

        boolean sucesso = c.getSenha().equals(senha);

        if (sucesso) {
            System.out.println("[LOGIN] Login realizado - Conta: " + conta);
        } else {
            System.out.println("[LOGIN] Senha incorreta - Conta: " + conta);
        }

        return sucesso;
    }

    @Override
    public double consultarSaldo(int conta, String senha) {

        System.out.println("[SALDO] Consulta solicitada - Conta: " + conta);

        Conta c = contas.get(conta);

        if (c == null || !c.getSenha().equals(senha)) {

            System.out.println("[SALDO] Consulta negada - Conta: " + conta);

            return -1;
        }

        double saldo = c.getSaldo();

        System.out.printf("[SALDO] Conta %d -> R$ %.2f%n", conta, saldo);

        return saldo;
    }

    @Override
    public synchronized boolean depositar(int conta, double valor) {

        System.out.printf("[DEPOSITO] Conta: %d | Valor: R$ %.2f%n", conta, valor);

        Conta c = contas.get(conta);

        if (c == null || valor <= 0) {

            System.out.println("[DEPOSITO] Operação recusada.");

            return false;
        }

        double saldoAnterior = c.getSaldo();

        c.depositar(valor);

        repositorio.salvar(contas);

        System.out.printf(
                "[DEPOSITO] Conta %d: R$ %.2f -> R$ %.2f%n",
                conta,
                saldoAnterior,
                c.getSaldo()
        );

        return true;
    }

    @Override
    public synchronized boolean sacar(int conta, String senha, double valor) {

        System.out.printf("[SAQUE] Conta: %d | Valor: R$ %.2f%n", conta, valor);

        Conta c = contas.get(conta);

        if (c == null) {

            System.out.println("[SAQUE] Conta não encontrada.");

            return false;
        }

        if (!c.getSenha().equals(senha)) {

            System.out.println("[SAQUE] Senha incorreta.");

            return false;
        }

        double saldoAnterior = c.getSaldo();

        if (!c.sacar(valor)) {

            System.out.println(
                    "[SAQUE] Saque recusado - saldo insuficiente ou valor inválido."
            );

            return false;
        }

        repositorio.salvar(contas);

        System.out.printf(
                "[SAQUE] Conta %d: R$ %.2f -> R$ %.2f%n",
                conta,
                saldoAnterior,
                c.getSaldo()
        );

        return true;
    }

    @Override
    public synchronized boolean transferir(
            int origem,
            String senha,
            int destino,
            double valor
    ) {

        System.out.printf(
                "[TRANSFERENCIA] %d -> %d | R$ %.2f%n",
                origem,
                destino,
                valor
        );

        Conta contaOrigem = contas.get(origem);
        Conta contaDestino = contas.get(destino);

        if (contaOrigem == null) {

            System.out.println(
                    "[TRANSFERENCIA] Conta de origem não encontrada."
            );

            return false;
        }

        if (contaDestino == null) {

            System.out.println(
                    "[TRANSFERENCIA] Conta de destino não encontrada."
            );

            return false;
        }

        if (!contaOrigem.getSenha().equals(senha)) {

            System.out.println(
                    "[TRANSFERENCIA] Senha incorreta."
            );

            return false;
        }

        if (valor <= 0) {

            System.out.println(
                    "[TRANSFERENCIA] Valor inválido."
            );

            return false;
        }

        double saldoOrigemAnterior = contaOrigem.getSaldo();
        double saldoDestinoAnterior = contaDestino.getSaldo();

        if (!contaOrigem.sacar(valor)) {

            System.out.println(
                    "[TRANSFERENCIA] Saldo insuficiente."
            );

            return false;
        }

        contaDestino.depositar(valor);

        repositorio.salvar(contas);

        System.out.printf(
                "[TRANSFERENCIA] Conta %d: R$ %.2f -> R$ %.2f%n",
                origem,
                saldoOrigemAnterior,
                contaOrigem.getSaldo()
        );

        System.out.printf(
                "[TRANSFERENCIA] Conta %d: R$ %.2f -> R$ %.2f%n",
                destino,
                saldoDestinoAnterior,
                contaDestino.getSaldo()
        );

        System.out.println(
                "[TRANSFERENCIA] Transferência realizada com sucesso."
        );

        return true;
    }

    @Override
    public synchronized int criarConta(String titular, String senha) {

        System.out.println(
                "[CONTA] Solicitação de criação de conta."
        );

        if (titular == null || titular.trim().isEmpty()) {

            System.out.println(
                    "[CONTA] Criação recusada - titular inválido."
            );

            return -1;
        }

        if (senha == null || senha.trim().isEmpty()) {

            System.out.println(
                    "[CONTA] Criação recusada - senha inválida."
            );

            return -1;
        }

        int numero = proximaConta++;

        Conta novaConta = new Conta(
                numero,
                titular,
                senha,
                0.0
        );

        contas.put(numero, novaConta);

        repositorio.salvar(contas);

        System.out.println(
                "[CONTA] Nova conta criada."
        );

        System.out.println(
                "[CONTA] Número: " + numero
                        + " | Titular: " + titular
        );

        return numero;
    }
}