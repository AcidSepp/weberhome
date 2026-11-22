@file:Suppress("PropertyName")

import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets

private const val AQUARIUM_TOPIC_SET = "zigbee2mqtt/Aquarium/set"
private const val TERRARIUM_TOPIC_SET = "zigbee2mqtt/Terrarium/set"
private const val GMIATLICHT_TOPIC_SET = "zigbee2mqtt/Gmiatlicht/set"

private const val AQUARIUM_TOPIC_GET = "zigbee2mqtt/Aquarium/get"
private const val TERRARIUM_TOPIC_GET = "zigbee2mqtt/Terrarium/get"
private const val GMIATLICHT_TOPIC_GET = "zigbee2mqtt/Gmiatlicht/get"

class YannickBedroomController(val client: Mqtt3BlockingClient) {

    fun turnOffGrowLights() {
        val payload = Json.encodeToString(AquariumPowerStripState("OFF", "OFF"))
            .toByteArray()
        client.publishWith()
            .topic(AQUARIUM_TOPIC_SET)
            .payload(payload)
            .send()
    }

    fun turnOnGrowLights() {
        val payload = Json.encodeToString(AquariumPowerStripState("ON", "ON"))
            .toByteArray()
        client.publishWith()
            .topic(AQUARIUM_TOPIC_SET)
            .payload(payload)
            .send()
    }

    fun getGrowLightsState(): AquariumPowerStripState {
        val payload = Json.encodeToString(AquariumPowerStripState("", ""))
            .toByteArray()
        client.subscribeWith()
            .topicFilter(AQUARIUM_TOPIC_GET)
            .send()
        val publishes = client.publishes(MqttGlobalPublishFilter.ALL)
        client.publishWith()
            .topic(AQUARIUM_TOPIC_GET)
            .payload(payload)
            .send()
        client.unsubscribeWith()
            .topicFilter(AQUARIUM_TOPIC_GET)
            .send()
        return Json.decodeFromString(
            publishes.receive().payloadAsBytes.toString(
                StandardCharsets.UTF_8
            )
        )
    }

    fun turnOffDeskLamp() {
        val payload = Json.encodeToString(AquariumPowerStripState("OFF"))
            .toByteArray()
        client.publishWith()
            .topic(AQUARIUM_TOPIC_SET)
            .payload(payload)
            .send()
    }

    fun turnOnDeskLamp() {
        val payload = Json.encodeToString(AquariumPowerStripState("ON"))
            .toByteArray()
        client.publishWith()
            .topic(AQUARIUM_TOPIC_SET)
            .payload(payload)
            .send()
    }

    fun turnOffAquariumLight() {
        val payload =
            Json.encodeToString(AquariumPowerStripState(state_4 = "OFF"))
                .toByteArray()
        client.publishWith()
            .topic(AQUARIUM_TOPIC_SET)
            .payload(payload)
            .send()
    }

    fun turnOnAquariumLight() {
        val payload =
            Json.encodeToString(AquariumPowerStripState(state_4 = "ON"))
                .toByteArray()
        client.publishWith()
            .topic(AQUARIUM_TOPIC_SET)
            .payload(payload)
            .send()
    }

    fun turnOffTerrariumLight() {
        val payload = Json.encodeToString(State(state = "OFF"))
            .toByteArray()
        client.publishWith()
            .topic(TERRARIUM_TOPIC_SET)
            .payload(payload)
            .send()
    }

    fun turnOnTerrariumLight() {
        val payload = Json.encodeToString(State(state = "ON"))
            .toByteArray()
        client.publishWith()
            .topic(TERRARIUM_TOPIC_SET)
            .payload(payload)
            .send()
    }

    fun turnOffGmiatlicht() {
        val payload = Json.encodeToString(State(state = "OFF"))
            .toByteArray()
        client.publishWith()
            .topic(GMIATLICHT_TOPIC_SET)
            .payload(payload)
            .send()
    }

    fun turnOnGmiatlicht() {
        val payload = Json.encodeToString(State(state = "ON"))
            .toByteArray()
        client.publishWith()
            .topic(GMIATLICHT_TOPIC_SET)
            .payload(payload)
            .send()
    }
}


@Serializable
data class AquariumPowerStripState(
    val state_1: String? = null,
    val state_2: String? = null,
    val state_4: String? = null,
)

@Serializable
data class State(
    val state: String? = null,
)