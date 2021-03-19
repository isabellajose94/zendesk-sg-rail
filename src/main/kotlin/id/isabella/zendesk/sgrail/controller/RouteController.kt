package id.isabella.zendesk.sgrail.controller

import id.isabella.zendesk.sgrail.services.RouteService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/routes")
class RouteController {

    @Autowired
    private lateinit var routeService: RouteService

    @GetMapping("/{startStation}/{endStation}")
    fun getUserStatus(
        @PathVariable("startStation") startStation: String,
        @PathVariable("endStation") endStation: String
    ): ResponseEntity<Any> {
        return ResponseEntity.ok(routeService.getRouteSteps(startStation, endStation))
    }
}