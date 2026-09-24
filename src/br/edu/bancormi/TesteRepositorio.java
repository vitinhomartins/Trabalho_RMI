package br.edu.bancormi;

import java.util.Map;

public class TesteRepositorio {

    public static void main(String[] args) {

        RepositorioContas repositorio =
                new RepositorioContas();

        Map<Integer, Conta> contas =
                repositorio.carregar();

        contas.put(
                1001,
                new Conta(
                        1001,
                        "João",
                        "1234",
                        1500.00
                )
        );

        repositorio.salvar(contas);

        System.out.println(
                "Contas salvas: " + contas.size()
        );
    }
}