import com.hivemq.client.mqtt.mqtt3.Mqtt3Client

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

Controller version 0.2.0
---
    """.trimIndent()
    )


    val client = Mqtt3Client.builder()
        .serverHost("mitterweg7")
        .serverPort(1883)
        .automaticReconnect()
        .applyAutomaticReconnect()
        .buildBlocking()
    client.connect()

    val yannickBedroomController = YannickBedroomController(client)

    val terrariumTimer = DeviceTimer(9, 0, 19, 0, {
        yannickBedroomController.turnOnTerrariumLight()
    }, {
        yannickBedroomController.turnOffTerrariumLight()
    }, name = "Terrarium")
    terrariumTimer.start()

    val growLightsTimer = DeviceTimer(9, 0, 19, 0, {
        yannickBedroomController.turnOnGrowLights()
    }, {
        yannickBedroomController.turnOffGrowLights()
    }, name = "Growlights")
    growLightsTimer.start()

    val aquariumTimer = DeviceTimer(9, 0, 17, 0, {
        yannickBedroomController.turnOnAquariumLight()
    }, {
        yannickBedroomController.turnOffAquariumLight()
    }, name = "Aquarium")
    aquariumTimer.start()
}