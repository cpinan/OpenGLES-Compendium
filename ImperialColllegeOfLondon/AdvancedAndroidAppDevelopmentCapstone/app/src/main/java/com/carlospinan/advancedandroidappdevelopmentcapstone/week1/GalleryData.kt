package com.carlospinan.advancedandroidappdevelopmentcapstone.week1

import android.graphics.Paint
import com.carlospinan.advancedandroidappdevelopmentcapstone.common.Vector3

class GalleryData(
    val portraitInfo: Array<Array<Vector3>>, // Each array is attached to a paint.
    val paint: Array<Paint>
)

/**
 * @author Carlos Piñan
 */
val plotData = intArrayOf(
    1539,
    1531,
    1547,
    1539,
    1543,
    1531,
    1575,
    1591,
    1543,
    1539,
    1523,
    1539,
    1543,
    1539,
    1859,
    2587,
    1455,
    1539,
    1523,
    1527,
    1543,
    1587,
    1619,
    1635,
    1655,
    1659,
    1639,
    1639,
    1579,
    1547,
    1527,
    1527,
    1547,
    1543,
    1551,
    1547,
    1547,
    1563,
    1539,
    1527,
    1523,
    1543,
    1539,
    1575,
    1599,
    1555,
    1531,
    1539,
    1551,
    1547,
    1487,
    1995,
    2331,
    1563,
    1539,
    1523,
    1563,
    1559,
    1591,
    1615,
    1635,
    1659,
    1651,
    1675,
    1631,
    1567,
    1531,
    1519,
    1527,
    1511,
    1531,
    1527,
    1539,
    1539,
    1527,
    1539,
    1543,
    1547,
    1547,
    1571,
    1603,
    1571,
    1539,
    1551,
    1547,
    1559,
    1487,
    1927,
    2475,
    1491,
    1531,
    1503,
    1551,
    1559,
    1571,
    1599,
    1623,
    1663,
    1659,
    1659,
    1615,
    1547,
    1519,
    1519,
    1511,
    1523,
    1539,
    1543,
    1551,
    1567,
    1563,
    1551,
    1555,
    1547,
    1587,
    1579,
    1567,
    1559,
    1539,
    1559,
    1555,
    1563
)

val skipBorderFrontPoints = intArrayOf(1, 2, 4, 5, 7, 8, 10, 11)

val galleryBorderPointsFront = arrayOf(
    Vector3(-4, 3, 2), // TOP LEFT - 0
    Vector3(-1, 3, 2), // TOP LEFT-1 - 1
    Vector3(1, 3, 2), // TOP RIGHT-1 - 2
    Vector3(4, 3, 2), // TOP RIGHT - 3
    Vector3(4, 1, 2), // RIGHT TOP - 4
    Vector3(4, -1, 2), // RIGHT BOTTOM - 5
    Vector3(4, -3, 2), // BOTTOM RIGHT - 6
    Vector3(1, -3, 2), // BOTTOM RIGHT-1 - 7
    Vector3(-1, -3, 2), // BOTTOM LEFT-1 - 8
    Vector3(-4, -3, 2), // BOTTOM LEFT - 9
    Vector3(-4, -1, 2), // LEFT BOTTOM - 10
    Vector3(-4, 1, 2), // LEFT TOP - 11
)

val galleryBorderPointsBack = arrayOf(
    Vector3(-4, 3, -2), // TOP LEFT - 0
    Vector3(-1, 3, -2), // TOP LEFT-1 - 1
    Vector3(1, 3, -2), // TOP RIGHT-1 - 2
    Vector3(4, 3, -2), // TOP RIGHT - 3
    Vector3(4, 1, -2), // RIGHT TOP - 4
    Vector3(4, -1, -2), // RIGHT BOTTOM - 5
    Vector3(4, -3, -2), // BOTTOM RIGHT - 6
    Vector3(1, -3, -2), // BOTTOM RIGHT-1 - 7
    Vector3(-1, -3, -2), // BOTTOM LEFT-1 - 8
    Vector3(-4, -3, -2), // BOTTOM LEFT - 9
    Vector3(-4, -1, -2), // LEFT BOTTOM - 10
    Vector3(-4, 1, -2), // LEFT TOP - 11
)

