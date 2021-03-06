import kotlin.test.*

internal class Test1 {

    @Test
    fun lcsTests() {
        assertEquals(listOf(Pair(2, 2), Pair(3, 5), Pair(4, 6), Pair(5, 8)),
            longestCommonSubsequence(listOf(1, 2, 3, 4, 5), listOf(7, 2, 4, 5, 3, 4, 9, 5)))

        assertEquals(listOf(Pair(1, 2), Pair(4, 3), Pair(5, 4)),
            longestCommonSubsequence(listOf("aaa", "b", "ab", "ababa", "b"), listOf("b", "aaa", "ababa", "b")))

        assertEquals(listOf(Pair(1, 2), Pair(3, 3), Pair(5, 5), Pair(8, 6)),
            longestCommonSubsequence(listOf(1, 2, 3, 1, 2, 1, 1, 3), listOf(2, 1, 3, 4, 2, 3)))

        assertEquals(listOf(Pair(1, 2), Pair(2, 3), Pair(3, 4)),
            longestCommonSubsequence(listOf(5, 5, 5, 4), listOf(4, 5, 5, 5)))

        assertEquals(listOf(Pair(1, 1), Pair(2, 2), Pair(3, 3), Pair(4, 4), Pair(5, 5), Pair(6, 6), Pair(7, 7)),
            longestCommonSubsequence(listOf(1, 1, 1, 1, 1, 1, 1), listOf(1, 1, 1, 1, 1, 1, 1)))

    }

    @Test
    fun diffTests() {
        assertEquals(listOf(
            DiffBlock(0, 1, 0, 1),
            DiffBlock(2, 0, 2, 2),
            DiffBlock(4, 0, 6, 1)
        ),
            findDiff(listOf(Pair(2, 2), Pair(3, 5), Pair(4, 6), Pair(5, 8)), 5, 8)
        )

        assertEquals(listOf(
            DiffBlock(0, 0, 0, 1),
            DiffBlock(1, 2, 2, 0),
        ),
            findDiff(listOf(Pair(1, 2), Pair(4, 3), Pair(5, 4)), 5, 4)
        )

        assertEquals(listOf(
            DiffBlock(0, 0, 0, 1),
            DiffBlock(1, 1, 2, 0),
            DiffBlock(3, 1, 3, 1),
            DiffBlock(5, 2, 5, 0),
        ),
            findDiff(listOf(Pair(1, 2), Pair(3, 3), Pair(5, 5), Pair(8, 6)), 8, 6)
        )

        assertEquals(listOf(
            DiffBlock(0, 0, 0, 1),
            DiffBlock(3, 1, 4, 0),
        ),
            findDiff(listOf(Pair(1, 2), Pair(2, 3), Pair(3, 4)), 4, 4)
        )

        assertEquals(listOf(),
            findDiff(listOf(Pair(1, 1), Pair(2, 2), Pair(3, 3), Pair(4, 4), Pair(5, 5),
                Pair(6, 6), Pair(7, 7)), 7, 7)
        )
    }

    @Test
    fun diffToContextTests() {
        assertEquals(listOf(ContextBlock(0, 8, 0, 4),
            ContextBlock(12, 15, 8, 22),
            ContextBlock(29, 3, 32, 8)),
        diffToContext(listOf(DiffBlock(1, 4, 1, 0),
            DiffBlock(15, 1, 11, 2),
            DiffBlock(20, 4, 17, 10),
            DiffBlock(32, 0, 35, 5)), 32, 40))

        assertEquals(listOf(), diffToContext(listOf(), 30, 30))
    }

    @Test
    fun markLinesTests() {
        assertEquals(Pair(listOf(' ', '!', '!', ' ', '-'), listOf('+', ' ', '!', ' ')),
            markLines(listOf(DiffBlock(0, 0, 0, 1),
                DiffBlock(1, 2, 2, 1),
                DiffBlock(4, 1, 4, 0)), 5, 4))
    }

    @Test
    fun formatTests() {
        assertEquals(Format.UnifiedContext, getFormat("aaauaaaaa"))
        assertEquals(Format.Default, getFormat("aaabaaaaa"))
        assertEquals(Format.UnifiedContext, getFormat("aaauaaauua"))
        assertEquals(Format.EditScript, getFormat("aaaeaaaaa"))
        assertEquals(Format.CopiedContext, getFormat("aaacaaaaa"))
        assertEquals(Format.SideBySide, getFormat("aaayaaaaa"))
        assertFails { getFormat("uc") }
    }
}
