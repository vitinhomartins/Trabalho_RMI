package br.edu.bancormi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {

        try {

            Registry registry =
                    LocateRegistry.getRegistry("localhost", 1099);

            Banco banco =
                    (Banco) registry.lookup("BancoRMI");

            Scanner scanner = new Scanner(System.in);

            System.out.println("==============================");
            System.out.println("       BANCO RMI");
            System.out.println("==============================");

            System.out.print("Número da conta: ");
            int conta = Integer.parseInt(scanner.nextLine());

            System.out.print("Senha: ");
            String senha = scanner.nextLine();

            if (!banco.autenticar(conta, senha)) {

                System.out.println("Conta ou senha inválida.");
                return;

            }

            System.out.println("Login realizado com sucesso!");

            while (true) {

                System.out.println();
                System.out.println("===== MENU =====");
                System.out.println("1 - Consultar saldo");
                System.out.println("2 - Depositar");
                System.out.println("3 - Sacar");
                System.out.println("4 - Transferir");
                System.out.println("5 - Sair");
                System.out.print("Escolha: ");

                int opcao =
                        Integer.parseInt(scanner.nextLine());

                switch (opcao) {

                    case 1:

                        double saldo =
                                banco.consultarSaldo(conta, senha);

                        System.out.printf(
                                "Saldo: R$ %.2f%n",
                                saldo
                        );

                        break;

                    case 2:

                        System.out.print(
                                "Valor do depósito: R$ "
                        );

                        double deposito =
                                Double.parseDouble(scanner.nextLine());

                        if (banco.depositar(conta, deposito)) {
                            System.out.println(
                                    "Depósito realizado!"
                            );
                        } else {
                            System.out.println(
                                    "Depósito inválido."
                            );
                        }

                        break;

                    case 3:

                        System.out.print(
                                "Valor do saque: R$ "
                        );

                        double saque =
                                Double.parseDouble(scanner.nextLine());

                        if (banco.sacar(
                                conta,
                                senha,
                                saque
                        )) {

                            System.out.println(
                                    "Saque realizado!"
                            );

                        } else {

                            System.out.println(
                                    "Saque não realizado."
                            );
                        }

                        break;

                    case 4:

                        System.out.print(
                                "Conta destino: "
                        );

                        int destino =
                                Integer.parseInt(scanner.nextLine());

                        System.out.print(
                                "Valor da transferência: R$ "
                        );

                        double valor =
                                Double.parseDouble(scanner.nextLine());

                        if (banco.transferir(
                                conta,
                                senha,
                                destino,
                                valor
                        )) {

                            System.out.println(
                                    "Transferência realizada!"
                            );

                        } else {

                            System.out.println(
                                    "Transferência não realizada."
                            );
                        }

                        break;

                    case 5:

                        System.out.println(
                                "Encerrando cliente..."
                        );

                        return;

                    default:

                        System.out.println(
                                "Opção inválida."
                        );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Erro ao conectar ao servidor RMI:"
            );

            e.printStackTrace();
        }
    }
}