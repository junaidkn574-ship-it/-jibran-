package com.example.model

data class LevelData(
    val id: Int,
    val size: Int,
    val packName: String,
    val difficulty: String,
    val parMoves: Int,
    val endpoints: Map<Int, Pair<GridPos, GridPos>>,
    val solutionPaths: Map<Int, List<GridPos>>
) {
    val totalColors: Int get() = endpoints.size
    val totalCells: Int get() = size * size

    fun isEndpoint(pos: GridPos): Pair<Int, Boolean>? {
        for ((colorId, pair) in endpoints) {
            if (pair.first == pos) return Pair(colorId, true)
            if (pair.second == pos) return Pair(colorId, false)
        }
        return null
    }

    fun getColorForEndpoint(pos: GridPos): Int? {
        return isEndpoint(pos)?.first
    }

    companion object {
        fun create(
            id: Int,
            size: Int,
            packName: String,
            difficulty: String,
            parMoves: Int,
            solutionDefs: Map<Int, String>
        ): LevelData {
            val solutions = solutionDefs.mapValues { (_, pathStr) ->
                pathStr.trim().split(" ").map { item ->
                    val comma = item.indexOf(',')
                    GridPos(
                        item.substring(0, comma).toInt(),
                        item.substring(comma + 1).toInt()
                    )
                }
            }
            val eps = solutions.mapValues { (_, path) ->
                Pair(path.first(), path.last())
            }
            return LevelData(
                id = id,
                size = size,
                packName = packName,
                difficulty = difficulty,
                parMoves = parMoves,
                endpoints = eps,
                solutionPaths = solutions
            )
        }
    }
}
