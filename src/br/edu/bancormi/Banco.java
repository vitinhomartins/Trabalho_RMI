package br.edu.bancormi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Banco extends Remote {

    boolean autenticar(int conta, String senha) throws RemoteException;

    double consultarSaldo(int conta, String senha) throws RemoteException;

    boolean depositar(int conta, double valor) throws RemoteException;

    boolean sacar(int conta, String senha, double valor) throws RemoteException;

    boolean transferir(
            int origem,
            String senha,
            int destino,
            double valor
    ) throws RemoteException;
}