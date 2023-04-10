package svcs

enum class Command(private val desc: String = "No description available.") {
    CONFIG("Get and set a username."),
    ADD("Add a file to the index."),
    LOG("Show commit logs."),
    COMMIT("Save changes."),
    CHECKOUT("Restore a file.");

    companion object {
        fun printAllDesc() {
            var maxLength = 0
            for (command in values()) if (command.name.length > maxLength) maxLength = command.name.length
            println("These are SVCS commands:")
            for (command in values()) println("%-${maxLength}s %s".format(command.name.lowercase(), command.desc))
        }

        fun printDescByCommand(command: String) {
            try {
                println(valueOf(command.uppercase()).desc)
            } catch (e: Exception) {
                println("'$command' is not a SVCS command.")
            }
        }
    }
}