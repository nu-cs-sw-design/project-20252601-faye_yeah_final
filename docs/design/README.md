# Software Design Document

本设计文档包含两部分：给定版本（原始结构）与重构版本（仅聚焦四张重点卡牌）。文件夹内同时提供 PlantUML 类图以支撑描述。

## 1. 初始设计（`given.puml`）

- **特点**：所有卡牌行为直接由 `Game` 与 `GameUI` 程序化控制，`Card` 只保存类型/标记状态。
- **结构亮点**：
  - `Game` 类承担多数规则（爆炸判定、Nope 链、洗牌、抽底等）。
  - `GameUI` 集成了大量用户交互与业务逻辑，存在 God-Class 风险。
  - 策略或工厂模式尚未引入，导致卡牌行为难于扩展或复用。

## 2. 重构设计（`new.puml`）

> 目标：围绕 Exploding Kitten、Nope、Shuffle 与 Draw From Bottom 引入可组合的策略/触发器架构，隔离规则与 UI。

- **核心元素**：
  - `Card` 拥有 `CardEffect` 与 `DrawTrigger` 两个策略引用，使“打出时”与“抽到时”逻辑可独立演进。
  - `EffectContext` 聚合 `Game`、`Player`、`Deck`、`UserInputProvider` 等运行期状态；所有效果类仅依赖上下文接口，降低耦合。
  - `CardEffectFactory`/`CardFactory` 负责创建卡与装饰，决定哪些效果需要 `NOPEInterceptor` 包装。
  - `NOPEInterceptor` 通过组合实现 Decorator/Chain of Responsibility，统一处理 Nope 判定，不再把条件散落在 `GameUI`。
  - `ExplodingKittenTrigger`、`NormalDrawTrigger` 等将抽牌后逻辑封装为 `DrawTrigger`。
  - `ShuffleStrategy`、`DrawStrategy` 等策略接口为洗牌和抽牌方式提供扩展点（本次重点在 Fisher-Yates 与 BottomDraw）。

## 3. 主要结构变更与动机

| 变更编号 | 内容 | 动机 |
| --- | --- | --- |
| C1 | 将 `Card` 扩展为持有 `useEffect` 与 `drawTrigger`，提供 `executeUseEffect/executeDrawTrigger` | 使卡牌行为与数据同处一体，避免 `GameUI` 依据 `CardType` 写冗长 `switch` |
| C2 | 新增 `EffectContext`/`EffectResult`/`DrawResult` DTO | 为效果链提供统一输入输出，便于测试与 Nope/Defuse 等跨组件协调 |
| C3 | 引入 `CardEffect`、`DrawTrigger` 及对应实现（`ShuffleEffect`、`DrawFromBottomEffect`、`ExplodingKittenTrigger`、`NOPEInterceptor`） | 使用策略/装饰模式隔离多种卡牌规则，提升组合能力 |
| C4 | 抽象 `ShuffleStrategy` 与 `DrawStrategy`，并让 `Deck`/`TurnEngine` 通过策略注入临时行为 | 轻松扩展新的洗牌或抽牌方式（如“从底部抽”），同时保证可回滚 |
| C5 | 引入 `CardEffectFactory` 与 `CardFactory` | 集中创建逻辑，确保 Nope 包装与策略注入的一致性；方便测试与未来扩展更多卡牌 |
| C6 | 新增 `NopeCoordinator` 负责问询输入/广播消息 | 将 UI 交互与 Nope 决策从 `GameUI` 中抽离，便于未来替换前端或实现自动化测试 |

## 4. 设计一致性检查

- **初始设计**满足“记录原始结构”的需求，可作为基线。
- **重构设计**覆盖需求中的四张卡牌：
  - `ExplodingKittenTrigger` & `DrawFromBottomEffect`
  - `ShuffleEffect`（基于 `ShuffleStrategy`）
  - `NOPEInterceptor`（绑定 `NopeCoordinator`）
- 文档与 PlantUML 保持同步：若后续新增效果/触发器，需更新 `new.puml` 并在本 README 中补充相应变更记录。

通过以上补充，设计交付项（初始图 + 新图 + 变更分析）已完整呈现，可支持实现阶段的代码重构与评审。

