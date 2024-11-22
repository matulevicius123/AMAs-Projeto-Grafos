//João Pedro Maia Matulevicius Garcia - 10402158
//Rodrigo Mileo Lourenço Gil - 10390174
//TGrafoND baseado na implementação do professor. De array para hashmap em função de ganho de performance.

// Histórico de Mudanças do Código de Grafos

// 2024-11-01: Adicionada verificação para determinar se o grafo é Euleriano
// Autor: João

// 2024-11-03: Adicionada verificação para determinar se o grafo é Hamiltoniano
// Autor: Rodrigo

// 2024-11-05: Adicionada função para calcular os graus dos vértices
// Autor: João

// 2024-11-07: Adicionada função para encontrar o grafo com a maior distância
// Autor: Rodrigo

// 2024-11-10: Otimização da verificação Hamiltoniana (alta complexidade no cálculo normal)
// Autor: João

// 2024-11-12: Correção de bugs no cálculo dos graus dos vértices
// Autor: Rodrigo

// 2024-11-15: Refatoração da verificação Hamiltoniana para melhorar desempenho
// Autor: João

// 2024-11-17: Melhoria na função de encontrar o grafo com a maior distância para lidar com grafos maiores
// Autor: Rodrigo

// 2024-11-20: Documentação e comentários adicionados em todas as funções
// Autor: João e Rodrigo
// Detalhamento da lógica e explicação das implementações

package ProjetoGrafo;

import java.io.*;
import java.util.*;

public class TGrafoND {
    private int m; // quantidade de arestas
    private Map<Integer, Map<Integer, Float>> adj; // HashMap para armazenar a matriz de adjacência
    private Map<Integer, String> identificacaoVertices; // HashMap para armazenar a identificação dos vértices
    private Map<Integer, Float> pesosVertices; // HashMap para armazenar os pesos dos vértices
    private int tipoGrafo; // tipo do grafo

