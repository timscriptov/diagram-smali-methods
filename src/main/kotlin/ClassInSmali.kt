data class ClassInSmali(
    @JvmField
    var className: String? = null
) {
    val methodDict = mutableMapOf<String, Method>()

    fun setClassName(className: String) {
        this.className?.let {
            throw Exception("More than one class in smali file!")
        }
        this.className = className
    }

    fun addMethod(methodName: String) {
        if (methodName in methodDict) {
            throw Exception("There are methods with the same name\nmethod name = $methodName")
        }
        methodDict[methodName] = Method(methodName)
    }

    fun addMethodIns(methodName: String, ins: String, lineNum: Int) {
        if (methodName !in methodDict) {
            println("method->$methodName does not exist")
        }
        methodDict[methodName]?.addIns(ins, lineNum)
    }
}
