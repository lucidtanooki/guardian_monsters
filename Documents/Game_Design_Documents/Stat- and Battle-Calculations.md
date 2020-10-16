# Calculations

## Base Values vs. Stat Values

Stats are a Guardians points of HP, MP, PStr, PDef, MStr, MDef and Speed. They are calculated from it's base values. Individual Base values can be interpreted as some sort of a Guardians *genes*. Common base values can be considered a species' common properties. And growth base values are a Guardians "experience".

## Common Base Values (Species)

Every species has a set of base values for the various Stats. These base values are the same for every Guardian of the same species. Every Kroki has the same base values, no matter what. Guardians only differ in *Individual Base Values* and *Growth Values*. The *Common Base Values* stay the same, always, even at level-up.

## Individual Base Values (Genes)

Every Guardian has individual base values of HP, MP, PStr, PDef, MStr, MDef and Speed. These values make every Guardian unique. The values are given at birth and will never change. They can range from:

|Stat|minimum|maximum|
|-----------------------|-------------------------|----------------------------|
|HP   |0   |63   |
|MP   |0   |63   |
|PStr   |0   |15   |
|PDef   |0   |15   |
|MStr   |0   |15   |
|MDef   |0   |15   |
|Speed   |0   |15


## Growth Base Values (Experience)

At level-up Guardians gain some extra points on their Stats. This gain is partly random, so it makes Guardians even more unique.

## Equipment Stats

Equipment can add some extra points to the Guardians Stat values. Equipment raising HP or MP work as fractions. An **HP +10%** helmet raises the Guardians *common + individual base HP value* to 110%. Equipment improving PStr, PDef, MStr, MDef or Speed work additionally. Shoes with **Speed +5** raise a Guardians full Speed Stat from e.g. 50 to 55.


## Damage Calculation

$$\Delta = \epsilon \cdot \left({{(0.5 \cdot \lambda + 1) \cdot \Delta_a \cdot {\Sigma \over \Theta} + 50} \over {5}}\right)$$

* $\Delta$ ... damage caused by this ability
* $\epsilon$ ... elemental efficiency multiplier
* $\lambda$ ... level
* $\Delta_a$ ... ability damage
* $\Sigma$ ... physical or magical strength of the attacking guardian, depending on ability type
* $\Theta$ ... physical or magical defense of the defending guardian, depending on ability type

## Stat Calculations

### HP

$$HP = \lambda \cdot 10 + 100 + \gamma \cdot \lfloor {{2 \cdot HP_c + HP_{ib} + HP_g \cdot \lambda} \over {10}} \rfloor$$

* $HP$ ... Health Points
* $\lambda$ ... Level
* $\gamma$ ... Character Factor (balanced, viviacious, prudent)
* $HP_c$ ... common HP value, equal for all Guardians of this species
* $HP_{ib}$ ... indiviual base HP value, unique to every indiviual Guardian, decided at birth
* $HP_g$ ... growth HP value, growing on every level-up

### MP

$$MP = \lambda \cdot 2 + 20 + \gamma \cdot \lfloor {{2 \cdot MP_c + MP_{ib} + MP_g \cdot \lambda} \over {20}} \rfloor$$

* $MP$ ... Magical Points
* $\lambda$ ... Level
* $\gamma$ ... Character Factor (balanced, viviacious, prudent)
* $MP_c$ ... common MP value, equal for all Guardians of this species
* $MP_{ib}$ ... indiviual base MP value, unique to every indiviual Guardian, decided at birth
* $MP_g$ ... growth MP value, growing on every level-up

### PStr

$$\Sigma_{\pi} = \lambda + 50 + \gamma \cdot \lfloor {{2 \cdot \Sigma_{\pi c} + \Sigma_{\pi ib} + \Sigma_{\pi g} \cdot \lambda} \over {10}} \rfloor$$

* $\Sigma_{\pi}$ ... physical strength
* $\lambda$ ... Level
* $\gamma$ ... Character Factor (balanced, viviacious, prudent)
* $\Sigma_{\pi c}$ ... common PStr value, equal for all Guardians of this species
* $\Sigma_{\pi ib}$ ... indiviual base PStr value, unique to every indiviual Guardian, decided at birth
* $\Sigma_{\pi g}$ ... growth PStr value, growing on every level-up

### PDef

$$\Theta_{\pi} = \lambda + 50 + \gamma \cdot \lfloor {{2 \cdot \Theta_{\pi c} + \Theta_{\pi ib} + \Theta_{\pi g} \cdot \lambda} \over {10}} \rfloor$$

* $\Theta_{\pi}$ ... physical defense
* $\lambda$ ... Level
* $\gamma$ ... Character Factor (balanced, viviacious, prudent)
* $\Theta_{\pi c}$ ... common PDef value, equal for all Guardians of this species
* $\Theta_{\pi ib}$ ... indiviual base PDef value, unique to every indiviual Guardian, decided at birth
* $\Theta_{\pi g}$ ... growth PDef value, growing on every level-up

### MStr

$$\Sigma_{\mu} = \lambda + 50 + \gamma \cdot \lfloor {{2 \cdot \Sigma_{\mu c} + \Sigma_{\mu ib} + \Sigma_{\mu g} \cdot \lambda} \over {10}} \rfloor$$

* $\Sigma_{\mu}$ ... magical strength
* $\lambda$ ... Level
* $\gamma$ ... Character Factor (balanced, viviacious, prudent)
* $\Sigma_{\mu c}$ ... common MStr value, equal for all Guardians of this species
* $\Sigma_{\mu ib}$ ... indiviual base MStr value, unique to every indiviual Guardian, decided at birth
* $\Sigma_{\mu g}$ ... growth MStr value, growing on every level-up

### MDef

$$\Theta_{\mu} = \lambda + 50 + \gamma \cdot \lfloor {{2 \cdot \Theta_{\mu c} + \Theta_{\mu ib} + \Theta_{\mu g} \cdot \lambda} \over {10}} \rfloor$$

* $\Theta_{\mu}$ ... magical defense
* $\lambda$ ... Level
* $\gamma$ ... Character Factor (balanced, viviacious, prudent)
* $\Theta_{\mu c}$ ... common MDef value, equal for all Guardians of this species
* $\Theta_{\mu ib}$ ... indiviual base MDef value, unique to every indiviual Guardian, decided at birth
* $\Theta_{\mu g}$ ... growth MDef value, growing on every level-up

### Speed

$$\Omega = \lambda + 50 + \gamma \cdot \lfloor {{2 \cdot \Omega_c + \Omega_{ib} + \Omega_{g} \cdot \lambda} \over {10}} \rfloor$$

* $\Omega$ ... speed
* $\lambda$ ... Level
* $\gamma$ ... Character Factor (balanced, viviacious, prudent)
* $\Omega_{c}$ ... common Speed value, equal for all Guardians of this species
* $\Omega_{ib}$ ... indiviual base Speed value, unique to every indiviual Guardian, decided at birth
* $\Omega_{g}$ ... growth Speed value, growing on every level-up
