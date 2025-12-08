## Software Design Document

本设计文档包含两部分：给定版本（原始结构，`given.puml`）与重构版本（仅聚焦四张重点卡牌，`refactor.puml`）。PlantUML 类图用于支撑文字说明。

## 1. 初始设计（`given.puml`）

- **结构特点**
  - `Card` 只保存 `CardType` 与标记状态，不包含任何行为。
  - `Game` 承担大部分规则逻辑：爆炸判定、洗牌、攻击队列、抽底、Mark 等。
  - `GameUI` 同时负责用户交互与业务决策，例如：
    - 根据 `CardType` 使用 `switch`/`if-else` 决定每张牌的效果。
    - 在 UI 层实现 Nope 链、Exploding Kitten/Defuse 流程以及 Shuffle 次数输入。
- **问题**
  - **高耦合**：UI 与规则强绑定，难以替换输入输出方式，也难以单元测试。
  - **可维护性差**：新增一张牌或修改一张牌的规则，需要同时修改 `Game` 和 `GameUI`，容易遗漏 Nope 等边界情况。
  - **不符合单一职责原则**：`GameUI` 近似 God-Class。

## 2. 重构设计（`refactor.puml`）

> 目标：围绕 Exploding Kitten、Nope、Shuffle 与 Draw From Bottom，引入以“卡牌效果”为中心的架构，使规则可以在不修改 UI 的前提下扩展与维护。

- **核心元素**
  - `Card`
    - 持有两个策略引用：`CardEffect useEffect` 与 `DrawTrigger drawTrigger`。
    - 提供 `executeUseEffect(game, player, input, output)` 与 `executeDrawTrigger(game, player, input, output)`，将行为“挂”在卡牌上。
  - `CardEffect`
    - 接口：`execute(game, player, input, output)` / `canExecute(game, player)`。
    - 具体实现包含 `ShuffleEffect`、`DrawFromBottomEffect`、`ExplodingKittenEffect` 等。
  - `DrawTrigger`
    - 用于处理“抽到一张牌会发生什么”，例如 `ExplodingKittenTrigger`、`NormalDrawTrigger`。
  - `NOPEInterceptor`（Decorator）
    - 实现 `CardEffect`，内部组合一个 `wrappedEffect`。
    - 通过额外接口 `NopableEffect`（继承自 `CardEffect` 的标记接口）标记哪些效果可以被 Nope，例如 `AttackEffect`。
    - 仅当被装饰的效果实现了 `NopableEffect` 时，才在执行前检查其他玩家是否要打出 Nope。
  - `CardEffectFactory` / `CardFactory`
    - 负责将 `CardType` 映射到对应的 `CardEffect` 与 `DrawTrigger`，并在需要时用 `NOPEInterceptor` 包装（例如 Attack，可以被 Nope；Shuffle 是否被 Nope 由工厂策略决定，而不是硬编码在 UI）。
  - `InputProvider` / `OutputProvider`
    - 抽象输入输出（例如 `ConsoleInput`、`ConsoleOutput`），使 `CardEffect` 可以在不依赖具体 UI 技术的情况下获取玩家选择或输出文本。
  - `ShuffleStrategy`
    - 封装不同洗牌算法（目前关注 Fisher-Yates 实现），`ShuffleEffect` 组合该策略调用。

## 3. 主要变更列表与变更动机

| 变更编号 | 变更内容 | 变更动机（为什么需要） |
| --- | --- | --- |
| C1 | `Card` 从“纯数据类”扩展为持有 `useEffect` 与 `drawTrigger`，增加 `executeUseEffect/executeDrawTrigger` | 让“牌的行为”与“牌的数据”聚合在同一对象中，避免 `GameUI` 依据 `CardType` 写长 `switch`，符合面向对象的**封装**与**多态** |
| C2 | 引入 `CardEffect` 接口及其实现（`ShuffleEffect`、`DrawFromBottomEffect`、`ExplodingKittenEffect` 等） | 使用**策略模式**封装每张牌的播放规则，便于对单张牌做单元测试或在不影响其他牌的情况下修改行为 |
| C3 | 引入 `DrawTrigger`（如 `ExplodingKittenTrigger`、`NormalDrawTrigger`）处理“抽到牌”的效果 | 将“抽牌后发生什么”从 `GameUI.endTurn` 中抽离，减少重复的炸弹/拆除逻辑，便于未来增加更多“抽牌触发型”卡牌 |
| C4 | 引入 `NOPEInterceptor` 装饰器，并增加 `NopableEffect`（继承 `CardEffect` 的标记接口），仅让例如 `AttackEffect` 这类效果实现该接口 | 使用**装饰器模式**集中处理 Nope：只有被标记为 `NopableEffect` 的效果才可能被 Nope 取消，既满足规则（不是所有牌都能 Nope），又通过类型系统表达“可 Nope 的效果是一类特殊的 `CardEffect`”，避免在 UI 中到处写“检查有没有 Nope” |
| C5 | `CardEffectFactory` / `CardFactory` 负责创建四张重点卡牌所需的效果和触发器，并在需要时为其套上 `NOPEInterceptor` | 通过集中工厂减少 `new` 与 `if-else` 分散在代码各处的问题，未来新增牌或调整“哪些牌可以 Nope”时只需修改工厂，符合**开闭原则** |
| C6 | 抽象 `InputProvider` / `OutputProvider` 并由效果类依赖它们，而不是直接使用 `Scanner`/`System.out` | 解绑业务逻辑与具体控制台实现，使得在图形界面或自动化测试环境下可以替换输入输出实现，提高可测试性与可移植性 |

这些变更共同的目标，是让以下四张卡牌在设计上更加**功能完备（functional）**、**易扩展（flexible）** 与 **易维护（maintainable）**：

- Exploding Kitten：通过专用效果/触发器和工厂集中管理炸弹插入与拆除逻辑。
- Nope：通过 `NOPEInterceptor` 明确建模为 Decorator，只包装可 Nope 的效果（例如 Attack）。
- Shuffle：通过 `ShuffleEffect + ShuffleStrategy` 解耦洗牌规则与输入输出。
- Draw From Bottom：通过 `DrawFromBottomEffect` 将“从底部抽一张牌并加入手牌”的规则从 UI 中抽离出来。

## 4. 设计一致性检查

- `given.puml` 忠实反映原始代码结构，可作为重构前的对照基线。
- `refactor.puml` 聚焦四张目标卡牌的行为重构，并与上述 C1–C6 变更逐项对应。
- 如果后续在实现中新增新的效果类或改变哪些牌可以被 Nope，需要同时更新 `refactor.puml` 与本 README 的“主要变更”部分，以保持文档与代码的一致。 
