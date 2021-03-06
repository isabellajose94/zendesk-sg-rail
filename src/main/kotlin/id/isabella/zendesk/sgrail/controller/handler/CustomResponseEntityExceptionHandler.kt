package id.isabella.zendesk.sgrail.controller.handler

import id.isabella.zendesk.sgrail.exceptions.BadInputException
import id.isabella.zendesk.sgrail.exceptions.NotFoundException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class CustomResponseEntityExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(
        value = [
            NotFoundException::class,
            BadInputException::class,
            MethodArgumentTypeMismatchException::class,
            Exception::class
        ]
    )
    final fun handleExceptionCustom(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        logger.debug(ex)
        var status = HttpStatus.INTERNAL_SERVER_ERROR
        var message = ex.message.toString()
        if (ex is NotFoundException)
            status = HttpStatus.NOT_FOUND
        if (ex is BadInputException)
            status = HttpStatus.BAD_REQUEST
        if (ex is MethodArgumentTypeMismatchException){
            status = HttpStatus.BAD_REQUEST
            val startTime = request.getParameter("startTime")
            if (startTime != null && startTime.isNotEmpty()) {
                message = "`${startTime}` is wrong format, should be `yyyy-MM-dd'T'hh:mm`"
            }
        }

        return handleExceptionInternal(ex, mapOf("errorMessage" to message), HttpHeaders(), status, request)

    }
}