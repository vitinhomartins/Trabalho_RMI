package br.edu.bancormi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Servidor {

    public static void main(String[] args) {

        try {

            String ipServidor = args[0];

            System.setProperty("java.rmi.server.hostname", ipServidor);

            System.out.println("======================================");
            System.out.println("          BANCO RMI - SERVIDOR");
            System.out.println("======================================");

            System.out.println("[RMI] Configurando hostname: " + ipServidor);

            Registry registry = LocateRegistry.createRegistry(1099);

            System.out.println("[RMI] Registry iniciado na porta 1099.");

            Banco banco = new BancoImpl();

            registry.rebind("BancoRMI", banco);

            System.out.println("[RMI] Objeto BancoRMI registrado.");

            System.out.println("[RMI] Porta do objeto: 5000");

            System.out.println("--------------------------------------");
            System.out.println("Servidor RMI iniciado com sucesso!");
            System.out.println("IP: " + ipServidor);
            System.out.println("Registry: 1099");
            System.out.println("Banco: 5000");
            System.out.println("--------------------------------------");
            System.out.println("Aguardando clientes...");
            System.out.println();

        } catch (Exception e) {

            System.out.println("[ERRO] Falha ao iniciar o servidor.");

            e.printStackTrace();
        }
    }
}