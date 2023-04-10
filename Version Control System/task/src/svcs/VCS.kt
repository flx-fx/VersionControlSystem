package svcs

import java.io.File
import java.security.MessageDigest
import java.util.HexFormat

class VCS(private val args: Array<String>) {
    private val vcsDir = File(VCS_DIR)
    private val commitsDir = vcsDir.resolve(COMMIT_DIR)
    private val configFile = vcsDir.resolve(CONFIG_FILE)
    private val indexFile = vcsDir.resolve(INDEX_FILE)
    private val logFile = vcsDir.resolve(LOG_FILE)

    init {
        if (!vcsDir.exists()) vcsDir.mkdir()
        if (!commitsDir.exists()) commitsDir.mkdir()
        if (!configFile.exists()) configFile.createNewFile()
        if (!indexFile.exists()) indexFile.createNewFile()
        if (!logFile.exists()) logFile.createNewFile()
    }

    fun config() {
        if (args.size > 1) configFile.writeText(args[1])
        val username = configFile.readText()
        println(if (username.isEmpty()) "Please, tell me who you are." else "The username is $username.")
    }

    fun add() {
        if (args.size > 1) {
            val filename = args[1]
            if (File(filename).exists()) {
                indexFile.appendText("$filename\n")
                println("The file '$filename' is tracked.")
            } else {
                println("Can't find '$filename'.")
            }
        } else {
            val trackedFiles = indexFile.readText()
            println(if (trackedFiles.isEmpty()) "Add a file to the index." else "Tracked files:\n$trackedFiles")
        }
    }

    private fun getHashHex(byteArray: ByteArray): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(byteArray)
        return HexFormat.of().formatHex(messageDigest.digest())
    }

    private fun getIndexedFilesHash(): String {
        var indexedFilesByteArray = byteArrayOf()
        indexFile.forEachLine { indexedFilesByteArray += File(it).readBytes() }
        return getHashHex(indexedFilesByteArray)
    }

    private fun commitFiles(indexedFilesHash: String) {
        val commitDir = commitsDir.resolve(indexedFilesHash)
        commitDir.mkdir()
        indexFile.forEachLine { File(it).copyTo(commitDir.resolve(it)) }
        val logFileContent = logFile.readText()
        logFile.writeText(
            """
                commit $indexedFilesHash
                Author: ${configFile.readText()}
                ${args[1]}
                
            """.trimIndent() + logFileContent
        )
    }

    fun commit() {
        if (args.size < 2) {
            println("Message was not passed.")
        } else {
            val indexedFilesHash = getIndexedFilesHash()
            val committedFilesHash = if (logFile.readLines().isEmpty()) "" else logFile.readLines()[0].split(' ')[1]

            if (indexedFilesHash == committedFilesHash || indexFile.readLines().isEmpty()) {
                println("Nothing to commit.")
            } else {
                commitFiles(indexedFilesHash)
                println("Changes are committed.")
            }
        }
    }

    fun log() {
        if (logFile.readText().isEmpty()) println("No commits yet.")
        else logFile.forEachLine { println(it) }
    }

    private fun switchToCommit(commitDir: File) {
        commitDir.listFiles()?.forEach {
            it.copyTo(File(".${it.path}".replace(commitDir.path, "")), true)
        }
    }

    fun checkout() {
        if (args.size < 2) {
            println("Commit id was not passed.")
        } else {
            val commitDir = commitsDir.resolve(args[1])
            if (!commitDir.exists()) {
                println("Commit does not exist.")
            } else {
                switchToCommit(commitDir)
                println("Switched to commit ${args[1]}.")
            }
        }
    }
}