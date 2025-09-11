# 💇‍♀️ LuxConnect - App de Agendamento de Salões de Beleza  

<p align="center">
  <img src="https://img.shields.io/badge/build-passing-brightgreen" alt="Build Status">
  <img src="https://img.shields.io/github/issues/Gondran85/TCC-BarbeariaLux alt="Issues">
  <img src="https://img.shields.io/github/forks/Gondran85/TCC-BarbeariaLux" alt="Forks">
  <img src="https://img.shields.io/github/stars/Gondran85/TCC-BarbeariaLux" alt="Stars">
  <img src="https://img.shields.io/github/license/Gondran85/TCC-BarbeariaLux" alt="License">
</p>

---


![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?logo=kotlin)  
![Firebase](https://img.shields.io/badge/Firebase-Backend-orange?logo=firebase)  
![Scrum](https://img.shields.io/badge/Agile-Scrum-green?logo=scrumalliance) 

---

## 📌 Sobre o Projeto  

O **LuxConnect** é um aplicativo móvel desenvolvido como parte do meu **Trabalho de Conclusão de Curso (TCC)** em Engenharia de Software.  
Ele tem como objetivo **simplificar a busca e o agendamento de serviços de beleza**, conectando clientes e salões em uma experiência centralizada, prática e intuitiva.  

---

## ✨ Funcionalidades  

- **Cadastro e Login** via **Firebase Authentication**  
- **Busca e Localização** com **Google Maps API** ( Em desenvolvimento ) 
- **Agendamento de Serviços** com redirecionamento para sites parceiros  
- **Painel para Salões**: gestão de horários, preços e serviços  
- **Notificações Push**: lembretes de agendamentos e promoções ( Em desenvolvimento )

> 🔮 **Futuro**: iOS, Web, Desktop, melhorias de UX e recomendações personalizadas  

---

## 🛠️ Tecnologias  

- **Kotlin (Multiplatform)**  
- **Jetpack Compose**  
- **Firebase (Auth, Firestore, Cloud Messaging)**  
- **Google Maps API**  
- **Arquitetura MVVM**  
- **Scrum como metodologia ágil**  

---

## 📊 Engenharia de Software no Projeto  

Este projeto aplica princípios fundamentais de **Engenharia de Software**:

- **Requisitos** bem definidos, documentados e validados (PERSEGUINE, 2016).  
- **Qualidade** garantida por testes, métricas e boas práticas de desenvolvimento (FREITAS, 2021).  
- **Design de Interação** centrado no usuário, priorizando usabilidade e acessibilidade (ALMEIDA; FREITAS, 2020; TEIXEIRA, 2019).  
- **Gestão Ágil** com Scrum, garantindo entregas iterativas e alinhadas às necessidades dos usuários (SABBAGH, 2013).  

---

## 📱 Fluxo do Usuário  

```mermaid
flowchart TD
    A[Login/Cadastro] --> B[Busca de Salões]
    B --> C[Visualização de Serviços]
    C --> D[Escolha de Horário]
    D --> E[Confirmação da marcação]
    E --> F[Notificações de Lembrete - desenvolvimento]
````

# LuxConnect - Sistema de Agendamento para Barbearias

Este projeto integra Firebase Cloud Firestore para gerenciar agendamentos de serviços em barbearias.

## 🔥 Integração Firebase Firestore

### Estrutura do Banco de Dados

O sistema utiliza as seguintes coleções no Firestore:

```
/usuarios/{usuarioId}
├── nome: string
├── email: string  
├── telefone: string (opcional)
└── dataCadastro: timestamp

/saloes/{salaoId}
├── nome: string
├── endereco: string (opcional)
├── telefone: string
├── servicos: array[string]
├── horarioFuncionamento: string
├── avaliacaoMedia: number
├── ativo: boolean
└── dataCadastro: timestamp

/agendamentos/{agendamentoId}
├── usuarioId: string
├── salaoId: string
├── tipoServico: string
├── horario: timestamp
├── status: string (AGENDADO, CONCLUIDO, CANCELADO, EM_ANDAMENTO)
├── observacoes: string (opcional)
├── preco: number (opcional)
└── dataAgendamento: timestamp
```

### Dependências Adicionadas

O projeto foi configurado com Firebase BoM e dependências necessárias:

- `firebase-bom`: Gerencia versões compatíveis do Firebase
- `firebase-firestore-ktx`: Firestore com extensões Kotlin
- `kotlinx-coroutines-android`: Para operações assíncronas
- `kotlinx-coroutines-play-services`: Integração coroutines com Firebase

### Arquitetura Implementada

#### 1. **Application Class** (`LuxConnectApplication.kt`)
- Inicializa o Firebase automaticamente
- Providencia acesso seguro ao Firestore
- Cache offline habilitado por padrão

#### 2. **Modelos de Dados** (Pacote `Model`)
- **Usuario**: Representa clientes da barbearia
- **Salao**: Representa estabelecimentos disponíveis  
- **Agendamento**: Representa reservas de serviços
- Todos com comentários em português e validações

#### 3. **Repositório** (`RepositorioFirestore.kt`)
- Padrão singleton para acesso ao banco
- Operações CRUD completas com Result pattern
- Flows para observação em tempo real
- Tratamento de erros robusto

#### 4. **ViewModels** (Pacote `Viewmodel`)
- **SaloesViewModel**: Gerencia lista de salões
- **AgendamentosViewModel**: Gerencia agendamentos do usuário
- **UsuarioViewModel**: Gerencia perfil e cadastro
- Seguem padrão MVVM com StateFlow

### Como Usar

#### Observar Salões em Tempo Real

```kotlin
// Na sua Activity/Fragment
private val saloesViewModel: SaloesViewModel by viewModels()

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Observa mudanças na lista de salões
    lifecycleScope.launch {
        saloesViewModel.saloes.collect { saloes ->
            // Atualiza a UI com a nova lista
            atualizarListaSaloes(saloes)
        }
    }
    
    // Observa estado de carregamento
    lifecycleScope.launch {
        saloesViewModel.carregando.collect { carregando ->
            mostrarIndicadorCarregamento(carregando)
        }
    }
}
```

#### Criar um Agendamento

```kotlin
private val agendamentosViewModel: AgendamentosViewModel by viewModels()

fun criarNovoAgendamento(usuarioId: String, salaoId: String) {
    val agendamento = Agendamento(
        usuarioId = usuarioId,
        salaoId = salaoId,
        tipoServico = "Corte de Cabelo",
        horario = Timestamp(dataEscolhida),
        observacoes = "Preferência por corte social"
    )
    
    agendamentosViewModel.criarAgendamento(agendamento)
    
    // Observa resultado
    lifecycleScope.launch {
        agendamentosViewModel.operacaoSucesso.collect { mensagem ->
            mensagem?.let {
                mostrarMensagemSucesso(it)
                navegarParaConfirmacao()
            }
        }
    }
}
```

#### Listar Agendamentos do Usuário

```kotlin
fun carregarAgendamentosUsuario(usuarioId: String) {
    agendamentosViewModel.carregarAgendamentosDoUsuario(usuarioId)
    
    lifecycleScope.launch {
        agendamentosViewModel.agendamentos.collect { agendamentos ->
            // Filtra apenas agendamentos ativos
            val ativos = agendamentosViewModel.obterAgendamentosAtivos()
            atualizarListaAgendamentos(ativos)
        }
    }
}
```

### Regras de Segurança

O arquivo `firestore.rules` implementa:

- **Usuários**: Só podem editar próprios dados
- **Salões**: Leitura pública, escrita apenas admin
- **Agendamentos**: Usuário só acessa próprios agendamentos
- Validações de campos obrigatórios
- Verificação de timestamps futuros

### Funcionalidades Principais

#### ✅ **Implementado**
- Inicialização automática do Firebase
- Modelos de dados em português
- Repositório com operações CRUD
- ViewModels seguindo padrão MVVM
- Cache offline automático
- Observação em tempo real com Flow
- Tratamento de erros robusto
- Regras de segurança do Firestore
- Comentários explicativos em português

#### 🎯 **Recursos Avançados**
- **Tempo Real**: Atualizações automáticas quando dados mudam
- **Offline**: Funciona sem internet com sincronização automática
- **Escalável**: Estrutura preparada para crescimento
- **Seguro**: Regras de acesso rigorosas
- **Performático**: Queries otimizadas e cache inteligente

### Próximos Passos

1. **Implementar Authentication**: Integrar Firebase Auth
2. **Adicionar Imagens**: Storage para fotos de perfil e salões
3. **Notificações**: Firebase Cloud Messaging para lembretes
4. **Analytics**: Firebase Analytics para métricas
5. **Busca Avançada**: Implementar busca por localização

### Estrutura de Arquivos Criados

```
app/src/main/java/com/jeffersongondran/tcc_barbearialux/
├── LuxConnectApplication.kt          # Inicialização Firebase
├── Model/
│   ├── Usuario.kt                    # Modelo de usuário
│   ├── Salao.kt                      # Modelo de salão
│   └── Agendamento.kt               # Modelo de agendamento
├── Repository/
│   └── RepositorioFirestore.kt      # Operações banco de dados
├── Viewmodel/
│   ├── SaloesViewModel.kt           # ViewModel para salões
│   ├── AgendamentosViewModel.kt     # ViewModel para agendamentos
│   └── UsuarioViewModel.kt          # ViewModel para usuários
└── Utils/
    └── ExemplosDeUso.kt            # Exemplos práticos

firestore.rules                      # Regras de segurança
```

### Observações Importantes

- Todos os nomes de classes, variáveis e comentários estão em português
- Seguiu-se o padrão arquitetural MVVM já existente no projeto
- Cache offline habilitado automaticamente (Firebase SDK mais recente)
- Tratamento de erros com Result pattern para robustez
- Documentação completa para facilitar manutenção

## 🚀 Como Testar

1. Certifique-se de que o `google-services.json` está configurado
2. Execute o projeto no emulador ou dispositivo
3. Use os exemplos em `ExemplosDeUso.kt` como referência
4. Monitore dados em tempo real no Firebase Console

---

**Implementação completa do Firebase Cloud Firestore realizada com sucesso!** 🎉
