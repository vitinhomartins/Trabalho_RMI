package br.edu.bancormi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class BancoImpl extends UnicastRemoteObject implements Banco {

    private Map<Integer, Conta> contas;
    private int proximaConta = 1004;

    public BancoImpl() throws RemoteException {
        super(5000);
        contas = new HashMap<>();

        contas.put(1001, new Conta(
                1001,
                "João",
                "1234",
                1500.00
        ));

        contas.put(1002, new Conta(
                1002,
                "Maria",
                "5678",
                800.00
        ));

        contas.put(1003, new Conta(
                1003,
                "Pedro",
                "9999",
                2300.00
        ));
    }

    @Override
    public boolean autenticar(int conta, String senha) {

        Conta c = contas.get(conta);

        if (c == null) {
            return false;
        }

        return c.getSenha().equals(senha);
    }

    @Override
    public double consultarSaldo(int conta, String senha) {

        Conta c = contas.get(conta);

        if (c == null || !c.getSenha().equals(senha)) {
            return -1;
        }

        return c.getSaldo();
    }

    @Override
    public synchronized boolean depositar(int conta, double valor) {

        Conta c = contas.get(conta);

        if (c == null || valor <= 0) {
            return false;
        }

        c.depositar(valor);

        return true;
    }

    @Override
    public synchronized boolean sacar(int conta, String senha, double valor) {

        Conta c = contas.get(conta);

        if (c == null || !c.getSenha().equals(senha)) {
            return false;
        }

        return c.sacar(valor);
    }

    @Override
    public synchronized boolean transferir(
            int origem,
            String senha,
            int destino,
            double valor) {

        Conta contaOrigem = contas.get(origem);
        Conta contaDestino = contas.get(destino);

        if (contaOrigem == null || contaDestino == null) {
            return false;
        }

        if (!contaOrigem.getSenha().equals(senha)) {
            return false;
        }

        if (valor <= 0) {
            return false;
        }

        if (!contaOrigem.sacar(valor)) {
            return false;
        }

        contaDestino.depositar(valor);

        return true;
    }

    @Override
    public synchronized int criarConta(
            String titular,
            String senha) {

        if (titular == null || titular.trim().isEmpty()) {
            return -1;
        }

        if (senha == null || senha.trim().isEmpty()) {
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

        return numero;
    }
}