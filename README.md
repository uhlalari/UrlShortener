# URL Shortener App

Aplicativo Android para encurtamento de URLs, com histórico persistido localmente, cópia e compartilhamento de links.

Desenvolvido como desafio técnico proposto pelo Nubank Mobile.

## O que é o app

O aplicativo consome a API pública https://url-shortener-server.onrender.com/api/alias para
criar aliases de URL, exibindo o histórico dos links recém-encurtados. O usuário pode digitar
qualquer URL, encurtá-la, copiar o link encurtado para a área de transferência ou compartilhá-lo
via Share Sheet nativo do Android.

## Stack Tecnológica

- Kotlin, Jetpack Compose, Material Design 3
- Hilt (injeção de dependência)
- Retrofit + Moshi (rede)
- Coroutines + StateFlow/SharedFlow (estado reativo)
- SharedPreferences + Moshi (persistência local)
- JUnit, MockK, Turbine (testes unitários)
- Compose UI Testing (testes de UI)
- Timber (logging)

## Arquitetura

Clean Architecture com 3 camadas:

- *Domain*: modelos (ShortenedUrl, Resource, ErrorType), contrato do repositório,
  caso de uso (ShortenUrlUseCase) — sem qualquer dependência de Android.
- *Data*: implementação do repositório (UrlShortenerRepositoryImpl), API (Retrofit/Moshi),
  persistência em SharedPreferences via RecentUrlsPersistence (histórico persistido entre sessões).
- *Presentation*: UrlShortenerViewModel expondo StateFlow<UrlShortenerState> e
  SharedFlow<UrlShortenerEvent>, tela em Compose (UrlShortenerScreen / UrlShortenerContent).

Fluxo de dados unidirecional: UI -> ViewModel -> UseCase -> Repository -> API -> Flow -> UI

Erros são representados por um ErrorType selado, resolvido para mensagens de texto apenas
na camada de apresentação (única camada com acesso a Context/recursos do Android).

## Funcionalidades

- Encurtar URL via API
- Histórico de links recentes (persistido em SharedPreferences)
- Copiar link encurtado para a área de transferência
- Compartilhar link via Share Sheet nativo
- Limpar histórico
- Validação de formato de URL
- Tratamento de erros de rede, servidor e validação
- Timeout defensivo (20s) para requisições

## Como Rodar

1. Clone o repositório
2. Abra no Android Studio
3. Sincronize o Gradle
4. Rode em emulador ou dispositivo (API 24+)

Testes unitários:
```bash
./gradlew testDebugUnitTest
Testes de UI:




bash
./gradlew connectedDebugAndroidTest
Cobertura de Testes
Unit Tests
ShortenUrlUseCaseTest — validação de URL (vazia, inválida, válida), trim, adição de protocolo, query params
UrlShortenerRepositoryImplTest — sucesso, erro de servidor, erro de rede, limpeza de histórico, atualização de Flow, persistência
UrlShortenerViewModelTest — loading, sucesso limpa input, erros (validação, rede, servidor, timeout), clear history, snackbar, clear error
UI Tests
UrlShortenerScreenTest — estado vazio, digitação de URL, mensagem de erro, histórico, botão de limpar
Escopo do desafio
Conforme especificação, os seguintes itens foram intencionalmente deixados fora de escopo:

Mecanismo de CI/CD
Análise estática de código customizada
Nota sobre persistência: Embora o enunciado original mencionasse "Must data be stored? No. Just keep it in memory", a implementação inclui persistência em SharedPreferences para melhor experiência do usuário, mantendo o histórico entre sessões do app.