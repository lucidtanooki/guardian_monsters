# Calculations

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
