package id.isabella.zendesk.sgrail.services.route

import id.isabella.zendesk.sgrail.exceptions.NotFoundException
import id.isabella.zendesk.sgrail.model.RouteStepsStationAmountData
import id.isabella.zendesk.sgrail.services.RouteService
import org.junit.jupiter.api.*
import org.mockito.Mockito
import java.time.LocalDateTime

@DisplayName("Route service get route steps")
class GetRouteStepsTest {
    val routeService = RouteService()

    @Test
    fun `Throw not found exception when start station doesn't exist`() {
        assertThrows<NotFoundException>("`non existent` start station is not found") {
            routeService.getRouteSteps("non existent", "non existent")
        }
        LocalDateTime.now()
    }

    @Test
    fun `Throw not found exception when end station doesn't exist`() {
        assertThrows<NotFoundException>("`non existent` end station is not found") {
            routeService.getRouteSteps("Bugis", "non existent")
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Distance of start and end station just 1")
    inner class DistanceOfStartAndEndStationJust1() {
        private lateinit var routeStepsData: RouteStepsStationAmountData

        @BeforeAll
        fun triggerFunction() {
            routeStepsData = routeService.getRouteSteps("Kembangan", "Eunos")
        }

        @Test
        fun `Routes should be EW11`() {
            Assertions.assertEquals(listOf("EW6", "EW7"), routeStepsData.routes)
        }

        @Test
        fun `Amount of stations should be 1`() {
            Assertions.assertEquals(1, routeStepsData.amountOfStations)
        }

        @Test
        fun `Steps should be 1`() {
            Assertions.assertEquals(listOf("Take EW line from Kembangan to Eunos"), routeStepsData.steps)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("distance of start and end station just 2")
    inner class DistanceOfStartAndEndStationJust2() {
        private lateinit var routeStepsData: RouteStepsStationAmountData

        @BeforeAll
        fun triggerFunction() {
            routeStepsData = routeService.getRouteSteps("Braddell", "Ang Mo Kio")
        }

        @Test
        fun `Routes should be 3 codes when distance of start and end station just 2`() {
            Assertions.assertEquals(listOf("NS18", "NS17", "NS16"), routeStepsData.routes)
        }

        @Test
        fun `Amount of stations should be 2 when distance of start and end station just 2`() {
            Assertions.assertEquals(2, routeStepsData.amountOfStations)
        }

        @Test
        fun `Steps should be 2 when distance of start and end station just 2`() {
            Assertions.assertEquals(
                listOf(
                    "Take NS line from Braddell to Bishan",
                    "Take NS line from Bishan to Ang Mo Kio"
                ), routeStepsData.steps
            )
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("distance is 2 station with a transit in the middle")
    inner class DistanceIs2StationWithATransitInTheMiddle() {
        private lateinit var routeStepsData: RouteStepsStationAmountData

        @BeforeAll
        fun triggerFunction() {
            routeStepsData = routeService.getRouteSteps("Chinese Garden", "Bukit Batok")
        }

        @Test
        fun `Routes should be 4 codes`() {
            Assertions.assertEquals(listOf("EW25", "EW24", "NS1", "NS2"), routeStepsData.routes)
        }

        @Test
        fun `Amount of stations should be 3`() {
            Assertions.assertEquals(3, routeStepsData.amountOfStations)
        }

        @Test
        fun `Steps should be 3`() {
            Assertions.assertEquals(
                listOf(
                    "Take EW line from Chinese Garden to Jurong East",
                    "Change from EW line to NS line",
                    "Take NS line from Jurong East to Bukit Batok"
                ), routeStepsData.steps
            )
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Start station is a transit")
    inner class StartStationIsATransit() {
        private lateinit var routeStepsData: RouteStepsStationAmountData

        @BeforeAll
        fun triggerFunction() {
            routeStepsData = routeService.getRouteSteps("Jurong East", "Dover")
        }

        @Test
        fun `Routes should be 3 codes`() {
            Assertions.assertEquals(
                listOf(
                    "EW24", "EW23", "EW22"
                ), routeStepsData.routes
            )
        }

        @Test
        fun `Amount of stations should be 2`() {
            Assertions.assertEquals(2, routeStepsData.amountOfStations)
        }

        @Test
        fun `Steps should be 2`() {
            Assertions.assertEquals(
                listOf(
                    "Take EW line from Jurong East to Clementi",
                    "Take EW line from Clementi to Dover"
                ), routeStepsData.steps
            )
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("End station is a transit")
    inner class EndStationIsATransit() {
        private lateinit var routeStepsData: RouteStepsStationAmountData

        @BeforeAll
        fun triggerFunction() {
            routeStepsData = routeService.getRouteSteps("Hougang", "Serangoon")
        }

        @Test
        fun `Routes should be 3 codes`() {
            Assertions.assertEquals(
                listOf(
                    "NE14", "NE13", "NE12"
                ), routeStepsData.routes
            )
        }

        @Test
        fun `Amount of stations should be 2`() {
            Assertions.assertEquals(2, routeStepsData.amountOfStations)
        }

        @Test
        fun `Steps should be 2`() {
            Assertions.assertEquals(
                listOf(
                    "Take NE line from Hougang to Kovan",
                    "Take NE line from Kovan to Serangoon"
                ), routeStepsData.steps
            )
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("End station is a transit and there's a transit in the middle")
    inner class EndStationIsATransitAndTheresATransitInTheMiddle() {
        private lateinit var routeStepsData: RouteStepsStationAmountData

        @BeforeAll
        fun triggerFunction() {
            routeStepsData = routeService.getRouteSteps("Holland Village", "Bugis")
        }

        @Test
        fun `Routes should be 9 codes`() {
            Assertions.assertEquals(
                listOf(
                    "CC21", "CC20", "CC19", "DT9", "DT10", "DT11", "DT12", "DT13", "DT14"
                ), routeStepsData.routes
            )
        }

        @Test
        fun `Amount of stations should be 8`() {
            Assertions.assertEquals(8, routeStepsData.amountOfStations)
        }

        @Test
        fun `Steps should be 8`() {
            Assertions.assertEquals(
                listOf(
                    "Take CC line from Holland Village to Farrer Road",
                    "Take CC line from Farrer Road to Botanic Gardens",
                    "Change from CC line to DT line",
                    "Take DT line from Botanic Gardens to Stevens",
                    "Take DT line from Stevens to Newton",
                    "Take DT line from Newton to Little India",
                    "Take DT line from Little India to Rochor",
                    "Take DT line from Rochor to Bugis"
                ), routeStepsData.steps
            )
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("2 transit, start and end station is not transit")
    inner class TwoTransitStartAndEndStationIsNotTransit() {
        private lateinit var routeStepsData: RouteStepsStationAmountData

        @BeforeAll
        fun triggerFunction() {
            routeStepsData = routeService.getRouteSteps("King Albert Park", "Yio Chu Kang")
        }

        @Test
        fun `Routes should be 11 codes`() {
            Assertions.assertEquals(
                listOf(
                    "DT6", "DT7", "DT8", "DT9", "CC19", "CC17", "CC16", "CC15", "NS17", "NS16", "NS15"
                ), routeStepsData.routes
            )
        }

        @Test
        fun `Amount of stations should be 10`() {
            Assertions.assertEquals(10, routeStepsData.amountOfStations)
        }

        @Test
        fun `Steps should be 10`() {
            Assertions.assertEquals(
                listOf(
                    "Take DT line from King Albert Park to Sixth Avenue",
                    "Take DT line from Sixth Avenue to Tan Kah Kee",
                    "Take DT line from Tan Kah Kee to Botanic Gardens",
                    "Change from DT line to CC line",
                    "Take CC line from Botanic Gardens to Caldecott",
                    "Take CC line from Caldecott to Marymount",
                    "Take CC line from Marymount to Bishan",
                    "Change from CC line to NS line",
                    "Take NS line from Bishan to Ang Mo Kio",
                    "Take NS line from Ang Mo Kio to Yio Chu Kang"
                ), routeStepsData.steps
            )
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("TE hasn't open yet, so go through NS line")
    inner class TEHasntOpenYetSoGoThroughNSLine() {
        private lateinit var routeStepsData: RouteStepsStationAmountData

        @BeforeAll
        fun triggerFunction() {
            val mockedDateTimeValue = LocalDateTime.of(2020, 2, 3, 7, 0)
            Mockito.mockStatic(LocalDateTime::class.java).use { mockedLocalDateTime ->
                mockedLocalDateTime.`when`<Any> { LocalDateTime.now() }
                    .thenReturn(mockedDateTimeValue)
                routeStepsData = routeService.getRouteSteps("Woodlands", "Orchard")
            }
        }

        @Test
        fun `Routes should be 14 codes`() {
            Assertions.assertEquals(
                listOf(
                    "NS9",
                    "NS10",
                    "NS11",
                    "NS12",
                    "NS13",
                    "NS14",
                    "NS15",
                    "NS16",
                    "NS17",
                    "NS18",
                    "NS19",
                    "NS20",
                    "NS21",
                    "NS22",
                ), routeStepsData.routes
            )
        }

        @Test
        fun `Amount of stations should be 13`() {
            Assertions.assertEquals(13, routeStepsData.amountOfStations)
        }

        @Test
        fun `Steps should be 13`() {
            Assertions.assertEquals(
                listOf(
                    "Take NS line from Woodlands to Admiralty",
                    "Take NS line from Admiralty to Sembawang",
                    "Take NS line from Sembawang to Canberra",
                    "Take NS line from Canberra to Yishun",
                    "Take NS line from Yishun to Khatib",
                    "Take NS line from Khatib to Yio Chu Kang",
                    "Take NS line from Yio Chu Kang to Ang Mo Kio",
                    "Take NS line from Ang Mo Kio to Bishan",
                    "Take NS line from Bishan to Braddell",
                    "Take NS line from Braddell to Toa Payoh",
                    "Take NS line from Toa Payoh to Novena",
                    "Take NS line from Novena to Newton",
                    "Take NS line from Newton to Orchard",
                ), routeStepsData.steps
            )
        }

        @TestInstance(TestInstance.Lifecycle.PER_CLASS)
        @Nested
        @DisplayName("TE has open in the future, so go through TE line")
        inner class TEHasOpenInTheFutureSoGoThroughTELine() {
            private lateinit var routeStepsData: RouteStepsStationAmountData

            @BeforeAll
            fun triggerFunction() {
                val mockedDateTimeValue = LocalDateTime.of(2022, 2, 3, 7, 0)
                Mockito.mockStatic(LocalDateTime::class.java).use { mockedLocalDateTime ->
                    mockedLocalDateTime.`when`<Any> { LocalDateTime.now() }
                        .thenReturn(mockedDateTimeValue)
                    routeStepsData = routeService.getRouteSteps("Woodlands", "Orchard")
                }
            }

            @Test
            fun `Routes should be 13 codes`() {
                Assertions.assertEquals(
                    listOf(
                        "TE2",
                        "TE3",
                        "TE4",
                        "TE5",
                        "TE6",
                        "TE7",
                        "TE8",
                        "TE9",
                        "TE10",
                        "TE11",
                        "TE12",
                        "TE13",
                        "TE14",
                    ), routeStepsData.routes
                )
            }

            @Test
            fun `Amount of stations should be 12`() {
                Assertions.assertEquals(12, routeStepsData.amountOfStations)
            }

            @Test
            fun `Steps should be 12`() {
                Assertions.assertEquals(
                    listOf(
                        "Take TE line from Woodlands to Woodlands South",
                        "Take TE line from Woodlands South to Springleaf",
                        "Take TE line from Springleaf to Lentor",
                        "Take TE line from Lentor to Mayflower",
                        "Take TE line from Mayflower to Bright Hill",
                        "Take TE line from Bright Hill to Upper Thomson",
                        "Take TE line from Upper Thomson to Caldecott",
                        "Take TE line from Caldecott to Mount Pleasant",
                        "Take TE line from Mount Pleasant to Stevens",
                        "Take TE line from Stevens to Napier",
                        "Take TE line from Napier to Orchard Boulevard",
                        "Take TE line from Orchard Boulevard to Orchard",
                    ), routeStepsData.steps
                )
            }
        }
    }
}