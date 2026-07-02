# URL Shortener App

Aplicativo Android para encurtamento de URLs com histórico em memória, cópia e compartilhamento de links.

## Stack Tecnológica

- Kotlin, Jetpack Compose, Material Design 3
- Hilt (injeção de dependência)
- Retrofit + Moshi (rede)
- Coroutines + StateFlow (estado reativo)
- JUnit, MockK, Turbine (testes unitários)
- Compose UI Testing (testes de UI)
- Timber (logging)
- Detekt (análise estática)
- GitHub Actions (CI/CD)

## Arquitetura

Clean Architecture com 3 camadas:

- **Domain**: modelos, contrato do repositório, casos de uso (sem dependência de Android)
- **Data**: implementação do repositório, API (Retrofit), armazenamento em memória
- **Presentation**: ViewModel com StateFlow/SharedFlow, tela em Compose

Fluxo de dados unidirecional: UI -> ViewModel -> UseCase -> Repository -> API -> Flow -> UI

## Funcionalidades

- Encurtar URL via API
- Histórico de links recentes (em memória)
- Copiar link encurtado para clipboard
- Compartilhar link via Share Sheet nativo
- Limpar histórico
- Validar formato de URL
- Tratamento de erro de rede

## Como Rodar

1. Clone o repositório
2. Abra no Android Studio
3. Sincronize o Gradle
4. Rode em emulador ou dispositivo (API 24+)

Testes unitários:
```bash
./gradlew testDebugUnitTest
```

Testes de UI:
```bash
./gradlew connectedDebugAndroidTest
```

Análise estática (Detekt):
```bash
./gradlew detekt
```

## Destaques Técnicos

### Error Handling Avançado
- Wrapper `Resource<T>` customizado para tratamento de estados (Success, Error, Loading)
- `ErrorType` selado para categorização de erros (Network, Server, Validation, Unknown)
- Logging estruturado com Timber para debug e produção

### Code Quality
- Detekt configurado para análise estática de código Kotlin
- GitHub Actions para CI/CD automatizado
- KDoc completo em classes e métodos públicos
- Previews do Compose para desenvolvimento visual

### Testes
- Testes unitários com MockK e Turbine
- Testes de UI com Compose Testing
- Cobertura de casos de uso e ViewModel

## Avaliação

- Architecture com boa separação de conceitos
- Unit tests
- UI testing
- State management (StateFlow + UI State imutável)
- Code organization sem code smells
- CI/CD automatizado
- Análise estática de código

## Notas de Implementação

- Dados mantidos apenas em memória conforme requisito do teste
- Corrigido bug de parse JSON: @Json(name = "_links") no campo links da API
- Corrigido testes: coEvery em vez de every para funções suspend
- Separado screen em stateful/stateless para testes de UI sem Hilt
- Adicionado Resource wrapper para error handling type-safe
