package de.limbusdev.guardianmonsters.guardians.abilities

import de.limbusdev.utils.geometry.IntVec2

/**
 * GraphTemplate contains arrays of fixed ability board graph layouts.
 *
 * @author Georg Eckert 2019
 */

object GraphTemplate
{
    /**
     * Coordinates of all nodes on the ability graph.
     */
    val coordinates = arrayOf(
            intArrayOf(0, 0, 0),    intArrayOf(1, 0, 1),    intArrayOf(2, 1, 0),    intArrayOf(3, 0, -1),   intArrayOf(4, -1, 0),   intArrayOf(5, 0, 2),    intArrayOf(6, 2, 1),    intArrayOf(7, 0, -2),   intArrayOf(8, -2, -1),  intArrayOf(9, -1, 2),
            intArrayOf(10, 2, 2),   intArrayOf(11, 1, -2),  intArrayOf(12, -2, -2), intArrayOf(13, 1, 2),   intArrayOf(14, 3, 1),   intArrayOf(15, -1, -2), intArrayOf(16, -3, -1), intArrayOf(17, 0, 3),   intArrayOf(18, 4, 4),   intArrayOf(19, 0, -3),
            intArrayOf(20, -4, -4), intArrayOf(21, -1, 4),  intArrayOf(22, 4, 2),   intArrayOf(23, 1, -4),  intArrayOf(24, -4, -2), intArrayOf(25, 1, 4),   intArrayOf(26, 3, 0),   intArrayOf(27, -1, -4), intArrayOf(28, -3, 0),  intArrayOf(29, 2, 3),
            intArrayOf(30, 5, 4),   intArrayOf(31, -2, -3), intArrayOf(32, -5, -4), intArrayOf(33, 5, 3),   intArrayOf(34, -5, -3), intArrayOf(35, 2, 0),   intArrayOf(36, -2, 0),  intArrayOf(37, 6, 3),   intArrayOf(38, -6, -3), intArrayOf(39, 5, 2),
            intArrayOf(40, -5, -2), intArrayOf(41, 1, -1),  intArrayOf(42, -1, 1),  intArrayOf(43, 6, 2),   intArrayOf(44, -6, -2), intArrayOf(45, 4, 3),   intArrayOf(46, -4, -3), intArrayOf(47, 3, -1),  intArrayOf(48, -3, 1),  intArrayOf(49, 5, 1),
            intArrayOf(50, -5, -1), intArrayOf(51, 2, -2),  intArrayOf(52, -2, 2),  intArrayOf(53, 5, 0),   intArrayOf(54, -5, 0),  intArrayOf(55, 4, -4),  intArrayOf(56, -4, 4),  intArrayOf(57, 4, 1),   intArrayOf(58, -4, -1), intArrayOf(59, 5, -3),
            intArrayOf(60, -5, 3),  intArrayOf(61, 6, 1),   intArrayOf(62, -6, -1), intArrayOf(63, 2, -4),  intArrayOf(64, -2, 4),  intArrayOf(65, 7, 0),   intArrayOf(66, -7, 0),  intArrayOf(67, 5, -1),  intArrayOf(68, -5, 1),  intArrayOf(69, 4, -1),
            intArrayOf(70, -4, 1),  intArrayOf(71, 2, -3),  intArrayOf(72, -2, 3),  intArrayOf(73, 7, 1),   intArrayOf(74, -7, -1), intArrayOf(75, 6, -2),  intArrayOf(76, -6, 2),  intArrayOf(77, 4, -2),  intArrayOf(78, -4, 2),  intArrayOf(79, 6, -3),
            intArrayOf(80, -6, 3),  intArrayOf(81, 7, -1),  intArrayOf(82, -7, 1),  intArrayOf(83, 7, -3),  intArrayOf(84, -7, 3),  intArrayOf(85, 3, -2),  intArrayOf(86, -3, 2),  intArrayOf(87, 6, -4),  intArrayOf(88, -6, 4),  intArrayOf(89, 8, 0),
            intArrayOf(90, -8, 0),  intArrayOf(91, 7, -4),  intArrayOf(92, -7, 4),  intArrayOf(93, 4, -3),  intArrayOf(94, -4, 3),  intArrayOf(95, 8, 2),   intArrayOf(96, -8, -2), intArrayOf(97, 8, -2),  intArrayOf(98, -8, 2),  intArrayOf(99, 8, 4),
            intArrayOf(100, -8, -4))

