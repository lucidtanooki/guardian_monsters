# TiledMapStructure

FWM-Engine uses *.tmx files, which can be edited with the free software
Tiled (www.tiled.org).

################################################################################
## MAP Properties

 * musicType: (town|battle)
 * musicIndex: number of the music to use
 * weather: (clouds)*


################################################################################
## Layers

Every Map consists of several layers. Some are optional, some must be available.
All layers should be sorted in the correct rendering order.


### Graphical Layers

Names and order of graphical layers is more or less free. The order is important
for the rendering process. The optimal naming scheme is:

 * ...
 * walls(n+1)
 * ground(n+1)
 * ...
 * tops1
 * lights1
 * shadows1
 * objects1
 * walls1
 * ground1

Layer names follow the scheme [layer name][layer number]


### Object Layers

Object layers comntain rectangles with information about the game objects in
that layer.


#### Neccessary Layers

 * people .. the layer, where people are drawn
 * sensors .. contains trigger objects
 * colliderWalls[number] .. colliders for game physics
 * objects .. non-living objects like signs and so on

#### Optional Layers

 * animatedObjects[number] .. objects that get animated


################################################################################
## Objects


### objects
#### sign
Sign objects are for things which need a description

 * title: title of the description
 * text: text of the description

### animatedObjects
Contains place holders for animations
 * index: name of the animation to use
 * frameDuration: duration of an animation frame


### sensors
Sensors are objects which trigger an action

#### warpField
 * targetID: number of the target map
 * targetWarpPointID: number of the startField object, where the player should start

#### startField
 * fieldID: ID of this object, must be unique in the whole map



### livingEntities
#### person

 * male: which sprite type (true|false)
 * static: whether a person walks around (true|false)
 * spriteIndex: which sprite (number)
 * name: name of the person
 * text: spoken text of a person
 * path: defines a walking path or looking direction (NSTOP|ESTOP|SSTOP|WSTOP|N|E|S|W)*



