# 💇‍♀️ LuxConnect - App de Agendamento de Salões de Beleza

<p align="center">
  <img src="https://img.shields.io/badge/build-passing-brightgreen" alt="Build Status">
  <img src="https://img.shields.io/github/issues/Gondran85/TCC-BarbeariaLux" alt="Issues">
  <img src="https://img.shields.io/github/forks/Gondran85/TCC-BarbeariaLux" alt="Forks">
  <img src="https://img.shields.io/github/stars/Gondran85/TCC-BarbeariaLux" alt="Stars">
  <img src="https://img.shields.io/github/license/Gondran85/TCC-BarbeariaLux" alt="License">
</p>

---

![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF?logo=kotlin)
![Firebase](https://img.shields.io/badge/Firebase-Firestore%20%7C%20Auth-orange?logo=firebase)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-blue)

---

## 📌 Sobre o Projeto

O **LuxConnect** é um aplicativo Android que simplifica a busca e o agendamento de serviços em barbearias, conectando clientes e salões em uma experiência prática e intuitiva.

---

## ✨ Funcionalidades

- Autenticação: cadastro e login com Firebase Authentication
- Salões em tempo real: listagem com atualização automática via Firestore
- Agendamentos: criação, listagem por usuário e cancelamento
- Favoritos: marcar/desmarcar salões e observar em tempo real
- Busca de salões: por prefixo de nome (startAt/endAt)

> Próximos: notificações, imagens e analytics

---

## 🛠️ Tecnologias

- Kotlin (Android) + AndroidX (AppCompat, ConstraintLayout)
- View Binding
- Arquitetura MVVM (ViewModel + StateFlow)
- Coroutines (kotlinx-coroutines-android / play-services)
- Firebase BoM + Firestore KTX
- Firebase Authentication (+ FirebaseUI Auth opcional)
- Material Design

---

## 📊 Engenharia de Software no Projeto

- Requisitos definidos e evolutivos
- Qualidade com boas práticas e tratamento robusto de erros
- MVVM para separação de responsabilidades
- Dados reativos (Flow/StateFlow) e cache offline do Firestore

---

## 📱 Telas Principais

- IntroActivity, SpashActivity, LoginActivity, SignUpActivity, ForgotPasswordActivity
- MainActivity (hub), EscolhaServicoActivity, ConfirmacaoActivity

## 🧠 ViewModels

- SaloesViewModel, AgendamentosViewModel, UsuarioViewModel
- FavoritosViewModel, ProcurarViewModel, EscolhaServicoViewModel, MainViewModel

## 🗂️ Repositórios

- RepositorioFirestore (CRUD + streams em tempo real)
- BarberRepository, EscolhaServicoRepository

---

## LuxConnect - Sistema de Agendamento para Barbearias

Este projeto integra Firebase Cloud Firestore para gerenciar agendamentos de serviços em barbearias.

### 🔥 Integração Firebase Firestore

#### Estrutura do Banco de Dados

```
/usuarios/{usuarioId}
├── nome: string
├── email: string  
├── telefone: string (opcional)
├── dataCadastro: timestamp
└── favoritos/{salaoId}
    └── adicionadoEm: timestamp

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

#### Dependências (Gradle)

- Firebase BoM + Firestore KTX
- Coroutines (Android + Play Services)
- Lifecycle ViewModel / LiveData
- Firebase Auth (+ FirebaseUI Auth opcional)

#### Arquitetura Implementada

1. Application Class (LuxConnectApplication.kt)
   - Inicializa o Firebase e configura Firestore offline
2. Modelos de Dados (Model)
   - Usuario, Salao, Agendamento (com validações e comentários)
3. Repositório (RepositorioFirestore.kt)
   - CRUD, Flows em tempo real, Result pattern e favoritos
4. ViewModels (Viewmodel)
   - StateFlow para estados, operações assíncronas com coroutines

---

## ▶️ Como Usar (exemplos)

### Observar Salões em Tempo Real

```kotlin
private val saloesViewModel: SaloesViewModel by viewModels()

lifecycleScope.launch {
    saloesViewModel.saloes.collect { saloes ->
        atualizarListaSaloes(saloes)
    }
}
```

### Criar um Agendamento

```kotlin
private val agendamentosViewModel: AgendamentosViewModel by viewModels()

val agendamento = Agendamento(
    usuarioId = usuarioId,
    salaoId = salaoId,
    tipoServico = "Corte de Cabelo",
    horario = Timestamp(dataEscolhida),
    observacoes = "Preferência por corte social"
)
agendamentosViewModel.criarAgendamento(agendamento)
```

### Favoritar/Desfavoritar um Salão

```kotlin
viewModelScope.launch {
    RepositorioFirestore.getInstance().alternarFavorito(usuarioId, salaoId)
}
```

---

### Regras de Segurança

- Usuários: só editam os próprios dados
- Salões: leitura pública, escrita apenas admin
- Agendamentos: usuário só acessa os seus
- Campos obrigatórios validados e horários futuros verificados
- Subcoleção de favoritos sob cada usuário

### Funcionalidades Principais

- Inicialização automática do Firebase e cache offline
- Modelos em português + documentação
- Repositório com CRUD e Flows em tempo real
- MVVM com StateFlow e coroutines
- Regras de segurança do Firestore

### Próximos Passos

- Notificações (Firebase Cloud Messaging)
- Imagens (Firebase Storage)
- Analytics (Firebase Analytics)
- Busca por localização

---

## 🗃️ Estrutura de Pastas (resumo)

```
app/src/main/java/com/jeffersongondran/tcc_barbearialux/
├── LuxConnectApplication.kt
├── Model/
│   ├── Usuario.kt
│   ├── Salao.kt
│   └── Agendamento.kt
├── Repository/
│   ├── RepositorioFirestore.kt
│   ├── BarberRepository.kt
│   └── EscolhaServicoRepository.kt
├── Viewmodel/
│   ├── SaloesViewModel.kt
│   ├── AgendamentosViewModel.kt
│   ├── UsuarioViewModel.kt
│   ├── FavoritosViewModel.kt
│   ├── ProcurarViewModel.kt
│   ├── EscolhaServicoViewModel.kt
│   └── MainViewModel.kt
└── View/
    ├── MainActivity.kt
    ├── LoginActivity.kt
    ├── SignUpActivity.kt
    ├── ForgotPasswordActivity.kt
    ├── IntroActivity.kt
    ├── SpashActivity.kt
    ├── EscolhaServicoActivity.kt
    └── ConfirmacaoActivity.kt

firestore.rules
```

## 🚀 Como Testar

1. Garanta o `app/google-services.json` configurado
2. Build e execute no emulador/dispositivo
3. Cadastre/login e teste: favoritar, buscar e agendar
4. Acompanhe as coleções no Firebase Console em tempo real

---

Implementação do Firebase Cloud Firestore finalizada e alinhada ao estado atual do projeto. 🚀
