# Battle System

The **Battle System** ist initialized with two instances of `Team`. After or at initialization a `BattleSystem.Callbacks`-object must be provided:

```java
BattleSystem battle = new BattleSystem(teamHero, teamOpponent, null);

BattleSystem.Callbacks battleCB = new BattleSystem.Callbacks(){...};
```

## Sequence Diagram

```mermaid
sequenceDiagram
    participant C as Client
    participant BS as BattleSystem
    participant BS.CB as BattleSystem.Callbacks
    participant AI as AIPlayer


    Note over C,BS: Callbacks are called by the BattleSystem, if an external action is required.
    C->>BS: setCallbacks(...)

    loop while both teams are fit
        C->>BS: continueBattle()

        alt queue.peekNextSide() == OPPONENT
            activate BS
            BS->>BS: letAItakeTurn()
            BS->>AI: turn()
            deactivate BS
            activate AI
            AI->>AI: chooseTarget()
              Note over AI,BS: setChosenTarget() informs the BattlySystem about the chosen target of the next attack.
            AI->>BS: setChosenTarget(guardianTarget)
              Note over AI,BS: setChosenAttack() informs the BattleSystem about the ability used to perform the next attack.
            AI->>BS: setChosenAttack()
              Note over AI,BS: attack() calculates the chosen abilities effects on the attacked Guardian and saves the results in an AttackReport
            AI->>BS: attack()
              Note over AI,BS: applyAttack() applies the results from the latest AttackReport on the involved Guardians. After that, it brings the next Guardian from the queue to front and checks, if one of the teams is KO.
            AI->>BS: applyAttack()
            deactivate AI
        else queue.peekNextSide() == HERO
            activate BS
            BS->>BS.CB: onPlayersTurn()
            deactivate BS
            activate Player
            Player->>-BS: getActiveMonster()
            activate BS
            BS-->>-Player:activeGuardian
            activate Player
            Player->>BS: setChosenTarget(guardianTarget)
            Player->>BS: setChosenAttack(attack)
            Player->>BS: attack()
            Player->>BS: applyAttack()
            deactivate Player
        end
    end
```
