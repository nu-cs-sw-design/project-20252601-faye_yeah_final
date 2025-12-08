## Software Design Document

This document compares the **original design** (`given.puml`) with the **refactored design** (`refactor.puml`), with a particular focus on four key cards in *Exploding Kittens*:

* Exploding Kitten
* Nope
* Shuffle
* Draw From Bottom

The PlantUML class diagrams (`given.puml` and `refactor.puml`) are used to support the explanations below.

---

## 1. Baseline Design (`given.puml`)

### 1.1 Structural Characteristics

* **Card**

  * Stores only `CardType` and some flags (e.g., marked state).
  * Does **not** own any behavior.

* **Game**

  * Contains most of the core rules:

    * Explosion resolution.
    * Shuffling logic.
    * Attack queue.
    * Draw-from-bottom.
    * Marking cards, etc.

* **GameUI**

  * Responsible for both **user interaction** and **game logic decisions**, including:

    * Using `switch` / `if-else` on `CardType` to determine what each card does.
    * Implementing the Nope chain.
    * Handling Exploding Kitten / Defuse resolution.
    * Asking for shuffle times and executing the shuffle.

### 1.2 Problems in the Original Design

* **High coupling between UI and rules**

  * `GameUI` is tightly coupled to game rules and card types.
  * Hard to replace the UI (e.g., switch to GUI or network) without touching rules.
  * Hard to unit-test card behavior without going through the console UI.

* **Poor maintainability**

  * Adding or changing a card often requires changes in **both** `Game` and `GameUI`.
  * Nope-related edge cases are easy to miss because they are scattered in UI logic.

* **Violation of Single Responsibility Principle**

  * `GameUI` behaves like a God Class:

    * It knows about user interaction, game flow, and detailed card rules.

---

## 2. Refactored Design (`refactor.puml`)

> **Goal:** For Exploding Kitten, Nope, Shuffle, and Draw From Bottom, introduce a **card-effect-centric architecture**, where each card owns its behavior through composition. Game rules should be extendable and maintainable **without modifying the UI layer**.

### 2.1 Core Elements

* **Card**

  * Holds two strategy-like collaborators:

    * `CardEffect useEffect`
    * `DrawTrigger drawTrigger`
  * Provides:

    * `executeUseEffect(game, player, input, output)`
    * `executeDrawTrigger(game, player, input, output)`
  * Behavior is now “attached” to the card itself instead of being handled in `GameUI`.

* **CardEffect**

  * Interface:

    * `execute(game, player, input, output)`
    * `canExecute(game, player)`
  * Concrete implementations include:

    * `ShuffleEffect`
    * `DrawFromBottomEffect`
    * `ExplodingKittenEffect`
    * `AttackEffect`
    * etc.

* **DrawTrigger**

  * Models “what happens when this card is drawn”.
  * Examples:

    * `ExplodingKittenTrigger`
    * `NormalDrawTrigger`
    * `StreakingKittenTrigger`

* **NOPEInterceptor (Decorator)**

  * Implements `CardEffect` and wraps another effect.
  * Wraps only effects that implement `NopableEffect`:

    * `NopableEffect` is a **marker interface extending `CardEffect`**, used for effects that can be canceled by Nope (e.g., `AttackEffect`).
  * Before executing the wrapped effect, it checks whether other players want to play Nope and resolves the Nope chain.

* **CardEffectFactory / CardFactory**

  * Map `CardType` to the correct:

    * `CardEffect useEffect`
    * `DrawTrigger`
  * Decide whether a given effect should be wrapped in `NOPEInterceptor` (e.g., Attack can be Noped, while some other effects might not be).
  * Centralize the construction logic for the four key cards.

* **InputProvider / OutputProvider**

  * Abstract away input and output:

    * e.g., `ConsoleInput`, `ConsoleOutput`.
  * `CardEffect` and `DrawTrigger` depend on these abstractions instead of directly using `Scanner` / `System.out`.

* **ShuffleStrategy**

  * Encapsulates shuffling algorithms (e.g., Fisher–Yates).
  * `ShuffleEffect` composes a `ShuffleStrategy` to perform the actual shuffle.

---

## 3. Major Design Changes and Rationale

Below is the **list of major changes** and an explanation of **why each change was needed** from an OOP perspective.

---

### **C1 – Card now owns behavior via `CardEffect` and `DrawTrigger`**

**Change**

* In the original design, `Card` was a pure data holder (only type + flags).
* In the refactored design, `Card` holds:

  * `CardEffect useEffect`
  * `DrawTrigger drawTrigger`
* `Card` exposes:

  * `executeUseEffect(...)`
  * `executeDrawTrigger(...)`

**Why this was needed**

* Moves from a **type-based, procedural switch** (in `GameUI`) to **object-based polymorphism**.
* Encapsulates “what the card does” inside the card itself, following **Encapsulation** and **Single Responsibility Principle (SRP)**.
* When adding a new card type, you simply:

  * Implement a new `CardEffect` / `DrawTrigger`.
  * Wire it in via `CardFactory`.
* `GameUI` no longer needs a long `switch (cardType)` and is no longer responsible for rules logic.

---

### **C2 – Introduce `CardEffect` interface and concrete effect classes**

**Change**

* Extracted a dedicated interface:

  ```java
  interface CardEffect {
      EffectResult execute(...);
      boolean canExecute(...);
  }
  ```
* Implementations:

  * `ShuffleEffect`
  * `DrawFromBottomEffect`
  * `ExplodingKittenEffect`
  * `AttackEffect`
  * `SkipEffect`
  * etc.

**Why this was needed**

* This is a classic **Strategy Pattern**:

  * Each card effect is a separate class encapsulating one piece of behavior.
