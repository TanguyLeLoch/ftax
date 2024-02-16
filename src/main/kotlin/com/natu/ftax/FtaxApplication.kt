package com.natu.ftax

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FtaxApplication

fun main(args: Array<String>) {
	runApplication<FtaxApplication>(*args)
}
