package de.limbusdev.guardianmonsters.battle.model

interface ListObserver<T>
{
    fun getInformedAboutRemoval(item: T)
}
