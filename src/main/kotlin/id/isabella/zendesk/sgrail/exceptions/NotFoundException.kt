package id.isabella.zendesk.sgrail.exceptions

import java.util.*

class NotFoundException(override val message: String): InputMismatchException(message) {
}