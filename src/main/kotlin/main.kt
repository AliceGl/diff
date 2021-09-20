import java.io.File
import kotlin.math.max
import kotlin.math.min

/* function longestCommonSubsequence finds LCS of lists a and b
 and returns list that contains pairs of indexes that elements of LCS
 have in lists a and b (numbering of elements starts from one)
 */
fun <T> longestCommonSubsequence (a: List<T>, b: List<T>): List<Pair<Int, Int>> {
    val n = a.size
    val m = b.size
    val lcsDP = List(n + 1) { MutableList(m + 1) {0} }
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
    val lcs = MutableList(lcsSize) { Pair(0, 0) }

    //finding LCS's indexes in a and b
    var i = n
    var j = m
    var curPair = lcsSize - 1
    while (curPair >= 0) {
        if (a[i - 1] == b[j - 1]) {
            lcs[curPair] = Pair(i, j)
            --i; --j; --curPair
        } else if (lcsDP[i][j] == lcsDP[i - 1][j])
            --i
        else
            --j
    }

    return lcs
}

data class DiffBlock (val originalL : Int, //start of range of changed lines in original file
                      val originalS : Int, //start of range of changed lines in original file
                      val newL : Int,      //start of range of changed lines in new file
                      val newS : Int       //number of added/changed lines in new file
)

/* function findDiff takes list from function longestCommonSubsequence and number of lines
in original and new file and returns list of changes/additions/deletions, where one
element describes one change/addition/deletion in file
 */
fun findDiff (indexes: List<Pair<Int, Int>>, originalSize: Int, newSize: Int) : List<DiffBlock> {
    var originalPrev = 0
    var newPrev = 0
    val result : MutableList<DiffBlock> = mutableListOf()
    indexes.forEach {
        if (!(originalPrev + 1 == it.first && newPrev + 1 == it.second))
            result.add(DiffBlock(originalPrev, it.first - originalPrev - 1,
                newPrev, it.second - newPrev - 1))
        originalPrev = it.first
        newPrev = it.second
    }
    if (!(originalPrev == originalSize && newPrev == newSize))
        result.add(DiffBlock(originalPrev, originalSize - originalPrev,
            newPrev, newSize - newPrev))

    return result
}

fun read() : List<String> {
    val list : MutableList<String> = mutableListOf()
    while(true) {
        val line = readLine() ?: break
        list.addAll(line.split(' '))
    }
    return list
}

fun input(args: List<String>) : Pair<String, List<String>> {
    val options = StringBuilder()
    val files : MutableList<String> = mutableListOf()
    for (arg in args.plus(read())) {
        if (arg == "")
            continue
        if (arg[0] == '-' && files.isEmpty())
            options.append(arg.substring(1))
        else
            files.add(arg)
    }
    return Pair(options.toString(), files)
}

fun checkValid(options: String, files : List<String>) {
    val validOptions = "ecuy"

    options.forEach {
        check(it in validOptions) { "There is no option \"$it\"" }
    }

    check(files.size <= 2) {"Too many arguments"}
    files.forEach {
        check(File(it).exists()) { "There is no file \"$it\"" }
    }
    check(files.size >= 2) {"Not enough arguments"}
}

class TextFile(val path: String) {
    val text : List<String> = File(path).readLines()
    val size = text.size
}

enum class Format { Default, EditScript, CopiedContext, UnifiedContext, SideBySide }

fun getFormatFromChar(c : Char) = when (c) {
    'e' -> Format.EditScript
    'c' -> Format.CopiedContext
    'u' -> Format.UnifiedContext
    'y' -> Format.SideBySide
    else -> Format.Default
}

fun getFormat(options: String) : Format {
    var result = Format.Default
    for (f in "ecuy") {
        if (f in options) {
            check(result == Format.Default || result == getFormatFromChar(f)) { "Option conflict" }
            result = getFormatFromChar((f))
        }
    }
    return result
}

fun printLine(outputPath : String?, line : String) =
    if (outputPath is String) File(outputPath).writeText(line)
    else println(line)

