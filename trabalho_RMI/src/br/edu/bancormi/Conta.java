package br.edu.bancormi;

import java.io.Serializable;

public class Conta implements Serializable {

    private int numero;
    private String titular;
    private String senha;
    private double saldo;

    public Conta(int numero, String titular, String senha, double saldo) {
        this.numero = numero;
        this.titular = titular;
        this.senha = senha;
        this.saldo = saldo;
    }

    public int getNumero() {
        return numero;
    }

    public String getTitular() {
        return titular;
    }

    public String getSenha() {
        return senha;
    }

    public double getSaldo() {
        return saldo;
    }

    public void depositar(double valor) {
        saldo += valor;
    }

    public boolean sacar(double valor) {

        if (valor <= 0 || valor > saldo) {
            return false;
        }

        saldo -= valor;
        return true;
    }
}