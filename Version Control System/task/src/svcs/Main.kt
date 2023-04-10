package svcs

const val VCS_DIR = "vcs"
const val COMMIT_DIR = "commits"
const val CONFIG_FILE = "config.txt"
const val INDEX_FILE = "index.txt"
const val LOG_FILE = "log.txt"

fun main(args: Array<String>) {
    val vcs = VCS(args)

    if (args.isEmpty()) Command.printAllDesc()
    else when (args[0].lowercase()) {
        "config" -> vcs.config()
        "add" -> vcs.add()
        "commit" -> vcs.commit()
        "log" -> vcs.log()
        "checkout" -> vcs.checkout()
        "--help" -> Command.printAllDesc()
        else -> Command.printDescByCommand(args[0].uppercase())
    }
}