class Instruction(val ins: String, val lineNum: Int) {
    val type = InstructionType.getInsType(ins.trim())
    val children: MutableList<Instruction> = ArrayList()
    val parents: MutableList<Instruction> = ArrayList()

    fun addChild(child: Instruction) {
        children.add(child)
    }

    fun addParent(parent: Instruction) {
        parents.add(parent)
    }

    val childrenAbove: List<Instruction>
        get() {
            val result: MutableList<Instruction> = ArrayList()
            for (child in children) {
                if (child.lineNum < lineNum) {
                    result.add(child)
                }
            }
            return result
        }

    val parentAbove: List<Instruction>
        get() {
            val result: MutableList<Instruction> = ArrayList()
            for (parent in parents) {
                if (parent.lineNum < lineNum) {
                    result.add(parent)
                }
            }
            return result
        }

    override fun toString(): String {
        return "lineNum: $lineNum ins: $ins"
    }
}
