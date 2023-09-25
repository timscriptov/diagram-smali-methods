data class Method(
    @JvmField
    val methodName: String
) {
    val instructions = mutableListOf<Instruction>()
    val labelDict = mutableMapOf<String, Instruction>()
    val jumpToLabelDict = mutableMapOf<String, MutableList<Instruction>>()

    fun addIns(ins: String, lineNum: Int) {
        val instruction = Instruction(ins, lineNum)
        when (instruction.type) {
            InstructionType.GOTO_LABEL, InstructionType.CON_LABEL -> {
                if (labelDict.containsKey(ins)) {
                    throw RuntimeException("There are the same label: $ins in method: $methodName")
                }
                if (jumpToLabelDict.containsKey(ins)) {
                    for (eachNode in jumpToLabelDict[ins]!!) {
                        eachNode.addChild(instruction)
                        instruction.addParent(eachNode)
                    }
                    jumpToLabelDict.remove(ins)
                }
                labelDict[ins] = instruction
                instructions.add(instruction)
            }

            InstructionType.GOTO, InstructionType.CON_JUMP -> {
                val label = ins.substring(ins.indexOf(":"))
                if (labelDict.containsKey(label)) {
                    instruction.addChild(labelDict[label]!!)
                    labelDict[label]!!.addParent(instruction)
                } else {
                    if (jumpToLabelDict.containsKey(label)) {
                        jumpToLabelDict[label]!!.add(instruction)
                    } else {
                        jumpToLabelDict[label] = ArrayList(listOf(instruction))
                    }
                }
                instructions.add(instruction)
            }

            InstructionType.RETURN -> {
                instructions.add(instruction)
            }
        }
    }
}
