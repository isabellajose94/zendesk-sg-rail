package id.isabella.zendesk.sgrail.controller

import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Route Controller")
class RouteControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Nested
    @DisplayName("Get route")
    inner class GetRoute() {

        @Nested
        @DisplayName("default")
        inner class Default() {
            @Test
            fun shouldReturnBadRequestIfStartStationIsEmpty() {
                mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/routes/nonExistentStart/nonExistentEnd")
                )
                    .andExpect(MockMvcResultMatchers.status().isNotFound)
                    .andExpect(
                        MockMvcResultMatchers.jsonPath(
                            "$.errorMessage",
                            `is`("`nonExistentStart` start station is not found")
                        )
                    )
            }

            @Test
            fun shouldReturnBadRequestIfEndStationIsEmpty() {
                mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/routes/Bugis/nonExistentEnd")
                )
                    .andExpect(MockMvcResultMatchers.status().isNotFound)
                    .andExpect(
                        MockMvcResultMatchers.jsonPath(
                            "$.errorMessage",
                            `is`("`nonExistentEnd` end station is not found")
                        )
                    )
            }

            @Test
            fun shouldReturnOKIfStartAndEndExist() {
                mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/routes/Sengkang/Bras Basah")
                )
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andExpect(
                        MockMvcResultMatchers.jsonPath(
                            "$.amountOfStations",
                            `is`(12)
                        )
                    ).andExpect(
                        MockMvcResultMatchers.jsonPath(
                            "$.routes",
                            `is`(
                                listOf(
                                    ("NE16"),
                                    ("NE15"),
                                    ("NE14"),
                                    ("NE13"),
                                    ("NE12"),
                                    ("NE11"),
                                    ("NE10"),
                                    ("NE9"),
                                    ("NE8"),
                                    ("NE7"),
                                    ("NE6"),
                                    ("CC1"),
                                    ("CC2")
                                )
                            )
                        )
                    ).andExpect(
                        MockMvcResultMatchers.jsonPath(
                            "$.steps",
                            `is`(
                                listOf(
                                    ("Take NE line from Sengkang to Buangkok"),
                                    ("Take NE line from Buangkok to Hougang"),
                                    ("Take NE line from Hougang to Kovan"),
                                    ("Take NE line from Kovan to Serangoon"),
                                    ("Take NE line from Serangoon to Woodleigh"),
                                    ("Take NE line from Woodleigh to Potong Pasir"),
                                    ("Take NE line from Potong Pasir to Boon Keng"),
                                    ("Take NE line from Boon Keng to Farrer Park"),
                                    ("Take NE line from Farrer Park to Little India"),
                                    ("Take NE line from Little India to Dhoby Ghaut"),
                                    ("Change from NE line to CC line"),
                                    ("Take CC line from Dhoby Ghaut to Bras Basah")
                                )
                            )
                        )
                    )
            }
        }

        @Nested
        @DisplayName("With time")
        inner class WithTime() {
            @Test
            fun shouldReturnBadRequestIfStartTimeHasWrongFormat() {
                mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/routes/Bedok North/MacPherson")
                        .queryParam("startTime", "2019-01-31T23")
                )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest)
                    .andExpect(
                        MockMvcResultMatchers.jsonPath(
                            "$.errorMessage",
                            `is`("`2019-01-31T23` is wrong format, should be `yyyy-MM-dd'T'hh:mm`")
                        )
                    )
            }

            @Test
            fun shouldReturnBadRequestIfStationIsClosed() {
                mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/routes/Bedok North/MacPherson")
                        .queryParam("startTime", "2019-01-31T23:00")
                )
                    .andExpect(MockMvcResultMatchers.status().isBadRequest)
                    .andExpect(
                        MockMvcResultMatchers.jsonPath(
                            "$.errorMessage",
                            `is`("`Bedok North` is close")
                        )
                    )
            }

            @Test
            fun shouldReturnOKIfStartAndEndIsOpen() {
                mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/routes/Boon Lay/Little India")
                        .queryParam("startTime", "2021-03-22T08:00")
                )
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andExpect(
                        MockMvcResultMatchers.jsonPath(
                            "$.durationTime",
                            `is`(150)
                        )
                    ).andExpect(
                        MockMvcResultMatchers.jsonPath(
                            "$.routes",
                            `is`(
                                listOf(
                                    "EW27",
                                    "EW26",
                                    "EW25",
                                    "EW24",
                                    "EW23",
                                    "EW22",
                                    "EW21",
                                    "CC22",
                                    "CC21",
                                    "CC20",
                                    "CC19",
                                    "DT9",
                                    "DT10",
                                    "DT11",
                                    "DT12"
                                )
                            )
                        )
                    ).andExpect(
                        MockMvcResultMatchers.jsonPath(
                            "$.steps",
                            `is`(
                                listOf(
                                    "Take EW line from Boon Lay to Lakeside",
                                    "Take EW line from Lakeside to Chinese Garden",
                                    "Take EW line from Chinese Garden to Jurong East",
                                    "Take EW line from Jurong East to Clementi",
                                    "Take EW line from Clementi to Dover",
                                    "Take EW line from Dover to Buona Vista",
                                    "Change from EW line to CC line",
                                    "Take CC line from Buona Vista to Holland Village",
                                    "Take CC line from Holland Village to Farrer Road",
                                    "Take CC line from Farrer Road to Botanic Gardens",
                                    "Change from CC line to DT line",
                                    "Take DT line from Botanic Gardens to Stevens",
                                    "Take DT line from Stevens to Newton",
                                    "Take DT line from Newton to Little India"
                                )
                            )
                        )
                    )
            }
        }
    }
}