fun defaultOutput(diff : List<DiffBlock>, originalFile : TextFile,
                  newFile : TextFile, outputPath : String?) {
    diff.forEach {
        val head = StringBuilder()
        head.append(when (it.originalS) {
            0 -> it.originalL
            1 -> it.originalL + 1
            else -> (it.originalL + 1).toString() + "," + (it.originalS + it.originalL).toString()
        })
        head.append(when {
            it.originalS == 0 -> 'a'
            it.newS == 0 -> 'd'
            else -> 'c'
        })
        head.append(when (it.newS) {
            0 -> it.newL
            1 -> it.newL + 1
            else -> (it.newL + 1).toString() + "," + (it.newS + it.newL).toString()
        })
        printLine(outputPath, head.toString())

        for (i in 0 until it.originalS)
            printLine(outputPath, "< " + originalFile.text[it.originalL + i])
        if (it.originalS != 0)
            printLine(outputPath,"---")
        for (i in 0 until it.newS)
            printLine(outputPath, "> " + newFile.text[it.newL + i])
    }
}

fun editScriptOutput(diff : List<DiffBlock>, originalFile : TextFile,
                  newFile : TextFile, outputPath : String?) {
    diff.reversed().forEach {
        val head = StringBuilder()
        head.append(when (it.originalS) {
            0 -> it.originalL
            1 -> it.originalL + 1
            else -> (it.originalL + 1).toString() + "," + (it.originalS + it.originalL).toString()
        })
        head.append(when {
            it.originalS == 0 -> 'a'
            it.newS == 0 -> 'd'
            else -> 'c'
        })
        printLine(outputPath, head.toString())

        for (i in 0 until it.newS)
            printLine(outputPath, newFile.text[it.newL + i])
        if (it.newS != 0)
            printLine(outputPath, ".")
    }
}

fun copiedContextOutput(diff : List<DiffBlock>, originalFile : TextFile,
                  newFile : TextFile, outputPath : String?) {
    TODO()
}

fun unifiedContextOutput(diff : List<DiffBlock>, originalFile : TextFile,
                  newFile : TextFile, outputPath : String?) {
    TODO()
}

fun sideBySideOutput(diff : List<DiffBlock>, originalFile : TextFile,
                  newFile : TextFile, outputPath : String?) {
    val width = originalFile.text.maxOf {it.length} + 10
    var curBlock = 0
    var originalLine = 0
    var newLine = 0
    while (originalLine != originalFile.text.size || newLine != newFile.text.size) {
        if (curBlock < diff.size && originalLine == diff[curBlock].originalL && newLine == diff[curBlock].newL) {
            val common = min(diff[curBlock].originalS, diff[curBlock].newS)
            for (i in 0 until common)
                printLine(outputPath, originalFile.text[originalLine + i].padEnd(width) + "| "
                        + newFile.text[newLine + i])
            for (i in common until diff[curBlock].originalS)
                printLine(outputPath, originalFile.text[originalLine + i].padEnd(width) + "<")
            for (i in common until diff[curBlock].newS)
                printLine(outputPath, "".padEnd(width) + "> " + newFile.text[newLine + i])

            originalLine += diff[curBlock].originalS
            newLine += diff[curBlock].newS
            ++curBlock
        } else {
            printLine(outputPath, originalFile.text[originalLine].padEnd(width) + "  "
                    + newFile.text[newLine])
            ++originalLine
            ++newLine
        }
    }
}

fun output(diff : List<DiffBlock>, format : Format, originalFile : TextFile,
           newFile : TextFile, outputPath : String?) {
    when (format) {
        Format.Default -> defaultOutput(diff, originalFile, newFile, outputPath)
        Format.EditScript -> editScriptOutput(diff, originalFile, newFile, outputPath)
        Format.CopiedContext -> copiedContextOutput(diff, originalFile, newFile, outputPath)
        Format.UnifiedContext -> unifiedContextOutput(diff, originalFile, newFile, outputPath)
        Format.SideBySide -> sideBySideOutput(diff, originalFile, newFile, outputPath)
    }
}

fun main(args: Array<String>) {
    val (options, files) = input(args.toList())
    checkValid(options, files)
    val format = getFormat(options)
    val originalFile = TextFile(files[0])
    val newFile = TextFile(files[1])
    val diff = findDiff(longestCommonSubsequence(originalFile.text, newFile.text),
        originalFile.size, newFile.size)
    output(diff, format, originalFile, newFile, if (files.size > 2) files[2] else null)
}
