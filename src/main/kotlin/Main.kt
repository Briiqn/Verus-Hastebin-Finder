import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import kotlin.collections.HashSet
import kotlin.concurrent.thread
import kotlin.math.pow
val usedStrings = Collections.synchronizedSet(HashSet<String>())

fun main() {
    val charPool : List<Char> = ('a'..'z').toList()
    val random = Random()

    val numThreads = Runtime.getRuntime().availableProcessors()
    val stringsPerThread = charPool.size.toDouble().pow(10.0) / numThreads

    val threads = (1..numThreads).map {
        thread {
            var count =0
            while (count < numThreads) {

                (1..stringsPerThread.toInt()).forEach { _ ->
                    var randomString : String
                    do {
                        randomString = (1..10)
                            .map { charPool[random.nextInt(charPool.size)] }
                            .joinToString("")
                    } while (usedStrings.contains(randomString))
                    usedStrings.add(randomString)
                    if (getHastebinText(randomString) != null && (getHastebinText(randomString)!!.contains("failed") || getHastebinText(
                            randomString
                        )!!.contains("Ping"))
                    ) {
                        println("Valid Hastebin https://paste.verus.ac/raw/${randomString} ")
                        File("validurls.txt").appendText("\nhttps://paste.verus.ac/raw/${randomString}")

                    }
                }
                count++
            }
        }
    }
    threads.forEach { it.join() }

}

fun getHastebinText(id :String): String? {
    try {
        val connection = URL("https://paste.verus.ac/raw/${id}").openConnection()
        connection.setRequestProperty("User-Agent", "Mozilla/5.0") // set user agent to avoid 403 error
        connection.getInputStream()
        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val response = StringBuffer()
        var line : String?
        while (reader.readLine().also { line = it } != null) {
            response.append(line)
            response.append('\n')
        }
        reader.close()
        return response.toString()
    } catch (ex : Exception) {
        if (ex !is FileNotFoundException) {
            println("Rate Limited | Used ${usedStrings.size} Strings ")
            Thread.sleep(36500)

        } else {
            println("$id did not have any flags ")

        }
    }
    return null
}

