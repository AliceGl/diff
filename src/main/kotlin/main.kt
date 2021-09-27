import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
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
    //lcs[x][y] - LCS for a[0..x - 1] and b[0..y - 1]
    for (x in 1..n) {
        for (y in 1..m) {
            if (a[x - 1] == b[y - 1])
                lcsDP[x][y] = lcsDP[x - 1][y - 1] + 1
            else
                lcsDP[x][y] = max(lcsDP[x - 1][y], lcsDP[x][y - 1])
        }
    }
    val lcsSize = lcsDP[n][m]
    val lcs = MutableList(lcsSize) { Pair(0, 0) }

    //finding LCS's indexes in a and b
    var x = n
    var y = m
    var curPair = lcsSize - 1
    while (curPair >= 0) {
        if (a[x - 1] == b[y - 1]) {
            lcs[curPair] = Pair(x, y)
            --x; --y; --curPair
        } else if (lcsDP[x][y] == lcsDP[x - 1][y])
            --x
        else
            --y
    }

    return lcs
}

data class DiffBlock (val originalL : Int, //start of range of changed lines in original file
                      val originalS : Int, //start of range of changed lines in original file
                      val newL : Int,      //start of range of changed lines in new file
                      val newS : Int       //number of added/changed lines in new file
)

const val contextSize = 3
/* context block is a diff block with contextSize lines before and contextSize lines after it
 if context blocks intersect or there is no lines between them, then they combine
 */
data class ContextBlock (val originalL : Int, val originalS : Int, val newL : Int, val newS : Int)

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

fun diffToContext(diff : List<DiffBlock>, originalSize : Int, newSize : Int) : List<ContextBlock> {
    val context : MutableList<ContextBlock> = mutableListOf()
    var curBlock = ContextBlock(-1, -1, 0, 0)
    diff.forEach {
        curBlock = if (curBlock.originalL == -1 ||
            it.originalL - (curBlock.originalL + curBlock.originalS) > contextSize) {
            if (curBlock.originalL != -1)
                context.add(curBlock)
            ContextBlock(it.originalL - contextSize,
                it.originalS + 2 * contextSize,
                it.newL - contextSize,
                it.newS + 2 * contextSize
            )
        } else {
            ContextBlock(curBlock.originalL,
                it.originalL - curBlock.originalL + it.originalS + contextSize,
                curBlock.newL,
                it.newL - curBlock.newL + it.newS + contextSize
            )
        }
    }
    if (curBlock.originalL != -1)
        context.add(curBlock)
    if (context.isNotEmpty()) {
        val originalBegin = max(0, -context[0].originalL)
        val newBegin = max(0, -context[0].newL)
        context[0] = ContextBlock(context[0].originalL + originalBegin,
            context[0].originalS - originalBegin, context[0].newL + newBegin,
            context[0].newS - newBegin)

        val originalEnd = max(0, context.last().originalL + context.last().originalS - originalSize)
        val newEnd = max(0, context.last().newL + context.last().newS - newSize)
        context[context.lastIndex] = ContextBlock(context.last().originalL,
            context.last().originalS - originalEnd,
            context.last().newL, context.last().newS - newEnd)
    }
    return context
}

fun markLines(diff : List<DiffBlock>, originalSize: Int, newSize: Int) : Pair<List<Char>, List<Char>> {
    val originalMarks = MutableList(originalSize) {' '}
    val newMarks = MutableList(newSize) {' '}
    diff.forEach {
        for (x in it.originalL until it.originalL + it.originalS)
            originalMarks[x] = if (it.newS == 0) '-' else '!'
        for (x in it.newL until it.newL + it.newS)
            newMarks[x] = if (it.originalS == 0) '+' else '!'
    }
    return Pair(originalMarks, newMarks)
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
    val validOptions = "ecuysi"

    options.forEach {
        check(it in validOptions) { "There is no option \"$it\"" }
    }

    check(files.size <= 3) {"Too many arguments"}
    files.forEach {
        check(File(it).exists()) { "There is no file \"$it\"" }
    }
    check(files.size >= 2) {"Not enough arguments"}
}

