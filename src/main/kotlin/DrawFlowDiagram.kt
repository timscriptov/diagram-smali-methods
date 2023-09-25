import java.io.File

class DrawFlowDiagram(
    private val smaliFilePath: String,
    private val pictureFormat: String,
    private val methodsToDraw: List<String>?,
    private val outputDir: String,
    private val dotFilePath: String
) {
    private val classInSmali = ClassInSmali()
    private var curMethodName: String? = null

    fun run() {
        parseClassInSmali()
        draw()
    }

    private fun parseClassInSmali() {
        File(smaliFilePath).bufferedReader().use { br ->
            br.lineSequence().forEachIndexed { lineIndex, line ->
                val lineInfos = line.trim().split(" ")
                when (lineInfos[0]) {
                    ".class" -> classInSmali.setClassName(lineInfos.last())
                    ".method" -> {
                        curMethodName = lineInfos.last().also { methodName ->
                            if (methodsToDraw == null || methodName in methodsToDraw ||
                                methodName.substringBefore("(") in methodsToDraw
                            ) {
                                classInSmali.addMethod(methodName)
                            } else {
                                curMethodName = null
                            }
                        }
                    }

                    ".end" -> curMethodName = null
                    else -> {
                        curMethodName?.let { methodName ->
                            classInSmali.addMethodIns(methodName, line, lineIndex + 1)
                        }
                    }
                }
            }
        }
    }

    private fun draw() {
        classInSmali.methodDict.values.forEach { method ->
            try {
                drawMethodFlowDiagram(method)
            } catch (ex: Exception) {
                println("\n\tDraw method flow diagram error!\n\terror message: ${ex.message}\n\tmethod name: ${method.methodName}\n")
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

        val outputFileName = "$outputDir/$name.$pictureFormat"
        val args = listOf(dotFilePath, "-T$pictureFormat", tempDotFileName, "-o", outputFileName)
        val process = ProcessBuilder(args).start()
        process.waitFor()
        File(tempDotFileName).delete()
        println("Draw method($methodName) flow diagram succeeded!")
    }

    private fun getDotStrForEdge(fromLineNum: Int, toLineNum: Int, edgeColor: String): String {
        return "\tedge[color= $edgeColor ]\n\tnode_$fromLineNum->node_$toLineNum\n\tedge[color=black]\n";
    }

    private fun getDotStrForNode(instruction: Instruction): String {
        return if (instruction.ins.startsWith("return")) {
            "\tnode_${instruction.lineNum} [label=\"<f0>${instruction.lineNum}|<f1>${instruction.ins}\",style=filled,fillcolor=yellow];\n"
        } else {
            "\tnode_${instruction.lineNum} [label=\"<f0>${instruction.lineNum}|<f1>${instruction.ins}\"];\n"
        }
    }
}
