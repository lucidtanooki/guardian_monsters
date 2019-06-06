package de.limbusdev.guardianmonsters.guardians.monsters

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.ArrayMap
import de.limbusdev.utils.extensions.set


/**
 * GuardianDescriptionService
 *
 * @author Georg Eckert 2019
 */

class SpeciesDescriptionService private constructor (jsonSpeciesDescriptions: String)
    : ISpeciesDescriptionService
{
    private val speciesDB: ArrayMap<Int, SpeciesDescription> = ArrayMap()

    init
    {
        val rootElement = JSONGuardianParser.parseGuardianList(jsonSpeciesDescriptions)

        for (i in 0 until rootElement.size)
        {
            val info = JSONGuardianParser.parseGuardian(rootElement.get(i))
            speciesDB[info.ID] = info
        }
    }

    override fun getSpeciesDescription(speciesID: Int): SpeciesDescription
    {
        return speciesDB.get(speciesID)
    }

    override fun getCommonNameById(speciesID: Int, form: Int): String
    {
        return speciesDB.get(speciesID).getNameID(form)
    }

    override fun destroy() { instance = null }

    companion object
    {
        private var instance: SpeciesDescriptionService? = null

        fun getInstance(jsonSpecies: String): SpeciesDescriptionService
        {
            if (instance == null) { instance = SpeciesDescriptionService(jsonSpecies) }
            return instance!!
        }

        fun getInstanceFromFile(jsonSpeciesDescriptionFilePath: String): SpeciesDescriptionService
        {
            val json = Gdx.files.internal(jsonSpeciesDescriptionFilePath).readString()
            return getInstance(json)
        }
    }
}
