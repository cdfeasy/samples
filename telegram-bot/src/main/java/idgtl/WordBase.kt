package idgtl

import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.annotation.PostConstruct
import kotlin.jvm.*

/**
 * Created by dmitry on 30.12.2016.
 */
@Component()
@Scope("singleton")
class WordBase {
    internal var random = Random()
    lateinit internal var config:Config;
    @Value("\${config}")
    lateinit var conf : String

    @PostConstruct
    fun init(){
        var gson= Gson();
        config = gson.fromJson(Files.newBufferedReader(Paths.get(conf)), Config::class.java);
    }

    fun  getTables():List<String>{
        return config.tables;
    }

    fun  getRandomTable():String{
        return config.tables[random.nextInt(config.tables.size)]
    }

    fun  containWord(word:String):Boolean{
        return config.single.contains(word);
    }

}
