/*
 * Copyright (C) 2025 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3.model

/**
 * All valid grids as data objects, so that we can check whether a migration between two grids is
 * allowed. Both the source and the destination grid have to be instantiated here.
 *
 * In addition to the grids the device profiles use, this lists every size offered by
 * Settings > Home screen > Grid (3..8 rows x 5..10 columns), because those are switched by the
 * user at runtime and each switch has to migrate the workspace instead of letting the loader drop
 * items that fall outside the new bounds.
 *
 * @param columns is the number of columns of the grid.
 * @param rows is the number of rows of the grid.
 */
data class GridMigrationOption(val columns: Int, val rows: Int) {

    /**
     * Method that checks if we are able to migrate from this grid to another.
     *
     * @param destGridMigrationOption is the grid migration option we're going to.
     * @param isAfterRestore is a boolean that is true when in a backup and restore scenario.
     * @return a boolean that lets us know whether we can migrate from source to destination.
     */
    fun canMigrate(destGridMigrationOption: GridMigrationOption, isAfterRestore: Boolean): Boolean {
        // In a restore scenario any known grid is an acceptable destination. Otherwise the
        // destination has to be one of the grids we know how to place items into.
        return isAfterRestore || destGridMigrationOption in ALL_GRIDS
    }

    companion object {
        /** Grids used by the device profiles, plus every user selectable size. */
        private val PHONE_SIZES: List<Pair<Int, Int>> =
            listOf(
                2 to 2,
                3 to 3,
                4 to 4,
                4 to 5,
                4 to 6,
                5 to 5,
                5 to 6,
                7 to 3,
                8 to 3,
            ) + (3..8).flatMap { rows -> (5..10).map { columns -> columns to rows } }

        private val TABLET_SIZES: List<Pair<Int, Int>> = listOf(6 to 5)

        private val ALL_GRIDS: List<GridMigrationOption> =
            (PHONE_SIZES + TABLET_SIZES)
                .distinct()
                .map { (columns, rows) -> GridMigrationOption(columns, rows) }

        /**
         * Factory method that creates an instance of GridMigrationOption if valid.
         *
         * @param columns is the number of columns of the grid.
         * @param rows is the number of rows of the grid.
         */
        @JvmStatic
        fun from(columns: Int, rows: Int): GridMigrationOption? =
            ALL_GRIDS.firstOrNull { it.columns == columns && it.rows == rows }
    }
}
