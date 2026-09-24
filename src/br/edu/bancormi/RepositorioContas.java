package br.edu.bancormi;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class RepositorioContas {

    private final String arquivo = "dados/contas.dat";

    public Map<Integer, Conta> carregar() {

        File file = new File(arquivo);

        if (!file.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream entrada =
                     new ObjectInputStream(
                             new FileInputStream(file)
                     )) {

            return (Map<Integer, Conta>) entrada.readObject();

        } catch (IOException | ClassNotFoundException e) {

            System.out.println(
                    "[ARQUIVO] Erro ao carregar contas."
            );

            e.printStackTrace();

            return new HashMap<>();
        }
    }

    public void salvar(Map<Integer, Conta> contas) {

        File file = new File(arquivo);

        File pasta = file.getParentFile();

        if (!pasta.exists()) {
            pasta.mkdirs();
        }

        try (ObjectOutputStream saida =
                     new ObjectOutputStream(
                             new FileOutputStream(file)
                     )) {

            saida.writeObject(contas);

            System.out.println(
                    "[ARQUIVO] Contas salvas com sucesso."
            );

        } catch (IOException e) {

            System.out.println(
                    "[ARQUIVO] Erro ao salvar contas."
            );

            e.printStackTrace();
        }
    }
}