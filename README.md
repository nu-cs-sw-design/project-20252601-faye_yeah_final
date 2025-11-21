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
- **Responsibility**: Handles all user interactions and input/output operations.
- **Key Components**: 
  - `GameUI`: Manages the console interface, user prompts, and localized messages.
  - `Main`: The entry point of the application.

### 2. Domain Layer (`domain.game` package)
- **Responsibility**: Encapsulates the core business logic, state, and rules of the game.
- **Key Components**:
  - `Game`: Controls the game flow, turn management, and player actions.
  - `Player`: Represents a game participant and manages their hand.
  - `Deck`: Manages the collection of cards. **Note**: While `Deck` stores data, it is placed in the Domain Layer because it contains essential game logic (e.g., `shuffleDeck`, `initializeDeck`, bomb insertion rules).
  - `Card` / `CardType`: Defines the fundamental entities of the game.

### 3. Data Source Layer (`datasource` package)
- **Responsibility**: Provides infrastructure for object creation and data sourcing.
- **Key Components**:
  - `Instantiator`: A factory-like class responsible for creating object instances (like Cards) and providing randomness (`SecureRandom`). This isolates the "source" of objects from the logic that uses them.
