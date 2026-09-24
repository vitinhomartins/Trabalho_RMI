package br.edu.bancormi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Servidor {

    public static void main(String[] args) {

        try {

            String ipServidor = args[0];

            System.setProperty(
                    "java.rmi.server.hostname",
                    ipServidor
            );

            Registry registry =
                    LocateRegistry.createRegistry(1099);

            Banco banco = new BancoImpl();

            registry.rebind("BancoRMI", banco);

            System.out.println("Servidor RMI iniciado!");
            System.out.println("IP: " + ipServidor);
            System.out.println("Porta Registry: 1099");
            System.out.println("Porta Banco: 5000");
            System.out.println("Aguardando clientes...");

        } catch (Exception e) {

            e.printStackTrace();

        }
    }
}