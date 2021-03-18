package id.isabella.zendesk.sgrail.model

data class BestRoute(val startCode: String, val endCode: String, val transits: List<Pair<String, List<String>>>)
