package id.isabella.zendesk.sgrail

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SgrailApplication

fun main(args: Array<String>) {
	runApplication<SgrailApplication>(*args)
}
