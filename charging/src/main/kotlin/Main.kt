fun main() {
    println(
        """
---
        _ _   _                                 _____ 
  /\/\ (_) |_| |_ ___ _ ____      _____  __ _  |___  |
 /    \| | __| __/ _ \ '__\ \ /\ / / _ \/ _` |    / / 
/ /\/\ \ | |_| ||  __/ |   \ V  V /  __/ (_| |   / /  
\/    \/_|\__|\__\___|_|    \_/\_/ \___|\__, |  /_/   
                                        |___/                  

Charging version 0.6.0
---
    """.trimIndent()
    )

    val charging = Charging()
    val httpServer = HttpServer(charging)

    charging.start()
    httpServer.start()
}