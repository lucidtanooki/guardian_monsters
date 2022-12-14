package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Iterator;

public class KryoSerializer
{
    public static void addLibGdxSerializers(Kryo kryo)
    {
        kryo.register(Array.class, new Serializer<Array>()
        {
            { setAcceptsNull(true); }

            private Class genericType;

            public void setGenerics (Kryo kryo, Class[] generics)
            {
                if (generics != null && kryo.isFinal(generics[0])) genericType = generics[0];
                else genericType = null;
            }

            public void write (Kryo kryo, Output output, Array array)
            {
                int length = array.size;
                output.writeInt(length, true);
                if (length == 0)
                {
                    genericType = null;
                    return;
                }
                if (genericType != null)
                {
                    Serializer serializer = kryo.getSerializer(genericType);
                    genericType = null;
                    for (Object element : array) { kryo.writeObjectOrNull(output, element, serializer); }
                }
                else
                {
                    for (Object element : array) { kryo.writeClassAndObject(output, element); }
                }
            }

            public Array read (Kryo kryo, Input input, Class<Array> type)
            {
                Array array = new Array();
                kryo.reference(array);
                int length = input.readInt(true);
                array.ensureCapacity(length);

                if (genericType != null)
                {
                    Class elementClass = genericType;
                    Serializer serializer = kryo.getSerializer(genericType);
                    genericType = null;
                    for (int i = 0; i < length; i++) { array.add(kryo.readObjectOrNull(input, elementClass, serializer)); }
                }
                else
                {
                    for (int i = 0; i < length; i++) { array.add(kryo.readClassAndObject(input)); }
                }
                return array;
            }
        });

        kryo.register(IntArray.class, new Serializer<IntArray>()
        {
            { setAcceptsNull(true); }

            public void write (Kryo kryo, Output output, IntArray array)
            {
                int length = array.size;
                output.writeInt(length, true);
                if (length == 0) return;
                for (int i = 0, n = array.size; i < n; i++)
                    output.writeInt(array.get(i), true);
            }

            public IntArray read (Kryo kryo, Input input, Class<IntArray> type)
            {
                IntArray array = new IntArray();
                kryo.reference(array);
                int length = input.readInt(true);
                array.ensureCapacity(length);
                for (int i = 0; i < length; i++)
                    array.add(input.readInt(true));
                return array;
            }
        });

        kryo.register(FloatArray.class, new Serializer<FloatArray>()
        {
            { setAcceptsNull(true); }

            public void write (Kryo kryo, Output output, FloatArray array)
            {
                int length = array.size;
                output.writeInt(length, true);
                if (length == 0) return;
                for (int i = 0, n = array.size; i < n; i++)
                    output.writeFloat(array.get(i));
            }

            public FloatArray read (Kryo kryo, Input input, Class<FloatArray> type)
            {
                FloatArray array = new FloatArray();
                kryo.reference(array);
                int length = input.readInt(true);
                array.ensureCapacity(length);
                for (int i = 0; i < length; i++)
                    array.add(input.readFloat());
                return array;
            }
        });

        kryo.register(Color.class, new Serializer<Color>()
        {
            public Color read (Kryo kryo, Input input, Class<Color> type)
            {
                Color color = new Color();
                Color.rgba8888ToColor(color, input.readInt());
                return color;
            }

            public void write (Kryo kryo, Output output, Color color)
            {
                output.writeInt(Color.rgba8888(color));
            }
        });

        kryo.register(ArrayMap.class, new Serializer<ArrayMap> ()
        {
            Class keyClass, valueClass;
            Serializer keySerializer, valueSerializer;
            boolean keysCanBeNull = true, valuesCanBeNull = true;
            Class keyGenericType, valueGenericType;

            /** @param keysCanBeNull False if all keys are not null. This saves 1 byte per key if keyClass is set. True if it is not known
             * (default). */
            public void setKeysCanBeNull (boolean keysCanBeNull)
            {
                this.keysCanBeNull = keysCanBeNull;
            }

            /** @param keyClass The concrete class of each key. This saves 1 byte per key. Set to null if the class is not known or varies
             * per key (default).
             * @param keySerializer The serializer to use for each key. */
            public void setKeyClass (Class keyClass, Serializer keySerializer)
            {
                this.keyClass = keyClass;
                this.keySerializer = keySerializer;
            }

            /** @param valueClass The concrete class of each value. This saves 1 byte per value. Set to null if the class is not known or
             * varies per value (default).
             * @param valueSerializer The serializer to use for each value. */
            public void setValueClass (Class valueClass, Serializer valueSerializer)
            {
                this.valueClass = valueClass;
                this.valueSerializer = valueSerializer;
            }

            /** @param valuesCanBeNull True if values are not null. This saves 1 byte per value if keyClass is set. False if it is not known
             * (default). */
            public void setValuesCanBeNull (boolean valuesCanBeNull)
            {
                this.valuesCanBeNull = valuesCanBeNull;
            }

            public void setGenerics (Kryo kryo, Class[] generics)
            {
                keyGenericType = null;
                valueGenericType = null;

                if (generics != null && generics.length > 0)
                {
                    if (generics[0] != null && kryo.isFinal(generics[0])) keyGenericType = generics[0];
                    if (generics.length > 1 && generics[1] != null && kryo.isFinal(generics[1])) valueGenericType = generics[1];
                }
            }

            public void write (Kryo kryo, Output output, ArrayMap map)
            {
                int length = map.size;
                output.writeInt(length, true);

                Serializer keySerializer = this.keySerializer;
                if (keyGenericType != null)
                {
                    if (keySerializer == null) keySerializer = kryo.getSerializer(keyGenericType);
                    keyGenericType = null;
                }
                Serializer valueSerializer = this.valueSerializer;
                if (valueGenericType != null)
                {
                    if (valueSerializer == null) valueSerializer = kryo.getSerializer(valueGenericType);
                    valueGenericType = null;
                }

                for (Iterator iter = map.entries().iterator(); iter.hasNext();)
                {
                    ObjectMap.Entry entry = (ObjectMap.Entry)iter.next();
                    if (keySerializer != null) {
                        if (keysCanBeNull)
                            kryo.writeObjectOrNull(output, entry.key, keySerializer);
                        else
                            kryo.writeObject(output, entry.key, keySerializer);
                    } else
                        kryo.writeClassAndObject(output, entry.key);
                    if (valueSerializer != null) {
                        if (valuesCanBeNull)
                            kryo.writeObjectOrNull(output, entry.value, valueSerializer);
                        else
                            kryo.writeObject(output, entry.value, valueSerializer);
                    } else
                        kryo.writeClassAndObject(output, entry.value);
                }
            }

            /** Used by {@link #read(Kryo, Input, Class)} to create the new object. This can be overridden to customize object creation, eg
             * to call a constructor with arguments. The default implementation uses {@link Kryo#newInstance(Class)}. */
            protected ArrayMap create (Kryo kryo, Input input, Class<ArrayMap> type)
            {
                return kryo.newInstance(type);
            }

            public ArrayMap read (Kryo kryo, Input input, Class<ArrayMap> type)
            {
                ArrayMap map = create(kryo, input, type);
                int length = input.readInt(true);

                Class keyClass = this.keyClass;
                Class valueClass = this.valueClass;

                Serializer keySerializer = this.keySerializer;
                if (keyGenericType != null) {
                    keyClass = keyGenericType;
                    if (keySerializer == null) keySerializer = kryo.getSerializer(keyClass);
                    keyGenericType = null;
                }
                Serializer valueSerializer = this.valueSerializer;
                if (valueGenericType != null) {
                    valueClass = valueGenericType;
                    if (valueSerializer == null) valueSerializer = kryo.getSerializer(valueClass);
                    valueGenericType = null;
                }

                kryo.reference(map);

                for (int i = 0; i < length; i++) {
                    Object key;
                    if (keySerializer != null) {
                        if (keysCanBeNull)
                            key = kryo.readObjectOrNull(input, keyClass, keySerializer);
                        else
                            key = kryo.readObject(input, keyClass, keySerializer);
                    } else
                        key = kryo.readClassAndObject(input);
                    Object value;
                    if (valueSerializer != null) {
                        if (valuesCanBeNull)
                            value = kryo.readObjectOrNull(input, valueClass, valueSerializer);
                        else
                            value = kryo.readObject(input, valueClass, valueSerializer);
                    } else
                        value = kryo.readClassAndObject(input);
                    map.put(key, value);
                }
                return map;
            }

            protected ArrayMap createCopy (Kryo kryo, ArrayMap original)
            {
                return kryo.newInstance(original.getClass());
            }

            public ArrayMap copy (Kryo kryo, ArrayMap original)
            {
                ArrayMap copy = createCopy(kryo, original);
                for (Iterator iter = original.entries().iterator(); iter.hasNext();)
                {
                    ObjectMap.Entry entry = (ObjectMap.Entry)iter.next();
                    copy.put(kryo.copy(entry.key), kryo.copy(entry.value));
                }
                return copy;
            }
        });
    }
}
