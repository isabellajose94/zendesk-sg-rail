package id.isabella.zendesk.sgrail.controller

import id.isabella.zendesk.sgrail.model.RouteStepsData
import id.isabella.zendesk.sgrail.services.RouteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RestController
@RequestMapping("api/routes")
class RouteController {

    @Autowired
    private lateinit var routeService: RouteService

    @GetMapping("/{startStation}/{endStation}")
    fun getUserStatus(
        @PathVariable("startStation") startStation: String,
        @PathVariable("endStation") endStation: String,
        @RequestParam(required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'H:mm")
        startTime: LocalDateTime?
    ): ResponseEntity<Any> {
        var result: RouteStepsData = if (startTime != null)
            routeService.getRouteSteps(startStation, endStation, startTime)
        else
            routeService.getRouteSteps(startStation, endStation)
        return ResponseEntity.ok(result)
    }
}