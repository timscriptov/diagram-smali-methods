import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class DrawFlowDiagram(
    private val smaliFilePath: String,
    private val pictureFormat: String,
    private val methodsToDraw: Array<String>?,
    private val outputDir: String,
    private val dotFilePath: String,
) {
    private val classInSmali: ClassInSmali = ClassInSmali()
    private var curMethodName: String? = null

    fun run() {
        parseClassInSmali()
        draw()
    }

    private fun parseClassInSmali() {
        try {
            BufferedReader(FileReader(smaliFilePath)).use { smaliFile ->
                smaliFile.lineSequence().forEachIndexed { lineIndex, line ->
                    val trimLine = line.trim()
                    if (trimLine.isNotEmpty()) {
                        val splitLine = trimLine.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        if (".class" == splitLine[0]) {
                            classInSmali.setClassName(splitLine[splitLine.size - 1])
                        } else if (".method" == splitLine[0]) {
                            curMethodName = splitLine[splitLine.size - 1].also { methodName ->
                                if (methodsToDraw == null || listOf(*methodsToDraw).contains(methodName) ||
                                    listOf(*methodsToDraw)
                                        .contains(methodName.substring(0, methodName.indexOf("(")))
                                ) {
                                    classInSmali.addMethod(methodName)
                                } else {
                                    curMethodName = null
                                }
                            }
                        } else if (".end method" == splitLine[0]) {
                            curMethodName = null
                        } else {
                            curMethodName?.let { methodName ->
                                classInSmali.addMethodIns(methodName, trimLine, lineIndex + 1)
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun draw() {
        for (method in classInSmali.methodDict.values) {
            try {
                drawMethodFlowDiagram(method)
            } catch (ex: Exception) {
                println("Draw method flow diagram error!\nerror message:" + ex.message + "\nmethod name:" + method.methodName)
            }
        }
    }

    private fun drawMethodFlowDiagram(method: Method) {
        val methodName = method.methodName
        val dotStr = StringBuilder("digraph G{\n\tstart[label=\"start\"]\n")
        for (index in method.instructions.indices) {
            val ins = method.instructions[index]
            if (index == 0) {
                dotStr.append("\tnode[shape=record];\n")
                dotStr.append(getDotStrForNode(ins))
                dotStr.append("\tstart->node_").append(ins.lineNum).append("\n")
            } else {
                dotStr.append(getDotStrForNode(ins))
                var edgeColor = "black"
                val lastInsType = method.instructions[index - 1].type
                if (lastInsType == InstructionType.CON_JUMP) {
                    edgeColor = "red"
                } else if (lastInsType == InstructionType.GOTO || lastInsType == InstructionType.RETURN) {
                    edgeColor = "white"
                }
                dotStr.append(getDotStrForEdge(method.instructions[index - 1].lineNum, ins.lineNum, edgeColor))
                for (child in ins.childrenAbove) {
                    if (ins.type == InstructionType.CON_JUMP) {
                        dotStr.append(getDotStrForEdge(ins.lineNum, child.lineNum, "green"))
                    } else if (ins.type == InstructionType.GOTO) {
                        dotStr.append(getDotStrForEdge(ins.lineNum, child.lineNum, "orange"))
                    }
                }
                for (parent in ins.parentAbove) {
                    if (parent.type == InstructionType.CON_JUMP) {
                        dotStr.append(getDotStrForEdge(parent.lineNum, ins.lineNum, "green"))
                    } else if (parent.type == InstructionType.GOTO) {
                        dotStr.append(getDotStrForEdge(parent.lineNum, ins.lineNum, "orange"))
                    }
                }
            }
        }
        dotStr.append("}")
        parseDotToPicture(dotStr.toString(), methodName)
    }

    private fun parseDotToPicture(dotStr: String, methodName: String) {
        val name = methodName.replace("[/<>]".toRegex(), "")
        val tempDotFileName = "$outputDir/$name.dot"
        File(tempDotFileName).writeText(dotStr)
        val outputSvgFileName = "$outputDir/$name.$pictureFormat"
        try {
            ProcessBuilder(dotFilePath, "-T$pictureFormat", tempDotFileName, "-o", outputSvgFileName).start().waitFor()
            val tempDotFile = File(tempDotFileName)
            if (tempDotFile.exists()) {
                tempDotFile.delete()
            }
            println("Draw method($methodName) flow diagram succeed!")
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun getDotStrForEdge(fromLineNum: Int, toLineNum: Int, edgeColor: String): String {
        return "\tedge[color=$edgeColor]\n" +
                "\tnode_$fromLineNum->node_$toLineNum\n" +
                "\tedge[color=black]\n"
    }

    private fun getDotStrForNode(instruction: Instruction): String {
        val label = if (instruction.ins.startsWith("return")) {
            "style=filled,fillcolor=yellow"
        } else {
            ""
        }
        return "\tnode_" + instruction.lineNum + " [label=\"<f0>" + instruction.lineNum + "|<f1>" + instruction.ins + "\"" + label + "];\n"
    }
}
