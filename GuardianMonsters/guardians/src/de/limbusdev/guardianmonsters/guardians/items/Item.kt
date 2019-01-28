package de.limbusdev.guardianmonsters.guardians.items

/**
 * @author Georg Eckert on 17.02.17.
 */

abstract class Item(val name: String, val category: Category)
{
    val ID: Int

    init
    {
        this.ID = INSTANCE_COUNTER++
    }


    override fun equals(other: Any?): Boolean
    {
        return (other.hashCode() == hashCode())
    }

    override fun hashCode(): Int
    {
        var result = name.hashCode()
        result = 31 * result + category.hashCode()
        return result
    }

    companion object
    {
        private var INSTANCE_COUNTER = 0
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Inner Classes
    enum class Category
    {
        ALL,
        MEDICINE,
        EQUIPMENT,
        KEY,
        CHAKRACRYSTAL
    }
}