* Benefits:

  * Each effect can be **unit-tested independently**.
  * You can change the behavior of one card without touching others (improves **Open/Closed Principle**).
  * Clarifies responsibilities: `Game` coordinates the game; `CardEffect` knows how a card behaves.

---

### **C3 – Introduce `DrawTrigger` for draw-time effects**

**Change**

* Introduced `DrawTrigger` to model draw-time behavior, e.g.:

  * `ExplodingKittenTrigger`
  * `NormalDrawTrigger`
  * (potentially) `ImplodingKittenTrigger`, etc.
* `Card.executeDrawTrigger(...)` delegates to its `DrawTrigger`.

**Why this was needed**

* In the original design, draw-time effects were mixed into `Game` / `GameUI` flow logic.
* By extracting `DrawTrigger`:

  * The logic for “what happens when this card is drawn” is localized and reusable.
  * It avoids duplicating explosion/defuse logic in multiple places (e.g., end-of-turn, draw step).
  * Makes it easier to introduce new cards with custom draw-time behavior in the future.

---

### **C4 – Add `NOPEInterceptor` Decorator and `NopableEffect` marker interface**

**Change**

* Introduced:

  ```java
  interface NopableEffect extends CardEffect {
      // Marker interface for effects that can be NOPE’d
  }

  class NOPEInterceptor implements CardEffect {
      private final NopableEffect wrappedEffect;
      ...
  }
  ```

* Effects that can be canceled by Nope (e.g., `AttackEffect`) implement `NopableEffect`.

* `NOPEInterceptor` wraps `NopableEffect` and:

  * Before executing, asks other players if they want to play Nope.
  * Resolves the Nope chain (possibly multiple NOPEs).
  * Either cancels the original effect or lets it proceed.

**Why this was needed**

* In the original design, Nope resolution was deeply entangled with UI logic and card-specific code.

* This change applies two OOP ideas:

  1. **Decorator Pattern**

     * `NOPEInterceptor` decorates normal effects with extra logic (Nope chain handling).
     * This keeps the core effect behavior (e.g., Attack) clean and focused.

  2. **Type-Safe Domain Modeling**

     * Not all effects can be NOPE’d according to the game rules.
     * By introducing `NopableEffect extends CardEffect`, the type system explicitly encodes:

       > “Only some effects may be wrapped and canceled by Nope.”
     * This avoids scattering `if (cardCanBeNoped)` booleans or magic conditions in the UI.
     * It improves **Liskov Substitution Principle (LSP)** and keeps the design explicit and self-documenting.

* Overall, this design centralizes Nope handling, removes Nope-specific logic from the UI, and makes the rules easier to reason about and extend.

---

### **C5 – Introduce `CardEffectFactory` and `CardFactory`**

**Change**

* Created:

  * `CardEffectFactory` to map `CardType` to the appropriate `CardEffect` and (if applicable) wrap with `NOPEInterceptor`.
  * `CardFactory` to construct fully initialized `Card` objects with the correct `useEffect` and `drawTrigger`.

**Why this was needed**

* Previously, card construction logic and effect wiring were scattered (often in `GameUI` or `Game`).
* By centralizing this logic in factories:

  * You reduce duplication of `new` expressions and `if-else` chains.
  * Adding a new card type or changing whether a card is Nopable becomes a **local change** in the factory.
  * This strongly supports the **Open/Closed Principle**:

    * The rest of the system (Game/UI) does not need to change when new cards are added or existing card wiring is updated.

---

### **C6 – Abstract input/output via `InputProvider` and `OutputProvider`**

**Change**

* Introduced interfaces such as:

  * `InputProvider`
  * `OutputProvider`
* `CardEffect`, `DrawTrigger`, and other rule classes depend on these interfaces instead of concrete console APIs.

**Why this was needed**

* In the original design, rule code directly used:

  * `Scanner` for input.
  * `System.out.println` for output.

* That made the rules:

  * Hard to test (no easy way to inject test inputs or capture outputs).
  * Tied to a specific UI technology (console).

* By introducing `InputProvider` / `OutputProvider`:

  * You **decouple domain logic from presentation**.
  * The same rules can be reused in:

    * A console UI.
    * A GUI.
    * A networked or web-based UI.
    * Automated tests.
  * This follows the **Dependency Inversion Principle (DIP)**: high-level policy (game rules) should not depend on concrete I/O mechanisms.

---

### **C7 – Introduce `ShuffleStrategy` for pluggable shuffling algorithms**

**Change**

* Added a `ShuffleStrategy` abstraction.
* `ShuffleEffect` composes a `ShuffleStrategy` and delegates the shuffle operation to it (e.g., Fisher–Yates implementation).

**Why this was needed**

* Shuffling used to be a concrete algorithm embedded directly in the game logic or UI.

* Extracting it as a strategy:

  * Allows swapping or comparing different shuffling algorithms.
  * Supports easier testing (e.g., deterministic strategies for tests).
  * Keeps `ShuffleEffect` focused on *when* to shuffle, not *how*.

* This is another application of the **Strategy Pattern** and contributes to a cleaner separation of concerns.

---

## 4. Consistency Between Diagrams and Document

* `given.puml` reflects the original, UI-centric design, where:

  * `GameUI` contains both UI and rule logic.
  * `Card` is a passive data holder.
* `refactor.puml` captures the refactored, card-effect-centric architecture, and corresponds to changes **C1–C7** above.
* When new effect classes are added or the set of Nopable cards changes, both:

  * `refactor.puml`, and
  * This “Major Design Changes and Rationale” section
    should be updated to ensure the documentation remains aligned with the code.
