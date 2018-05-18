package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components;

import com.badlogic.ashley.core.Component;

import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere;

public class GuardoSphereComponent implements Component
{
    public GuardoSphere guardoSphere;

    public GuardoSphereComponent()
    {
        guardoSphere = new GuardoSphere();
    }
}