    /**
     * Connections of the nodes in the ability graph.
     */
    val connections = arrayOf(
            intArrayOf(0, 1),   intArrayOf(1, 5),   intArrayOf(5, 9),   intArrayOf(5, 17),  intArrayOf(5, 13),  intArrayOf(5, 17),  intArrayOf(17, 21), intArrayOf(17, 25), intArrayOf(25, 29),
            intArrayOf(0, 3),   intArrayOf(3, 7),   intArrayOf(7, 11),  intArrayOf(7, 15),  intArrayOf(7, 19),  intArrayOf(19, 23), intArrayOf(19, 27), intArrayOf(27, 31),
            intArrayOf(0, 2),   intArrayOf(2, 6),   intArrayOf(6, 10),  intArrayOf(10, 18), intArrayOf(18, 30), intArrayOf(30, 37), intArrayOf(37, 43), intArrayOf(43, 49), intArrayOf(49, 57), intArrayOf(49, 61), intArrayOf(49, 53),
            intArrayOf(53, 69), intArrayOf(69, 77), intArrayOf(77, 85), intArrayOf(77, 93),
            intArrayOf(53, 65), intArrayOf(65, 89), intArrayOf(65, 81), intArrayOf(81, 97), intArrayOf(65, 73), intArrayOf(73, 95), intArrayOf(95, 99),
            intArrayOf(6, 14),  intArrayOf(14, 22), intArrayOf(22, 39), intArrayOf(22, 33), intArrayOf(33, 45),
            intArrayOf(14, 26), intArrayOf(26, 35), intArrayOf(35, 41), intArrayOf(41, 47), intArrayOf(47, 51), intArrayOf(51, 55),
            intArrayOf(55, 63), intArrayOf(63, 71),
            intArrayOf(55, 59), intArrayOf(59, 67), intArrayOf(67, 75), intArrayOf(75, 79), intArrayOf(79, 83), intArrayOf(83, 87), intArrayOf(87, 91),
            intArrayOf(0, 4),   intArrayOf(4, 8),   intArrayOf(8, 16),  intArrayOf(16, 24), intArrayOf(24, 40), intArrayOf(24, 34), intArrayOf(34, 46),
            intArrayOf(16, 28), intArrayOf(28, 36), intArrayOf(36, 42), intArrayOf(42, 48), intArrayOf(48, 52), intArrayOf(52, 56), intArrayOf(56, 64), intArrayOf(64, 72),
            intArrayOf(56, 60), intArrayOf(60, 68), intArrayOf(68, 76), intArrayOf(76, 80), intArrayOf(80, 84), intArrayOf(84, 88), intArrayOf(88, 92),
            intArrayOf(8, 12),  intArrayOf(12, 20), intArrayOf(20, 32), intArrayOf(32, 38), intArrayOf(38, 44), intArrayOf(44, 50), intArrayOf(50, 62), intArrayOf(50, 58), intArrayOf(50, 54),
            intArrayOf(54, 70), intArrayOf(70, 78), intArrayOf(78, 86), intArrayOf(78, 94),
            intArrayOf(54, 66), intArrayOf(66, 82), intArrayOf(82, 98), intArrayOf(66, 90), intArrayOf(66, 74), intArrayOf(74, 96), intArrayOf(96, 100))

    fun getNodePosition(ID: Int) = IntVec2(coordinates[ID][1],    coordinates[ID][2])

    fun getEdge(index: Int)      = IntVec2(connections[index][0], connections[index][1])
}
