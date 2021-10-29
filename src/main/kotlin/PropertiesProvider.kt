import java.util.Properties
import java.util.logging.Logger

object PropertiesProvider {

    private val logger = Logger.getLogger(PropertiesProvider::class.simpleName)

    fun loadConfig(fileName: String): HashMap<String, String> {
        val stream = Thread.currentThread().contextClassLoader.getResourceAsStream(fileName)
        val conf = Properties()
        conf.load(stream)
        val map = hashMapOf<String, String>()
        conf.stringPropertyNames().asSequence().forEach {
            map[it] = conf.getProperty(it)
        }
        return map
    }

    fun configOf(key: String): String = System.getenv(key) ?: getConfigLocal(key)

    private fun getConfigLocal(name: String) : String {
        val stream = Thread.currentThread().contextClassLoader.getResourceAsStream("application.properties")
        val conf = Properties()
        conf.load(stream)
        return conf.getProperty(name) ?: ""
    }
}
