import kotlin.test.*

internal class Test1 {

    @Test
    fun lcsTests() {
        assertContentEquals(arrayOf(Pair(1, 1), Pair(2, 4), Pair(3, 5), Pair(4, 7)),
            longestCommonSubsequence(arrayOf(1, 2, 3, 4, 5), arrayOf(7, 2, 4, 5, 3, 4, 9, 5)))

        assertContentEquals(arrayOf(Pair(0, 1), Pair(3, 2), Pair(4, 3)),
            longestCommonSubsequence(arrayOf("aaa", "b", "ab", "ababa", "b"), arrayOf("b", "aaa", "ababa", "b")))

        assertContentEquals(arrayOf(Pair(0, 1), Pair(2, 2), Pair(4, 4), Pair(7, 5)),
            longestCommonSubsequence(arrayOf(1, 2, 3, 1, 2, 1, 1, 3), arrayOf(2, 1, 3, 4, 2, 3)))

        assertContentEquals(arrayOf(Pair(0, 1), Pair(1, 2), Pair(2, 3)),
            longestCommonSubsequence(arrayOf(5, 5, 5, 4), arrayOf(4, 5, 5, 5)))

        assertContentEquals(arrayOf(Pair(0, 0), Pair(1, 1), Pair(2, 2), Pair(3, 3), Pair(4, 4), Pair(5, 5), Pair(6, 6)),
            longestCommonSubsequence(arrayOf(1, 1, 1, 1, 1, 1, 1), arrayOf(1, 1, 1, 1, 1, 1, 1)))

    }
}
