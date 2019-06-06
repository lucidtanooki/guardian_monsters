package de.limbusdev.guardianmonsters.battle.ui.widgets

/**
 * When implementing ObservableWidget, prevent observers registering multiple times
 *
 * @author Georg Eckert 2019
 */
interface ObservableWidget
{
    fun addWidgetObserver(wo: WidgetObserver)
    fun notifyWidgetObservers()
}
