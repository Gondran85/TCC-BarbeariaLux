# 💇‍♀️ BarbeariaLux - App de Agendamento de Salões de Beleza  

<p align="center">
  <img src="https://img.shields.io/badge/build-passing-brightgreen" alt="Build Status">
  <img src="https://img.shields.io/github/issues/Gondran85/TCC-BarbeariaLux alt="Issues">
  <img src="https://img.shields.io/github/forks/Gondran85/TCC-BarbeariaLux" alt="Forks">
  <img src="https://img.shields.io/github/stars/Gondran85/TCC-BarbeariaLux" alt="Stars">
  <img src="https://img.shields.io/github/license/Gondran85/TCC-BarbeariaLux" alt="License">
</p>

---


## 📌 Sobre o Projeto  

O **BarbeariaLux** é um aplicativo móvel desenvolvido como parte do meu **Trabalho de Conclusão de Curso (TCC)** em Engenharia de Software.  
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

## 📱 Fluxo do Usuário  

```mermaid
flowchart TD
    A[Login/Cadastro] --> B[Busca de Salões]
    B --> C[Visualização de Serviços]
    C --> D[Escolha de Horário]
    D --> E[Confirmação da marcação]
    E --> F[Notificações de Lembrete - desenvolvimento]
