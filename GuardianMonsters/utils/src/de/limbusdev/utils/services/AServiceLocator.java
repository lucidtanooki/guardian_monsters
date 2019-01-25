package de.limbusdev.utils.services;


import java.util.HashMap;
import java.util.Map;

/**
 * IServiceLocator
 *
 * @author Georg Eckert 2017
 */

public abstract class AServiceLocator
{
    private final Map<Class<? extends IService>, IService> services;


    public AServiceLocator()
    {
        services = new HashMap<>();
        provideServices();
    }

    public abstract void provideServices();

    @SuppressWarnings("unchecked")
    public <T extends IService> T get(Class<T> serviceInterface)
    {
        if(!services.containsKey(serviceInterface))
        {
            throw new IllegalArgumentException("No such Service in this Module. Provide it first!");
        }

        return (T) services.get(serviceInterface);
    }

    public <T extends IService> void provide(T service , Class<? extends IService> serviceInterface)
    {
        services.put(serviceInterface, service);
    }
}
