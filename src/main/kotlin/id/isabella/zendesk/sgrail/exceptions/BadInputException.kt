package id.isabella.zendesk.sgrail.exceptions

import java.util.*

class BadInputException (override val message: String): InputMismatchException(message) {
}