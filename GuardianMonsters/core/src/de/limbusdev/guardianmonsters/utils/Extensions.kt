package de.limbusdev.guardianmonsters.utils

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Family
import kotlin.reflect.KClass

@SafeVarargs
fun FamilyAll(vararg kComponentTypes: KClass<out Component>): Family.Builder
{
    val componentTypes = Array(kComponentTypes.size) { kComponentTypes[it].java }
    return Family.all(*componentTypes)
}

@SafeVarargs
fun FamilyOne(vararg kComponentTypes: KClass<out Component>): Family.Builder
{
    val componentTypes = Array(kComponentTypes.size) { kComponentTypes[it].java }
    return Family.one(*componentTypes)
}

@SafeVarargs
fun FamilyExclude(vararg kComponentTypes: KClass<out Component>): Family.Builder
{
    val componentTypes = Array(kComponentTypes.size) { kComponentTypes[it].java }
    return Family.exclude(*componentTypes)
}