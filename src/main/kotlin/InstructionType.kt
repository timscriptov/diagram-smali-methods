object InstructionType {
    const val GOTO = "goto"
    const val GOTO_LABEL = ":goto"
    const val CON_JUMP = "if-"
    const val CON_LABEL = ":cond"
    const val RETURN = "return"
    const val UNKNOWN = "null"

    fun getInsType(ins: String): String {
        if (ins.startsWith(GOTO)) {
            return GOTO
        }
        if (ins.startsWith(GOTO_LABEL)) {
            return GOTO_LABEL
        }
        if (ins.startsWith(CON_JUMP)) {
            return CON_JUMP
        }
        if (ins.startsWith(CON_LABEL)) {
            return CON_LABEL
        }
        return if (ins.startsWith(RETURN)) {
            RETURN
        } else {
            UNKNOWN
        }
    }
}
