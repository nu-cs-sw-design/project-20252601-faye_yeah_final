# Project: Exploding Kittens

## Contributors
Yunfei Ge

## Dependencies
- JDK 11
- JUnit 5.10
- Gradle 8.10

## Design Architecture

This project follows a **Three-Layer Architecture** to ensure a clean separation of concerns and improve maintainability.

### 1. Presentation Layer (`ui` package)
- **Responsibility**: Handles user interactions and renders game state without manipulating domain objects directly.
- **Key Components**:
  - `GameView`: Interface describing how the UI presents information and collects `PlayerAction`.
  - `GameUI`: Console implementation of `GameView`, responsible for prompts, localization (`ResourceBundle`, `MessageFormat`), and formatting.
  - `GameController`: Coordinates the overall loop—pulls `PlayerAction` from the view, invokes the application layer, then pushes `GameStateDto` back to the view.
  - `Main`: Composition root that wires repositories, services, controller, and UI together.

### 2. Application & Domain Layer (`domain.game` package)
- **Responsibility**: Encapsulates business rules/state and exposes use-case–level APIs to the presentation layer.
- **Key Components**:
  - `GameUseCase`: Boundary interface consumed by `GameController`.
  - `GameApplicationService`: Implements `GameUseCase`, orchestrates domain model operations, and maps them to DTOs.
  - DTOs / boundary models: `GameStateDto`, `PlayerPublicInfo`, `PlayerAction`, `GameConfig`, `ActionType`.
  - Domain model: `Game`, `Player`, `Deck`, `Card`, `CardType`, etc., which contain the actual rules (turn management, attacks, card effects).

### 3. Data Source Layer (`datasource` package)
- **Responsibility**: Provides persistence/object-creation infrastructure while staying behind interfaces.
- **Key Components**:
  - `GameRepository`: Abstraction for saving/loading the current `Game`.
  - `InMemoryGameRepository`: Default implementation; can be replaced by other storage mechanisms without touching upper layers.
  - `Instantiator`: Factory-style helper that builds domain objects and supplies randomness (`SecureRandom`), keeping creation logic out of the domain.