val galleryInnerPointsFront = arrayOf(
    Vector3(-1, 2, 2), // 0 with front 1
    Vector3(1, 2, 2), // 1 with front 2
    Vector3(-1, 1, 2), // 2 with front 11
    Vector3(1, 1, 2), // 3 with front 4
    Vector3(-1, -1, 2), // 4 with front 10
    Vector3(1, -1, 2), // 5 with front 5
    Vector3(-1, -2, 2), // 6 with front 8
    Vector3(1, -2, 2), // 7 with front 7
    Vector3(-1F, 0.5F, 2F), // 8 with this 9
    Vector3(-1F, -0.5F, 2F), // 9 with this 8
    Vector3(1F, 0.5F, 2F), // 10 with this 11
    Vector3(1F, -0.5F, 2F) // 11 with this 10
)

val galleryInnerPointsBack = arrayOf(
    Vector3(-1, 2, -2), // 0 with back 1
    Vector3(1, 2, -2), // 1 with back 2
    Vector3(-1, 1, -2), // 2 with back 11
    Vector3(1, 1, -2), // 3 with back 4
    Vector3(-1, -1, -2), // 4 with back 10
    Vector3(1, -1, -2), // 5 with back 5
    Vector3(-1, -2, -2), // 6 with back 8
    Vector3(1, -2, -2), // 7 with back 7
    Vector3(-1F, 0.5F, -2F), // 8 with this 9
    Vector3(-1F, -0.5F, -2F), // 9 with this 8
    Vector3(1F, 0.5F, -2F), // 10 with this 11
    Vector3(1F, -0.5F, -2F) // 11 with this 10
)

val frame01 = GalleryData(
    portraitInfo = arrayOf(
        arrayOf(
            Vector3(-3F, 3F, 1.5F),
            Vector3(-2F, 3F, 1.5F),
            Vector3(-2F, 3F, 0.8F),
            Vector3(-3F, 3F, 0.8F)
        ),
        arrayOf(
            Vector3(-3F, 3F, 0.8F),
            Vector3(-2F, 3F, 0.8F),
            Vector3(-2F, 3F, -0.8F),
            Vector3(-3F, 3F, -0.8F)
        ),
        arrayOf(
            Vector3(-3F, 3F, -0.8F),
            Vector3(-2F, 3F, -0.8F),
            Vector3(-2F, 3F, -1.5F),
            Vector3(-3F, 3F, -1.5F)
        )
    ),
    paint = arrayOf(
        Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = 0xFF0000FF.toInt()
                style = Paint.Style.FILL
            },
        Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = 0xFFFF0000.toInt()
                style = Paint.Style.FILL
            },
        Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = 0xFF0000FF.toInt()
                style = Paint.Style.FILL
            }
    )
)

val frame02 = GalleryData(
    portraitInfo = arrayOf(
        arrayOf(
            Vector3(-3F, -3F, 1.5F),
            Vector3(-2F, -3F, 1.5F),
            Vector3(-2F, -3F, 0.8F),
            Vector3(-3F, -3F, 0.8F)
        ),
        arrayOf(
            Vector3(-3F, -3F, 0.8F),
            Vector3(-2F, -3F, 0.8F),
            Vector3(-2F, -3F, -0.8F),
            Vector3(-3F, -3F, -0.8F)
        ),
        arrayOf(
            Vector3(-3F, -3F, -0.8F),
            Vector3(-2F, -3F, -0.8F),
            Vector3(-2F, -3F, -1.5F),
            Vector3(-3F, -3F, -1.5F)
        )
    ),
    paint = arrayOf(
        Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = 0xFFFFFF00.toInt()
                style = Paint.Style.FILL
            },
        Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = 0xFF00FF00.toInt()
                style = Paint.Style.FILL
            },
        Paint(Paint.ANTI_ALIAS_FLAG)
            .apply {
                color = 0xFF000000.toInt()
                style = Paint.Style.FILL
            }
    )
)