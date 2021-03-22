package id.isabella.zendesk.sgrail.services.route

import id.isabella.zendesk.sgrail.exceptions.BadInputException
import id.isabella.zendesk.sgrail.exceptions.NotFoundException
import id.isabella.zendesk.sgrail.model.RouteStepsStationAmountData
import id.isabella.zendesk.sgrail.model.RouteStepsTimeDurationData
import id.isabella.zendesk.sgrail.services.RouteService
import org.junit.jupiter.api.*
import java.time.LocalDateTime
import java.util.*

@DisplayName("Route service get route steps with time constraint test")
class GetRouteStepsWithTimeConstraintTest {

    val routeService = RouteService()

    @Test
    fun `Throw not found exception when start station doesn't exist`() {
        assertThrows<NotFoundException>("`non existent` start station is not found") {
            routeService.getRouteSteps("non existent", "non existent", LocalDateTime.of(2021, 3, 3, 0, 0, 0))
        }
    }

    @Test
    fun `Throw not found exception when end station doesn't exist`() {
        assertThrows<NotFoundException>("`non existent` end station is not found") {
            routeService.getRouteSteps("Bugis", "non existent", LocalDateTime.of(2021, 3, 3, 0, 0, 0))
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Distance 1 station on NE Line on non-peak hours")
    inner class Distance1StationOnNELineOnNonPeakHours() {
        private lateinit var routeStepsData: RouteStepsTimeDurationData

        @BeforeAll
        fun triggerFunction() {
            routeStepsData =
                routeService.getRouteSteps("Woodleigh", "Potong Pasir", LocalDateTime.of(2021, 3, 6, 10, 0, 0))
        }

        @Test
        fun `Routes should be EW11`() {
            Assertions.assertEquals(listOf("NE11", "NE10"), routeStepsData.routes)
        }

        @Test
        fun `Duration should be 10 minutes`() {
            Assertions.assertEquals(10, routeStepsData.durationTime)
        }

        @Test
        fun `Steps should be 1`() {
            Assertions.assertEquals(listOf("Take NE line from Woodleigh to Potong Pasir"), routeStepsData.steps)
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Distance 2 station on DT Line on non-peak hours")
    inner class Distance2StationOnDTLineOnNonPeakHours() {
        private lateinit var routeStepsData: RouteStepsTimeDurationData

        @BeforeAll
        fun triggerFunction() {
            routeStepsData = routeService.getRouteSteps(
                "Sixth Avenue",
                "Botanic Gardens",
                LocalDateTime.of(2021, 3, 6, 10, 0, 0)
            )
        }

        @Test
        fun `Routes should be 3 codes`() {
            Assertions.assertEquals(listOf("DT7", "DT8", "DT9"), routeStepsData.routes)
        }

        @Test
        fun `Duration should be 16 Minutes (8 minutes per station)`() {
            Assertions.assertEquals(16, routeStepsData.durationTime)
        }

        @Test
        fun `Steps should be 2`() {
            Assertions.assertEquals(
                listOf(
                    "Take DT line from Sixth Avenue to Tan Kah Kee",
                    "Take DT line from Tan Kah Kee to Botanic Gardens"
                ), routeStepsData.steps
            )

        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Distance 4 station on TE Line on non-peak hours")
    inner class Distance4StationOnTELineOnNonPeakHours() {
        private lateinit var routeStepsData: RouteStepsTimeDurationData

        @BeforeAll
        fun triggerFunction() {
            routeStepsData = routeService.getRouteSteps(
                "Woodlands",
                "Mayflower",
                LocalDateTime.of(2021, 3, 6, 10, 0, 0)
            )
        }

        @Test
        fun `Routes should be 5 codes`() {
            Assertions.assertEquals(listOf("TE2", "TE3", "TE4", "TE5", "TE6"), routeStepsData.routes)
        }

        @Test
        fun `Duration should be 32 Minutes (8 minutes per station)`() {
            Assertions.assertEquals(32, routeStepsData.durationTime)
        }

        @Test
        fun `Steps should be 2`() {
            Assertions.assertEquals(
                listOf(
                    "Take TE line from Woodlands to Woodlands South",
                    "Take TE line from Woodlands South to Springleaf",
                    "Take TE line from Springleaf to Lentor",
                    "Take TE line from Lentor to Mayflower",
                ), routeStepsData.steps
            )

        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Distance 11 Station with a transit on non-peak hours")
    inner class Distance11StationWithATransitOnNonPeakHours() {
        private lateinit var routeStepsData: RouteStepsTimeDurationData

        @BeforeAll
        fun triggerFunction() {
            routeStepsData = routeService.getRouteSteps(
                "Botanic Gardens",
                "Marina Bay",
                LocalDateTime.of(2021, 3, 6, 10, 0, 0)
            )
        }

        @Test
        fun `Routes should be 9 codes`() {
            Assertions.assertEquals(
                listOf(
                    "DT9",
                    "DT10",
                    "DT11",
                    "DT12",
                    "DT13",
                    "DT14",
                    "DT15",
                    "DT16",
                    "CE1",
                    "CE2",
                ), routeStepsData.routes
            )
        }

        @Test
        fun `Duration should be 76 Minutes`() {
            /*
            DT9 -> DT10 (8)
            DT10 -> DT11 (8)
            DT11 -> DT12 (8)
            DT12 -> DT13 (8)
            DT13 -> DT14 (8)
            DT14 -> DT15 (8)
            DT15 -> DT16 (8)
            DT16 -> CE1 (10) - Transit
            CE1 -> CE2 (10)
            */
            Assertions.assertEquals(76, routeStepsData.durationTime)
        }

        @Test
        fun `Steps should be 8`() {
            Assertions.assertEquals(
                listOf(
                    "Take DT line from Botanic Gardens to Stevens",
                    "Take DT line from Stevens to Newton",
                    "Take DT line from Newton to Little India",
                    "Take DT line from Little India to Rochor",
                    "Take DT line from Rochor to Bugis",
                    "Take DT line from Bugis to Promenade",
                    "Take DT line from Promenade to Bayfront",
                    "Change from DT line to CE line",
                    "Take CE line from Bayfront to Marina Bay"
                ), routeStepsData.steps
            )

        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Through NE, CC and NS line with two transit on morning peak hours")
    inner class ThroughNECCAndNSLineWithTwoTransitOnMorningPeakHours() {
        private lateinit var routeStepsData: RouteStepsTimeDurationData

        @BeforeAll
        fun triggerFunction() {
            routeStepsData = routeService.getRouteSteps(
                "Hougang",
                "Yio Chu Kang",
                LocalDateTime.of(2021, 3, 5, 8, 0, 0)
            )
        }

        @Test
        fun `Routes should be 9 codes`() {
            Assertions.assertEquals(
                listOf(
                    "NE14",
                    "NE13",
                    "NE12",
                    "CC13",
                    "CC14",
                    "CC15",
                    "NS17",
                    "NS16",
                    "NS15",
                ), routeStepsData.routes
            )
        }

        @Test
        fun `Duration should be 98 Minutes`() {
            /*
            NE14 -> NE13 (12)
            NE13 -> NE12 (12)
            NE12 -> CC13 (15) / Transit
            CC13 -> CC14 (10)
            CC14 -> CC15 (10)
            CC15 -> NS17 (15) / Transit
            NS17 -> NS16 (12)
            NS16 -> NS15 (12)
            */
            Assertions.assertEquals(98, routeStepsData.durationTime)
        }

        @Test
        fun `Steps should be 8`() {
            Assertions.assertEquals(
                listOf(
                    "Take NE line from Hougang to Kovan",
                    "Take NE line from Kovan to Serangoon",
                    "Change from NE line to CC line",
                    "Take CC line from Serangoon to Lorong Chuan",
                    "Take CC line from Lorong Chuan to Bishan",
                    "Change from CC line to NS line",
                    "Take NS line from Bishan to Ang Mo Kio",
                    "Take NS line from Ang Mo Kio to Yio Chu Kang"
                ), routeStepsData.steps
            )

        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Through NS and EW line with two transit on evening peak hours")
    inner class ThroughNSAndEWLineWithTwoTransitOnEveningPeakHours() {
        private lateinit var routeStepsData: RouteStepsTimeDurationData

        @BeforeAll
        fun triggerFunction() {
            routeStepsData = routeService.getRouteSteps(
                "Bukit Gombak",
                "Tanjong Pagar",
                LocalDateTime.of(2021, 3, 1, 21, 0, 0)
            )
        }

        @Test
        fun `Routes should be 12 codes`() {
            Assertions.assertEquals(
                listOf(
                    "NS3",
                    "NS2",
                    "NS1",
                    "EW24",
                    "EW23",
                    "EW22",
                    "EW21",
                    "EW20",
                    "EW19",
                    "EW18",
                    "EW17",
                    "EW16",
                    "EW15",
                ), routeStepsData.routes
            )
        }

        @Test
        fun `Duration should be 129 Minutes`() {
            /*
            NS3 -> NS2 (12)
            NS2 -> NS1 (12)
            NE1 -> EW24 (15) / Transit
            EW24 -> EW23 (10)
            EW23 -> EW22 (10)
            EW22 -> EW21 (10)
            EW21 -> EW20 (10)
            EW20 -> EW19 (10)
            EW19 -> EW18 (10)
            EW18 -> EW17 (10)
            EW17 -> EW16 (10)
            EW16 -> NS15 (10)
            */
            Assertions.assertEquals(129, routeStepsData.durationTime)
        }

        @Test
        fun `Steps should be 8`() {
            Assertions.assertEquals(
                listOf(
                    "Take NS line from Bukit Gombak to Bukit Batok",
                    "Take NS line from Bukit Batok to Jurong East",
                    "Change from NS line to EW line",
                    "Take EW line from Jurong East to Clementi",
                    "Take EW line from Clementi to Dover",
                    "Take EW line from Dover to Buona Vista",
                    "Take EW line from Buona Vista to Commonwealth",
                    "Take EW line from Commonwealth to Queenstown",
                    "Take EW line from Queenstown to Redhill",
                    "Take EW line from Redhill to Tiong Bahru",
                    "Take EW line from Tiong Bahru to Outram Park",
                    "Take EW line from Outram Park to Tanjong Pagar",
                ), routeStepsData.steps
            )

        }
    }

    @Test
    fun `Throw bad request when start station in CG line is closed`() {
        assertThrows<BadInputException>("`Changi Airport` is close") {
            routeService.getRouteSteps("Changi Airport", "Tanjong Pagar", LocalDateTime.of(2021, 3, 1, 22, 0, 0))
        }
    }

    @Test
    fun `Throw bad request when end station in CG line is closed`() {
        assertThrows<BadInputException>("`Changi Airport` is close") {
            routeService.getRouteSteps("Tanjong Pagar", "Changi Airport", LocalDateTime.of(2021, 3, 1, 2, 0, 0))
        }
    }

    @Test
    fun `Throw bad request when start station in DT line is closed`() {
        assertThrows<BadInputException>("`Jalan Besar` is close") {
            routeService.getRouteSteps("Jalan Besar", "Tanjong Pagar", LocalDateTime.of(2021, 3, 1, 23, 0, 0))
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Through CC and NE line with a transit on night hours")
    inner class ThroughCCAndNELineWithATransitOnNightHours() {
        private lateinit var routeStepsData: RouteStepsTimeDurationData

        @BeforeAll
        fun triggerFunction() {
            routeStepsData = routeService.getRouteSteps(
                "Botanic Gardens",
                "Marina Bay",
                LocalDateTime.of(2021, 3, 1, 6, 0, 0)
            )
        }

        @Test
        fun `Routes should be 12 codes`() {
            Assertions.assertEquals(
                listOf(
                    "CC19",
                    "CC20",
                    "CC21",
                    "CC22",
                    "EW21",
                    "EW20",
                    "EW19",
                    "EW18",
                    "EW17",
                    "EW16",
                    "EW15",
                    "EW14",
                    "NS26",
                    "NS27"
                ), routeStepsData.routes
            )
        }

        @Test
        fun `Duration should be 130 Minutes`() {
            /*
            CC19 -> CC20 (10)
            CC20 -> CC21 (10)
            CC21 -> CC22 (10)
            CC22 -> EW21 (10)
            EW21 -> EW20 (10)
            EW20 -> EW19 (10)
            EW19 -> EW18 (10)
            EW18 -> EW17 (10)
            EW17 -> EW16 (10)
            EW16 -> EW15 (10)
            EW15 -> EW14 (10)
            EW14 -> NS26 (10)
            NS26 -> NS27 (10)
            */
            Assertions.assertEquals(130, routeStepsData.durationTime)
        }

        @Test
        fun `Steps should be 8`() {
            Assertions.assertEquals(
                listOf(
                    "Take CC line from Botanic Gardens to Farrer Road",
                    "Take CC line from Farrer Road to Holland Village",
                    "Take CC line from Holland Village to Buona Vista",
                    "Change from CC line to EW line",
                    "Take EW line from Buona Vista to Commonwealth",
                    "Take EW line from Commonwealth to Queenstown",
                    "Take EW line from Queenstown to Redhill",
                    "Take EW line from Redhill to Tiong Bahru",
                    "Take EW line from Tiong Bahru to Outram Park",
                    "Take EW line from Outram Park to Tanjong Pagar",
                    "Take EW line from Tanjong Pagar to Raffles Place",
                    "Change from EW line to NS line",
                    "Take NS line from Raffles Place to Marina Bay",
                ), routeStepsData.steps
            )
        }
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Nested
    @DisplayName("Woodlands North (TE) to Caldecott (TE, CC)on night hours")
    inner class WoodlandsNorthToBishanOnNightHours() {
        private lateinit var routeStepsData: RouteStepsTimeDurationData

        @BeforeAll
        fun triggerFunction() {
            routeStepsData = routeService.getRouteSteps(
                "Woodlands North",
                "Caldecott",
                LocalDateTime.of(2021, 3, 1, 6, 0, 0)
            )
        }

        @Test
        fun `Routes should be 9 codes`() {
            Assertions.assertEquals(
                listOf(
                    "TE1",
                    "TE2",
                    "TE3",
                    "TE4",
                    "TE5",
                    "TE6",
                    "TE7",
                    "TE8",
                    "TE9"
                ), routeStepsData.routes
            )
        }

        @Test
        fun `Duration should be 98 Minutes`() {
            /*
            TE1 -> TE2 (8)
            TE2 -> TE3 (8)
            TE3 -> TE4 (8)
            TE4 -> TE5 (8)
            TE5 -> TE6 (8)
            TE6 -> TE7 (8)
            TE7 -> TE8 (8)
            TE8 -> TE9 (8)
            */
            Assertions.assertEquals(64, routeStepsData.durationTime)
        }

        @Test
        fun `Steps should be 8`() {
            Assertions.assertEquals(
                listOf(
                    "Take TE line from Woodlands North to Woodlands",
                    "Take TE line from Woodlands to Woodlands South",
                    "Take TE line from Woodlands South to Springleaf",
                    "Take TE line from Springleaf to Lentor",
                    "Take TE line from Lentor to Mayflower",
                    "Take TE line from Mayflower to Bright Hill",
                    "Take TE line from Bright Hill to Upper Thomson",
                    "Take TE line from Upper Thomson to Caldecott",
                ), routeStepsData.steps
            )
        }
    }
}