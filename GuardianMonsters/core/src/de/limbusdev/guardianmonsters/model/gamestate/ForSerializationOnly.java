package de.limbusdev.guardianmonsters.model.gamestate;

import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;

/**
 * ForSerializationOnly
 * marks constructors that should be used for Serialization only
 *
 * @author Georg Eckert 2017
 */

@Target(value={CONSTRUCTOR})
public @interface ForSerializationOnly {
}