    public void mostrarConteudoArquivo() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + "\\grafo.txt"));
        String linha;
        int linhaAtual = 0;
        int tipoGrafo = 0;
        int numVertices = 0;
        int numArestas = 0;
    
        while ((linha = reader.readLine()) != null) {
            linhaAtual++;
            if (linhaAtual == 1) {
                tipoGrafo = Integer.parseInt(linha.trim());
                System.out.println("Tipo do Grafo: " + tipoGrafo);
            } else if (linhaAtual == 2) {
                numVertices = Integer.parseInt(linha.trim());
                System.out.println("Número de Vértices: " + numVertices);
                System.out.println("Vértices:");
            } else if (linhaAtual <= 2 + numVertices) {
                String[] partes = linha.split("\"");
                int vertice = Integer.parseInt(partes[0].trim());
                String identificacao = partes.length > 1 ? partes[1].trim() : "N/A";
                float peso = partes.length > 2 ? Float.parseFloat(partes[2].trim()) : 0.0f;
                System.out.println("  Vértice: " + vertice + ", Identificação: " + identificacao + ", Peso: " + peso);
            } else if (linhaAtual == 3 + numVertices) {
                numArestas = Integer.parseInt(linha.trim());
                System.out.println("Número de Arestas: " + numArestas);
                System.out.println("Arestas:");
            } else {
                String[] partes = linha.split("\\s+");
                int origem = Integer.parseInt(partes[0]);
                int destino = Integer.parseInt(partes[1]);
                float peso = Float.parseFloat(partes[2]);
                System.out.println("  Origem: " + origem + ", Destino: " + destino + ", Peso: " + peso);
            }
        }
        reader.close();
    }
    
    public TGrafoND(int tipoGrafo) {
        this.m = 0;
        this.adj = new HashMap<>();
        this.identificacaoVertices = new HashMap<>();
        this.pesosVertices = new HashMap<>();
        this.tipoGrafo = tipoGrafo;
    }

    public void inserirVertice(int v, String identificacao, float peso) {
        if (adj.containsKey(v)) {
            System.out.println("Vértice " + v + " já existe.");
            return;
        }
        adj.put(v, new HashMap<>());
        identificacaoVertices.put(v, identificacao);
        if (tipoGrafo == 1 || tipoGrafo == 3 || tipoGrafo == 5 || tipoGrafo == 7) {
            pesosVertices.put(v, peso);
        }
    }

    public void inserirAresta(int v, int w, float valor) {
        if (!adj.containsKey(v) || !adj.containsKey(w)) {
            System.out.println("Um ou ambos os vértices não existem.");
            return;
        }
        if (adj.get(v).containsKey(w)) {
            System.out.println("Aresta entre " + v + " e " + w + " já existe.");
            return;
        }
        adj.get(v).put(w, valor);
        if (tipoGrafo == 0 || tipoGrafo == 1 || tipoGrafo == 2 || tipoGrafo == 3) {
            adj.get(w).put(v, valor); // Adiciona a aresta na direção oposta com o mesmo valor
        }
        m++; // atualiza qtd arestas
    }

    public void removerAresta(int v, int w) {
        if (adj.containsKey(v) && adj.get(v).containsKey(w)) {
            adj.get(v).remove(w);
            if (tipoGrafo == 0 || tipoGrafo == 1 || tipoGrafo == 2 || tipoGrafo == 3) {
                adj.get(w).remove(v); // Remove a aresta na direção oposta
            }
            m--; // atualiza qtd arestas
        }
    }

    public void removerVertice(int v) {
        if (adj.containsKey(v)) {
            for (int w : adj.get(v).keySet()) {
                adj.get(w).remove(v);
                m--;
            }
            adj.remove(v);
            identificacaoVertices.remove(v);
            pesosVertices.remove(v);
        }
    }

    public String isConexo() {
        if (adj.isEmpty()) return "O grafo está vazio.";
    
        // Verificar se há algum vértice sem conexões
        for (int v : adj.keySet()) {
            if (adj.get(v).isEmpty()) {
                return "O grafo não é conexo.";
            }
        }
    
        if (tipoGrafo == 0 || tipoGrafo == 1 || tipoGrafo == 2 || tipoGrafo == 3) {
            // Grafo não direcionado
            Set<Integer> visitados = new HashSet<>();
            Queue<Integer> fila = new LinkedList<>();
            int verticeInicial = adj.keySet().iterator().next();
            fila.add(verticeInicial);
    
            while (!fila.isEmpty()) {
                int v = fila.poll();
                if (!visitados.contains(v)) {
                    visitados.add(v);
                    for (int w : adj.get(v).keySet()) {
                        if (!visitados.contains(w)) {
                            fila.add(w);
                        }
                    }
                }
            }
    
            return visitados.size() == adj.size() ? "O grafo é conexo." : "O grafo não é conexo.";
        } else {
            // Grafo direcionado
            List<Set<Integer>> componentesFortementeConexas = encontrarComponentesFortementeConexas();
            int numComponentes = componentesFortementeConexas.size();
    
            if (numComponentes == 1) {
                return "O grafo é fortemente conexo (C3).";
            } else if (numComponentes == adj.size()) {
                return "O grafo é desconexo (C0).";
            } else if (numComponentes > 1 && numComponentes < adj.size()) {
                return "O grafo é fracamente conexo (C1).";
            } else {
                return "O grafo é parcialmente conexo (C2).";
            }
        }
    }
    
    private List<Set<Integer>> encontrarComponentesFortementeConexas() {
        // Implementação do algoritmo FCONEX para encontrar componentes fortemente conexas
        List<Set<Integer>> componentes = new ArrayList<>();
        Set<Integer> visitados = new HashSet<>();
        Stack<Integer> pilha = new Stack<>();
    
        for (int v : adj.keySet()) {
            if (!visitados.contains(v)) {
                dfs(v, visitados, pilha);
            }
        }
    
        Map<Integer, Set<Integer>> transposto = transporGrafo();
        visitados.clear();
    
        while (!pilha.isEmpty()) {
            int v = pilha.pop();
            if (!visitados.contains(v)) {
                Set<Integer> componente = new HashSet<>();
                dfsTransposto(v, visitados, componente, transposto);
                componentes.add(componente);
            }
        }
    
        return componentes;
    }
    
    private void dfs(int v, Set<Integer> visitados, Stack<Integer> pilha) {
        visitados.add(v);
        for (int w : adj.get(v).keySet()) {
            if (!visitados.contains(w)) {
                dfs(w, visitados, pilha);
            }
        }
        pilha.push(v);
    }
    
    private void dfsTransposto(int v, Set<Integer> visitados, Set<Integer> componente, Map<Integer, Set<Integer>> transposto) {
        visitados.add(v);
        componente.add(v);
        for (int w : transposto.getOrDefault(v, new HashSet<>())) {
            if (!visitados.contains(w)) {
                dfsTransposto(w, visitados, componente, transposto);
            }
        }
    }
    
    private Map<Integer, Set<Integer>> transporGrafo() {
        Map<Integer, Set<Integer>> transposto = new HashMap<>();
        for (int v : adj.keySet()) {
            for (int w : adj.get(v).keySet()) {
                transposto.putIfAbsent(w, new HashSet<>());
                transposto.get(w).add(v);
            }
        }
        return transposto;
    }     

    public TGrafoND grafoReduzido() {
        TGrafoND grafoReduzido = new TGrafoND(tipoGrafo);
        for (int v : adj.keySet()) {
            if (!adj.get(v).isEmpty()) {
                grafoReduzido.inserirVertice(v, identificacaoVertices.get(v), pesosVertices.getOrDefault(v, 0.0f));
                for (Map.Entry<Integer, Float> entry : adj.get(v).entrySet()) {
                    grafoReduzido.inserirAresta(v, entry.getKey(), entry.getValue());
                }
            }
        }
        return grafoReduzido;
    }

    public void gravarArquivo(String nomeArquivo) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(nomeArquivo));
        
        // Escreve o tipo do grafo
        writer.write(tipoGrafo + "\n");
        
        // Escreve o número de vértices
        writer.write(adj.size() + "\n");
        
        // Escreve os vértices com identificação e pesos
        for (int v : adj.keySet()) {
            String identificacao = identificacaoVertices.getOrDefault(v, "");
            float peso = pesosVertices.getOrDefault(v, 0.0f);
            writer.write(v + " \"" + identificacao + "\" " + peso + "\n");
        }
        
        // Escreve o número de arestas
        writer.write(m + "\n");
        
        // Escreve as arestas com seus pesos
        for (int v : adj.keySet()) {
            for (Map.Entry<Integer, Float> entry : adj.get(v).entrySet()) {
                writer.write(v + " " + entry.getKey() + " " + entry.getValue() + "\n");
            }
        }
        
        writer.close();
    }
        
    public void lerArquivo(String nomeArquivo) throws IOException {
        File file = new File(System.getProperty("user.dir"), nomeArquivo);
    
        if (!file.exists()) {
            throw new FileNotFoundException("Arquivo não encontrado: " + file.getAbsolutePath());
        }
    
        BufferedReader reader = new BufferedReader(new FileReader(file));
        tipoGrafo = Integer.parseInt(reader.readLine().trim());
        System.out.println("Tipo do grafo: " + tipoGrafo);
    
        int numVertices = Integer.parseInt(reader.readLine().trim());
        System.out.println("Vértices: " + numVertices);
    
        for (int i = 0; i < numVertices; i++) {
            String linha = reader.readLine().trim();
            String[] partes = linha.split("\"");
            int vertice = Integer.parseInt(partes[0].trim());
            String identificacao = partes.length > 1 ? partes[1].trim() : "";
            float peso = partes.length > 2 ? Float.parseFloat(partes[2].trim()) : 0.0f;
            inserirVertice(vertice, identificacao, peso);
        }
    
        int numArestas = Integer.parseInt(reader.readLine().trim());
        System.out.println("Arestas: " + numArestas);
        

        String linha;
        while ((linha = reader.readLine()) != null) {
            String[] partes = linha.trim().split("\\s+");
            if (partes.length != 3) {
                throw new IOException("Formato de linha inválido: " + Arrays.toString(partes));
            }
            int origem = Integer.parseInt(partes[0]);
            int destino = Integer.parseInt(partes[1]);
            float peso = Float.parseFloat(partes[2]);
            inserirAresta(origem, destino, peso);
            System.out.println("Aresta inserida: " + origem + " -> " + destino + " (" + peso + ")");
        }
    
        reader.close();
    }    

    public void show() {
        System.out.println("Tipo do grafo: " + tipoGrafo);
        System.out.println("Número de vértices: " + adj.size());
        System.out.println("Número de arestas: " + m);

        for (int v : adj.keySet()) {
            System.out.print(v + " \"" + identificacaoVertices.get(v) + "\"");
            if (tipoGrafo == 1 || tipoGrafo == 3 || tipoGrafo == 5 || tipoGrafo == 7) {
                System.out.print(" (Peso: " + pesosVertices.getOrDefault(v, 0.0f) + ")");
            }
            System.out.print(": ");
            for (Map.Entry<Integer, Float> entry : adj.get(v).entrySet()) {
                System.out.print(entry.getKey());
                if (tipoGrafo == 2 || tipoGrafo == 3 || tipoGrafo == 6 || tipoGrafo == 7) {
                    System.out.print("(" + entry.getValue() + ")");
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public int getTipoGrafo() {
        return tipoGrafo;
    }

    public void calcularMaiorDistanciaMediaAdjacentes() {
        int maiorDistanciaMediaVertice = -1; // Vértice com a maior distância média
        double maiorDistanciaMedia = -1;     // Valor da maior distância média
    
        // Percorre cada vértice para calcular a distância média dos adjacentes
        for (Integer vertice : adj.keySet()) {
            double distanciaMedia = calcularDistanciaMediaAdjacentes(vertice);
            if (distanciaMedia > maiorDistanciaMedia) {
                maiorDistanciaMedia = distanciaMedia;
                maiorDistanciaMediaVertice = vertice;
            }
        }
    
        // Se encontrou um vértice com maior distância média, imprime os detalhes
        if (maiorDistanciaMediaVertice != -1) {
            System.out.println("Vértice com maior distância média: " + maiorDistanciaMediaVertice);
            System.out.println("Identificação do vértice: " + identificacaoVertices.get(maiorDistanciaMediaVertice));
            System.out.println("Distância média: " + maiorDistanciaMedia);
    
            // Obtém e imprime os vizinhos e suas identificações
            System.out.println("Vizinhos:");
            for (Integer vizinho : adj.get(maiorDistanciaMediaVertice).keySet()) {
                String identificacaoVizinho = identificacaoVertices.get(vizinho);
                System.out.println("- Vértice: " + vizinho + ", Identificação: " + identificacaoVizinho);
            }
        }
    }

    private double calcularDistanciaMediaAdjacentes(int vertice) {
        if (!adj.containsKey(vertice) || adj.get(vertice).isEmpty()) {
            return 0; 
        }

        double somaDistancias = 0;
        int numAdjacentes = 0;

        for (Map.Entry<Integer, Float> vizinho : adj.get(vertice).entrySet()) {
            somaDistancias += vizinho.getValue();
            numAdjacentes++;
        }

        return somaDistancias / numAdjacentes;
    }

    // Método para imprimir o grau de cada vértice com seu nome
    public void calcularGraus() {
        for (Integer vertice : adj.keySet()) {
            // O grau de cada vértice é o tamanho do mapa de adjacências associado a ele
            int grau = adj.get(vertice).size();
            System.out.println("Grau do vértice " + vertice + ": " + grau);
        }
    }
    

    public boolean temCicloEuleriano() {
        String conexidade = this.isConexo();
    
        if (conexidade.equals("O grafo é desconexo (C0).")) return false;
    
        // Verifica se todos os vértices têm grau par
        for (Integer vertice : adj.keySet()) {
            if (adj.get(vertice).size() % 2 != 0) {
                return false;
            }
        }
        return true;
    }

        // Verifica se o grafo possui um ciclo Hamiltoniano
        public boolean temCicloHamiltoniano() {
            int n = adj.size(); // Número de vértices
            if (n < 3) {
                // Grafos com menos de 3 vértices não podem ser hamiltonianos
                return false;
            }
    
            // Verificar Teorema de Dirac
            boolean dirac = verificaTeoremaDirac(n);
            if (dirac) {
                return true; // Se o grafo satisfaz o Teorema de Dirac, é hamiltoniano
            }
    
            // Verificar Teorema de Ore
            boolean ore = verificaTeoremaOre(n);
            return ore; // Se o grafo satisfaz o Teorema de Ore, é hamiltoniano
        }
    
        // Função para verificar o Teorema de Dirac
        private boolean verificaTeoremaDirac(int n) {
            for (int v : adj.keySet()) {
                if (adj.get(v).size() < n / 2.0) {
                    return false; // Se algum vértice tem grau menor que n/2, falha
                }
            }
            return true; // Todos os vértices têm grau >= n/2
        }
    
        // Função para verificar o Teorema de Ore
        private boolean verificaTeoremaOre(int n) {
            // Verificar para todos os pares de vértices não adjacentes
            for (int u : adj.keySet()) {
                for (int v : adj.keySet()) {
                    if (u != v && !adj.get(u).containsKey(v)) { // Se u e v não são adjacentes
                        if (adj.get(u).size() + adj.get(v).size() < n) {
                            return false; // Se a soma dos graus for menor que n, falha
                        }
                    }
                }
            }
            return true; // Para todos os pares não adjacentes, a soma dos graus é >= n
        }
}

