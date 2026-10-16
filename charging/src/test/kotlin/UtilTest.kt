import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UtilTest {

    @Test
    fun almostEquals() {
        assertThat(42.almostEquals(43, 5))
        assertThat(7545.almostEquals(7407, 250))
        assertThat(7545.almostEquals(7407, 40)).isFalse
    }
}