import kotlin.test.*

internal class Test1 {

    @Test
    fun lcsTests() {
        assertContentEquals(arrayOf(Pair(2, 2), Pair(3, 5), Pair(4, 6), Pair(5, 8)),
            longestCommonSubsequence(arrayOf(1, 2, 3, 4, 5), arrayOf(7, 2, 4, 5, 3, 4, 9, 5)))

        assertContentEquals(arrayOf(Pair(1, 2), Pair(4, 3), Pair(5, 4)),
            longestCommonSubsequence(arrayOf("aaa", "b", "ab", "ababa", "b"), arrayOf("b", "aaa", "ababa", "b")))

        assertContentEquals(arrayOf(Pair(1, 2), Pair(3, 3), Pair(5, 5), Pair(8, 6)),
            longestCommonSubsequence(arrayOf(1, 2, 3, 1, 2, 1, 1, 3), arrayOf(2, 1, 3, 4, 2, 3)))

        assertContentEquals(arrayOf(Pair(1, 2), Pair(2, 3), Pair(3, 4)),
            longestCommonSubsequence(arrayOf(5, 5, 5, 4), arrayOf(4, 5, 5, 5)))

        assertContentEquals(arrayOf(Pair(1, 1), Pair(2, 2), Pair(3, 3), Pair(4, 4), Pair(5, 5), Pair(6, 6), Pair(7, 7)),
            longestCommonSubsequence(arrayOf(1, 1, 1, 1, 1, 1, 1), arrayOf(1, 1, 1, 1, 1, 1, 1)))

    }

    @Test
    fun diffTests() {
        assertEquals(listOf(
            DiffBlock(0, 1, 0, 1),
            DiffBlock(2, 0, 2, 2),
            DiffBlock(4, 0, 6, 1)
        ),
            findDiff(arrayOf(Pair(2, 2), Pair(3, 5), Pair(4, 6), Pair(5, 8)), 5, 8)
        )

        assertEquals(listOf(
            DiffBlock(0, 0, 0, 1),
            DiffBlock(1, 2, 2, 0),
        ),
            findDiff(arrayOf(Pair(1, 2), Pair(4, 3), Pair(5, 4)), 5, 4)
        )

        assertEquals(listOf(
            DiffBlock(0, 0, 0, 1),
            DiffBlock(1, 1, 2, 0),
            DiffBlock(3, 1, 3, 1),
            DiffBlock(5, 2, 5, 0),
        ),
            findDiff(arrayOf(Pair(1, 2), Pair(3, 3), Pair(5, 5), Pair(8, 6)), 8, 6)
        )

        assertEquals(listOf(
            DiffBlock(0, 0, 0, 1),
            DiffBlock(3, 1, 4, 0),
        ),
            findDiff(arrayOf(Pair(1, 2), Pair(2, 3), Pair(3, 4)), 4, 4)
        )

        assertEquals(listOf(),
            findDiff(arrayOf(Pair(1, 1), Pair(2, 2), Pair(3, 3), Pair(4, 4), Pair(5, 5),
                Pair(6, 6), Pair(7, 7)), 7, 7)
        )
    }
}
