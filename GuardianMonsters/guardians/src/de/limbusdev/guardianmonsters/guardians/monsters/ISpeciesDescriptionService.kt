package de.limbusdev.guardianmonsters.guardians.monsters

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator

/**
 * IGuardianDescriptionService
 *
 * @author Georg Eckert 2017
 */

interface ISpeciesDescriptionService : GuardiansServiceLocator.Service
{
    fun getSpeciesDescription(speciesID: Int): SpeciesDescription
    fun getCommonNameById(speciesID: Int, form: Int): String
}
