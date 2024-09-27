package ProjetoGrafo;

import java.io.*;
import java.util.*;

public class Menu {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TGrafoND grafo = null;

        while (true) {
            System.out.println("\nMenu de Opções:");
            System.out.println("1. Ler dados do arquivo grafo.txt");
            System.out.println("2. Gravar dados no arquivo grafo.txt");
            System.out.println("3. Inserir vértice");
            System.out.println("4. Inserir aresta");
            System.out.println("5. Remover vértice");
            System.out.println("6. Remover aresta");
            System.out.println("7. Mostrar conteúdo do arquivo");
            System.out.println("8. Mostrar grafo");
            System.out.println("9. Apresentar a conexidade do grafo");
            System.out.println("10. Encerrar a aplicação");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();

            if (opcao != 1 && opcao != 10 && grafo == null) {
                System.out.println("Por favor, carregue o grafo primeiro (opção 1).");
                continue;
            }

            try {
                switch (opcao) {
                    case 1:
                        grafo = new TGrafoND(0); // Inicializa com tipo 0 temporariamente
                        grafo.lerArquivo("grafo.txt");
                        break;
                    case 2:
                        grafo.gravarArquivo("grafo.txt");
                        break;
                    case 3:
                        System.out.print("Digite o rótulo do vértice: ");
                        int rotulo = scanner.nextInt();
                        System.out.print("Digite a identificação do vértice: ");
                        String identificacao = scanner.next();
                        float pesoVertice = 0.0f;
                        if (grafo.getTipoGrafo() == 1 || grafo.getTipoGrafo() == 3 || grafo.getTipoGrafo() == 5 || grafo.getTipoGrafo() == 7) {
                            System.out.print("Digite o peso do vértice: ");
                            pesoVertice = scanner.nextFloat();
                        }
                        grafo.inserirVertice(rotulo, identificacao, pesoVertice);
                        break;
                    case 4:
                        System.out.print("Digite o vértice de origem: ");
                        int origem = scanner.nextInt();
                        System.out.print("Digite o vértice de destino: ");
                        int destino = scanner.nextInt();
                        float pesoAresta = 1.0f;
                        if (grafo.getTipoGrafo() == 2 || grafo.getTipoGrafo() == 3 || grafo.getTipoGrafo() == 6 || grafo.getTipoGrafo() == 7) {
                            System.out.print("Digite o peso da aresta: ");
                            pesoAresta = scanner.nextFloat();
                        }
                        grafo.inserirAresta(origem, destino, pesoAresta);
                        break;
                    case 5:
                        System.out.print("Digite o rótulo do vértice a ser removido: ");
                        rotulo = scanner.nextInt();
                        grafo.removerVertice(rotulo);
                        break;
                    case 6:
                        System.out.print("Digite o vértice de origem: ");
                        origem = scanner.nextInt();
                        System.out.print("Digite o vértice de destino: ");
                        destino = scanner.nextInt();
                        grafo.removerAresta(origem, destino);
                        break;
                    case 7:
                        grafo.mostrarConteudoArquivo();
                        break;
                    case 8:
                        grafo.show();
                        break;
                    case 9:
                        System.out.println(grafo.isConexo());
                        break;
                    case 10:
                        System.out.println("Encerrando a aplicação...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (IOException e) {
                System.out.println("Erro ao acessar o arquivo: " + e.getMessage());
            }
        }
    }
}