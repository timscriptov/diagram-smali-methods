import java.io.File

fun main(args: Array<String>) {
    val parser = ArgumentParser(args)

    parser.addArgument("-s", "The smali file path")
    parser.addArgument("-f", "The picture format")
    parser.addArgument("-m", "The method name or method signature")
    parser.addArgument("-o", "The output flow diagrams' directory")
    parser.addArgument("-d", "The dot.exe application path")

    parser.parseArgs()

    val smaliFilePath = parser.getString("-s")
    val pictureFormat = parser.getString("-f") ?: "png"
    val methodsToDraw = parser.getList("-m")
    val outputDir = parser.getString("-o") ?: System.getProperty("user.dir")
    val dotFilePath = parser.getString("-d")

    if (smaliFilePath.isNullOrEmpty() || dotFilePath.isNullOrEmpty()) {
        parser.printHelp()
        return
    }

    if (!File(smaliFilePath).exists()) {
        println("\nError: $smaliFilePath doesn't exist.\n")
        return
    }

    DrawFlowDiagram(smaliFilePath, pictureFormat, methodsToDraw, outputDir, dotFilePath).run()
}
