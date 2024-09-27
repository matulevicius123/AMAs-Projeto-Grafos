import re

def buscarRegex(regex, texto):
    match = re.search(regex, texto)
    return match.group(1) if match else None

# Dicionário para armazenar os valores dos nós
dicionario = {}

# Padrões regex para capturar os valores desejados
padraoID = r'id="(\d+)"'
padraoNome = r'mainText="([^"]+)"'
padraoFonte = r'source="(\d+)"'
padraoAlvo = r'target="(\d+)"'
padraoPeso = r'weight="([\d.]+)"'  # Tolerância para números decimais

# Caminhos dos arquivos de origem e destino
origem = input("Caminho do arquivo de origem: ")
destino = input("Caminho do arquivo de destino: ")

# Abrir o arquivo de destino para escrita com codificação utf-8
with open(destino, 'w', encoding='utf-8') as arquivo2:
    arquivo2.write("2\n")
    # Ler o conteúdo do arquivo de origem como uma única string
    with open(origem, 'r', encoding='utf-8') as arquivo:
        conteudo = arquivo.read()
    
    # Processar os nós
    vertices = []
    for match in re.finditer(r'<node[^>]+>', conteudo):
        linha = match.group(0)
        id_value = buscarRegex(padraoID, linha)
        main_text_value = buscarRegex(padraoNome, linha)
        if id_value and main_text_value:
            # Adicionar ao dicionário com tolerância
            if id_value not in dicionario:
                dicionario[id_value] = main_text_value
                vertices.append(f'{id_value} "{main_text_value}"')

    # Escrever a quantidade de vértices no arquivo de destino
    arquivo2.write(f"{len(vertices)}\n")
    for vertice in vertices:
        arquivo2.write(vertice + '\n')
    
    # Processar as arestas
    arestas = []
    for match in re.finditer(r'<edge[^>]+>', conteudo):
        linha = match.group(0)
        fonte = buscarRegex(padraoFonte, linha)
        alvo = buscarRegex(padraoAlvo, linha)
        peso = buscarRegex(padraoPeso, linha)
        if fonte and alvo and peso:
            # Adiciona a aresta mesmo se houver saltos nos IDs
            if fonte in dicionario and alvo in dicionario:
                arestas.append(f'{fonte} {alvo} {peso}')
    
    # Escrever a quantidade de arestas no arquivo de destino
    arquivo2.write(f"{len(arestas)}\n")
    for aresta in arestas:
        arquivo2.write(aresta + '\n')