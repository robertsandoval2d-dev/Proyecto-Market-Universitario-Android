package com.example.marketuniversitario.feature.home.domain.usecases

import com.example.marketuniversitario.feature.business.domain.models.ProductCategory
import com.example.marketuniversitario.feature.home.domain.models.FeedProduct
import java.text.Normalizer
import javax.inject.Inject
import kotlin.math.abs

class SearchProductsUseCase @Inject constructor() {

    private class Indexed(
        val product: FeedProduct,
        val name: List<String>,
        val tags: List<String>,
        val keywords: List<String>,
        val category: List<String>
    )

    private class Scored(val product: FeedProduct, val matched: Int, val score: Double)

    // Índice cacheado: se reconstruye solo cuando llega una lista nueva
    private var cachedSource: List<FeedProduct>? = null
    private var cachedIndex: List<Indexed> = emptyList()

    @Synchronized
    private fun indexFor(products: List<FeedProduct>): List<Indexed> {
        if (cachedSource !== products) {
            cachedIndex = products.map { p ->
                Indexed(
                    product = p,
                    name = words(p.name),
                    tags = p.allTags.flatMap(::words).distinct(),
                    keywords = p.searchKeywords.flatMap(::words).distinct(),
                    category = words(ProductCategory.fromId(p.category).displayName)
                )
            }
            cachedSource = products
        }
        return cachedIndex
    }

    operator fun invoke(products: List<FeedProduct>, query: String): List<FeedProduct> {
        val tokens = words(query)
        if (tokens.isEmpty()) return products

        // Sinónimos: cuántas palabras de la búsqueda los generan (más = más peso)
        val synonyms = tokens
            .flatMap { SYNONYMS[it].orEmpty() }
            .map(::stem)
            .filter { it !in tokens }
            .groupingBy { it }
            .eachCount()

        return indexFor(products)
            .mapNotNull { item ->
                var matched = 0
                var score = 0.0
                for (t in tokens) {
                    val s = termScore(t, item, fuzzy = true)
                    if (s > 0) { matched++; score += s }
                }
                for ((t, count) in synonyms) {
                    score += termScore(t, item, fuzzy = false) * SYNONYM_FACTOR * count
                }
                if (score > 0) Scored(item.product, matched, score) else null
            }
            // Más palabras coincidentes primero; luego puntaje. El orden es estable:
            // los empates conservan el orden personalizado del feed.
            .sortedWith(compareByDescending<Scored> { it.matched }.thenByDescending { it.score })
            .map { it.product }
    }

    // ------------------------------------------------------------ Puntuación

    /** Mejor coincidencia de la palabra en cada campo, ponderada por la importancia del campo. */
    private fun termScore(term: String, item: Indexed, fuzzy: Boolean): Double = maxOf(
        W_NAME * fieldStrength(term, item.name, fuzzy),
        W_TAG * fieldStrength(term, item.tags, fuzzy),
        W_CATEGORY * fieldStrength(term, item.category, fuzzy),
        W_KEYWORD * fieldStrength(term, item.keywords, fuzzy)
    )

    private fun fieldStrength(term: String, words: List<String>, fuzzy: Boolean): Double {
        var best = 0.0
        for (w in words) {
            val s = wordMatch(term, w, fuzzy)
            if (s > best) {
                best = s
                if (best == 1.0) break
            }
        }
        return best
    }

    /** 1.0 exacta · 0.85 prefijo · 0.8 singular/plural · 0.6 contiene · 0.5 con typo. */
    private fun wordMatch(q: String, w: String, fuzzy: Boolean): Double = when {
        w == q -> 1.0
        q.length >= 3 && w.startsWith(q) -> 0.85
        w.length >= 4 && q.startsWith(w) && q.length - w.length <= 2 -> 0.8
        q.length >= 4 && w.contains(q) -> 0.6
        fuzzy && q.length >= 5 && editDistance(q, w, maxEdits(q.length)) <= maxEdits(q.length) -> 0.5
        else -> 0.0
    }

    private fun maxEdits(length: Int) = if (length >= 9) 2 else 1

    /** Distancia de edición con transposiciones adyacentes ("casca" ↔ "cacsa"). */
    private fun editDistance(a: String, b: String, max: Int): Int {
        if (abs(a.length - b.length) > max) return max + 1
        val d = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) d[i][0] = i
        for (j in 0..b.length) d[0][j] = j
        for (i in 1..a.length) {
            for (j in 1..b.length) {
                val cost = if (a[i - 1] == b[j - 1]) 0 else 1
                var v = minOf(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + cost)
                if (i > 1 && j > 1 && a[i - 1] == b[j - 2] && a[i - 2] == b[j - 1]) {
                    v = minOf(v, d[i - 2][j - 2] + 1)
                }
                d[i][j] = v
            }
        }
        return d[a.length][b.length]
    }

    // ------------------------------------------------------------ Texto

    private fun words(text: String): List<String> =
        normalize(text)
            .split(NON_ALNUM)
            .filter { it.length >= 2 && it !in STOPWORDS }
            .map(::stem)
            .distinct()

    /** Quita la "s" final: "casacas" -> "casaca". El resto lo cubre wordMatch. */
    private fun stem(word: String): String =
        if (word.length > 3 && word.endsWith("s")) word.dropLast(1) else word

    private fun normalize(text: String): String =
        Normalizer.normalize(text.lowercase(), Normalizer.Form.NFD)
            .replace(DIACRITICS, "")

    private companion object {
        const val W_NAME = 10.0
        const val W_TAG = 8.0
        const val W_CATEGORY = 5.0
        const val W_KEYWORD = 4.0
        const val SYNONYM_FACTOR = 0.4

        val NON_ALNUM = Regex("[^a-z0-9]+")
        val DIACRITICS = Regex("\\p{InCombiningDiacriticalMarks}+")

        val STOPWORDS = setOf("de", "la", "el", "los", "las", "un", "una", "para", "con", "y", "en", "por", "del")

        // Claves y valores sin tildes ni "s" final. Amplía según lo que busquen tus usuarios.
        val SYNONYMS = mapOf(
            "abrigo" to setOf("casaca", "buzo"),
            "frio" to setOf("casaca", "buzo"),
            "chompa" to setOf("casaca", "buzo"),
            "ropa" to setOf("polo", "casaca", "buzo", "zapatilla", "gorra"),
            "comer" to setOf("menu", "almuerzo", "snack", "desayuno"),
            "comida" to setOf("menu", "almuerzo", "snack", "postre"),
            "celular" to setOf("cargador", "audifono"),
            "computadora" to setOf("laptop", "pc"),
            "estudiar" to setOf("separata", "resumen", "examen", "libro"),
            "cuarto" to setOf("alquiler", "roommate", "colchon")
        )
    }
}