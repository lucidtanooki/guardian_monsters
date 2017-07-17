package de.limbusdev.guardianmonsters.ui;

/**
 * NullCommand
 * Simple place holder class for places where Command s can be used.
 *
 * @author Georg Eckert 2017
 */

public class NullCommand implements Command
{
    @Override
    public void run()
    {
        System.err.println("WARNING: NullCommand is a place holder. Replace it with another implementation of Command.");
    }
}
