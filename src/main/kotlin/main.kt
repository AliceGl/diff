import kotlin.math.max

/* function longestCommonSubsequence finds LCS of arrays a and b
 and returns array that contains pairs of indexes that elements of LCS
 have in arrays a and b
 */
fun <T> longestCommonSubsequence (a: Array<T>, b: Array<T>): Array<Pair<Int, Int>> {
    val n = a.size
    val m = b.size
    val lcsDP: Array<Array<Int>> = Array(n + 1) { Array(m + 1) {0} }
    //lcs[i][j] - LCS for a[0..i - 1] and b[0..j - 1]
    for (i in 1..n) {
        for (j in 1..m) {
            if (a[i - 1] == b[j - 1])
                lcsDP[i][j] = lcsDP[i - 1][j - 1] + 1
            else
                lcsDP[i][j] = max(lcsDP[i - 1][j], lcsDP[i][j - 1])
        }
    }
    val lcsSize = lcsDP[n][m]
    val lcs : Array<Pair<Int, Int>> = Array(lcsSize) { Pair(0, 0) }

    //finding LCS's indexes in a and b
    var i = n
    var j = m
    var curPair = lcsSize - 1
    while (curPair >= 0) {
        if (a[i - 1] == b[j - 1]) {
            lcs[curPair] = Pair(i - 1, j - 1)
            --i; --j; --curPair
        } else if (lcsDP[i][j] == lcsDP[i - 1][j])
            --i
        else
            --j
    }

    return lcs
}

fun main(args: Array<String>) {
    TODO()
}