class TextFile(val path: String, ignoreCase: Boolean) {
    val text : List<String> = File(path).readLines()
    val size = text.size
    val textHash = text.map { (if (ignoreCase) it.lowercase() else it).hashCode() }
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

const val RESET = "\u001B[0m"
const val RED = "\u001B[31m"
const val GREEN = "\u001B[32m"
const val PURPLE = "\u001B[35m"

fun printLine(writer : BufferedWriter?, line : String, color: String = RESET) {
    if (writer == null)
        println(color + line + RESET)
    else
        writer.write(line + '\n')
}

fun getHead(diffBlock: DiffBlock) : String {
    val head = StringBuilder()
    head.append(when (diffBlock.originalS) {
        0 -> diffBlock.originalL
        1 -> diffBlock.originalL + 1
        else -> (diffBlock.originalL + 1).toString() + "," +
                (diffBlock.originalS + diffBlock.originalL).toString()
    })
    head.append(when {
        diffBlock.originalS == 0 -> 'a'
        diffBlock.newS == 0 -> 'd'
        else -> 'c'
    })
    head.append(when (diffBlock.newS) {
        0 -> diffBlock.newL
        1 -> diffBlock.newL + 1
        else -> (diffBlock.newL + 1).toString() + "," +
                (diffBlock.newS + diffBlock.newL).toString()
    })
    return head.toString()
}

fun defaultOutput(diff : List<DiffBlock>, originalFile : TextFile,
                  newFile : TextFile, writer : BufferedWriter?) {
    diff.forEach {
        printLine(writer, getHead(it))

        for (x in 0 until it.originalS)
            printLine(writer, "< " + originalFile.text[it.originalL + x], RED)
        if (it.originalS != 0)
            printLine(writer,"---")
        for (x in 0 until it.newS)
            printLine(writer, "> " + newFile.text[it.newL + x], GREEN)
    }
}

fun editScriptOutput(diff : List<DiffBlock>, newFile : TextFile, writer : BufferedWriter?) {
    diff.reversed().forEach {
        printLine(writer, getHead(it))

        for (x in 0 until it.newS)
            printLine(writer, newFile.text[it.newL + x])
        if (it.newS != 0)
            printLine(writer, ".")
    }
}

fun copiedContextOutput(diff : List<DiffBlock>, originalFile : TextFile,
                  newFile : TextFile, writer : BufferedWriter?) {
    val context = diffToContext(diff, originalFile.size, newFile.size)
    val (originalMarks, newMarks) = markLines(diff, originalFile.size, newFile.size)

    printLine(writer, "*** " + originalFile.path, RED)
    printLine(writer, "--- " + newFile.path, GREEN)
    context.forEach {
        printLine(writer, "***************")
        printLine(writer, "*** " + (it.originalL + 1).toString() + ","
                + (it.originalL + it.originalS).toString() + " ****")
        if ((it.originalL until it.originalL + it.originalS).any { x -> originalMarks[x] != ' ' })
            for (x in it.originalL until it.originalL + it.originalS)
                printLine(writer, originalMarks[x] + " " + originalFile.text[x],
                    if (originalMarks[x] != ' ') RED else RESET)

        printLine(writer, "--- " + (it.newL + 1).toString() + "," +
                (it.newL + it.newS).toString() + " ----")
        if ((it.newL until it.newL + it.newS).any {x -> newMarks[x] != ' '})
            for (x in it.newL until it.newL + it.newS)
                printLine(writer, newMarks[x] + " " + newFile.text[x],
                    if (newMarks[x] != ' ') GREEN else RESET)
    }
}

fun unifiedContextOutput(diff : List<DiffBlock>, originalFile : TextFile,
                  newFile : TextFile, writer : BufferedWriter?) {
    val context = diffToContext(diff, originalFile.size, newFile.size)
    val (originalMarks, newMarks) = markLines(diff, originalFile.size, newFile.size)

    printLine(writer, "--- " + originalFile.path, RED)
    printLine(writer, "+++ " + newFile.path, GREEN)
    context.forEach {
        printLine(writer, "@@ -" + (it.originalL + 1).toString() + "," + it.originalS.toString()
                + " +" + (it.newL + 1).toString() + "," + it.newS.toString() + " @@", PURPLE)
        var originalCur = it.originalL
        var newCur = it.newL
        while (originalCur != it.originalL + it.originalS || newCur != it.newL + it.newS) {
            if (newCur == it.newL + it.newS)
                printLine(writer, "-" + originalFile.text[originalCur++], RED)
            else if (originalCur == it.originalL + it.originalS)
                printLine(writer, "+" + newFile.text[newCur++], GREEN)
            else if (originalMarks[originalCur] == ' ' && newMarks[newCur] == ' ') {
                printLine(writer, " " + originalFile.text[originalCur++])
                newCur++
            }
            else if (originalMarks[originalCur] != ' ')
                printLine(writer, "-" + originalFile.text[originalCur++], RED)
            else
                printLine(writer, "+" + newFile.text[newCur++], GREEN)
        }
    }
}

fun sideBySideOutput(diff : List<DiffBlock>, originalFile : TextFile,
                  newFile : TextFile, writer : BufferedWriter?) {
    val width = originalFile.text.maxOf {it.length} + 38
    var curBlock = 0
    var originalLine = 0
    var newLine = 0
    while (originalLine != originalFile.size || newLine != newFile.size) {
        if (curBlock < diff.size && originalLine == diff[curBlock].originalL && newLine == diff[curBlock].newL) {
            val common = min(diff[curBlock].originalS, diff[curBlock].newS)
            for (x in 0 until common)
                printLine(writer, originalFile.text[originalLine + x].padEnd(width) + "| "
                        + newFile.text[newLine + x])
            for (x in common until diff[curBlock].originalS)
                printLine(writer, originalFile.text[originalLine + x].padEnd(width) + "<")
            for (x in common until diff[curBlock].newS)
                printLine(writer, "".padEnd(width) + "> " + newFile.text[newLine + x])

            originalLine += diff[curBlock].originalS
            newLine += diff[curBlock].newS
            ++curBlock
        } else {
            printLine(writer, originalFile.text[originalLine].padEnd(width) + "  "
                    + newFile.text[newLine])
            ++originalLine
            ++newLine
        }
    }
}

fun output(diff : List<DiffBlock>, format : Format, originalFile : TextFile,
           newFile : TextFile, writer : BufferedWriter?) {
    when (format) {
        Format.Default -> defaultOutput(diff, originalFile, newFile, writer)
        Format.EditScript -> editScriptOutput(diff, newFile, writer)
        Format.CopiedContext -> copiedContextOutput(diff, originalFile, newFile, writer)
        Format.UnifiedContext -> unifiedContextOutput(diff, originalFile, newFile, writer)
        Format.SideBySide -> sideBySideOutput(diff, originalFile, newFile, writer)
    }
}

fun getOtherOptions(options: String) : List<Boolean> {
    val result = MutableList(2) {false}

    result[0] = options.contains('s')
    result[1] = options.contains('x')

    return result
}

fun main(args: Array<String>) {
    val (options, files) = input(args.toList())
    checkValid(options, files)
    val format = getFormat(options)
    val (reportIdentical, ignoreCase) = getOtherOptions(options)
    val originalFile = TextFile(files[0], ignoreCase)
    val newFile = TextFile(files[1], ignoreCase)
    val diff = findDiff(longestCommonSubsequence(originalFile.textHash, newFile.textHash),
        originalFile.size, newFile.size)
    val writer = if (files.size > 2) File(files[2]).bufferedWriter() else null
    if (reportIdentical && diff.isEmpty())
        printLine(writer,"Files ${originalFile.path} and ${newFile.path} are identical")
    else if (diff.isNotEmpty())
        output(diff, format, originalFile, newFile, writer)
    writer?.close()
}
