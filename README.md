# Sistema de Reciclagem Inteligente

Discentes: Igor Daniel Toledo Campos, Islabelle Vitória Mendonça Bastos, Juliano Kenzo Watanabe Santana

## Descrição Geral

Este projeto consiste em um sistema interativo de reciclagem com recompensas, implementado em Java com interface gráfica baseada na biblioteca Swing. O objetivo principal é simular o funcionamento de lixeiras inteligentes que reconhecem e validam resíduos recicláveis, recompensando os usuários por suas ações sustentáveis.

A aplicação adota diversos padrões de projeto (Observer, State, Strategy, Decorator e Singleton) com o propósito de reforçar boas práticas de engenharia de software e arquitetura orientada a objetos.

## Propósito do Sistema

A finalidade do sistema é promover a educação ambiental e o engajamento dos usuários no descarte correto de resíduos recicláveis, oferecendo moedas virtuais como recompensa. Para isso, o sistema:

- Permite o cadastro e a seleção de usuários;
- Simula lixeiras para diferentes tipos de materiais recicláveis (plástico, vidro, metal e papel);
- Realiza a validação dos resíduos descartados com base em critérios como tipo e peso mínimo;
- Calcula e distribui recompensas conforme o tipo de material reciclado;
- Registra e exibe logs de ações realizadas.

Esse projeto tem aplicação didática em cursos de Ciência da Computação e Engenharia de Software, abordando tanto conceitos de interface gráfica quanto de padrões de projeto e persistência de dados.

##  Funcionalidades Implementadas

-  **Gestão de Usuários**
  - Criação de novos usuários com ID único;
  - Persistência dos dados em arquivo binário (`usuarios.dat`);
  - Exibição do saldo de moedas acumuladas por usuário.

-  **Simulação de Lixeiras Inteligentes**
  - Cada lixeira aceita apenas resíduos compatíveis com seu tipo;
  - Verificação de capacidade máxima da lixeira;
  - Notificação automática via padrão Observer ao receber resíduos.

-  **Validação de Resíduos (State + Decorator)**
  - Implementação do ciclo de validação com estados: Pendente, Aprovado, Rejeitado e Em Validação;
  - Regras encadeadas para verificação de tipo de material e peso mínimo.

-  **Sistema de Recompensas (Strategy + Singleton)**
  - Cálculo de moedas com base no tipo e peso do material:
    - Plástico: 10 moedas/kg
    - Vidro: 8 moedas/kg
    - Metal: 12 moedas/kg
    - Papel: 6 moedas/kg
  - Gestão centralizada e reutilizável via instância Singleton.

-  **Interface Gráfica com Swing**
  - Componentes interativos como JComboBox, JTextField e JTextArea;
  - Feedback visual em tempo real ao usuário;
  - Registro das ações no log textual da interface.

-  **Tratamento de Exceções Personalizadas**
  - `LixeiraException` para erros no descarte;
  - `SistemaArquivosException` para problemas de leitura e gravação de arquivos.

##  Tecnologias Utilizadas

- **Linguagem:** Java 11+
- **Bibliotecas:** Java Swing (javax.swing), Java IO e Collections
- **Paradigma:** Orientação a Objetos
- **Padrões de Projeto:**
  - Observer
  - State
  - Strategy
  - Decorator
  - Singleton

##  Estrutura do Projeto

```
├── SistemaReciclagemGUI.java        # Classe principal com a interface gráfica
├── Residuo.java                     # Representação dos resíduos e seus estados
├── Usuario.java                     # Representação dos usuários
├── LixeiraInteligente.java         # Simulação da lixeira com Observer
├── EstadoResiduo.java              # Interface State
├── EstadoPendente/Aprovado/...     # Estados concretos do resíduo
├── ValidadorResiduo.java           # Interface Decorator
├── CalculadoraRecompensa.java      # Interface Strategy
├── SistemaRecompensas.java         # Singleton de cálculo de recompensa
├── GerenciadorUsuarios.java        # Persistência de usuários em arquivo
├── LixeiraException.java           # Exceção personalizada para erros no descarte
└── usuarios.dat                    # Arquivo gerado para armazenamento de dados
```

##  Como Executar o Projeto

1. **Requisitos**
   - JDK 11 ou superior instalado
   - IDE compatível (Eclipse, IntelliJ, NetBeans) ou terminal

2. **Compilação**
   ```bash
   javac SistemaReciclagemGUI.java
   ```

3. **Execução**
   ```bash
   java SistemaReciclagemGUI
   ```

4. **Uso**
   - Crie um novo usuário;
   - Selecione o tipo de lixeira e o tipo de resíduo;
   - Informe o peso e descarte o resíduo;
   - Veja o resultado no log e acompanhe o saldo de moedas.

##  Considerações Finais

Este sistema representa uma aplicação prática de conceitos de orientação a objetos e padrões de projeto em um contexto de sustentabilidade. Pode ser expandido com novas funcionalidades, como:

- Relatórios de descarte por usuário;
- Armazenamento em banco de dados;
- Integração com sensores físicos (em um contexto IoT);
- Novos tipos de resíduos ou critérios de recompensa.
