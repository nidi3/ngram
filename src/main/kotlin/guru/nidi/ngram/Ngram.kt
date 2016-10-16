package guru.nidi.ngram

import java.util.*

class Ngram(val n: Int) {
    val grams = HashMap<String, Int>()

    fun add(s: String) {
        for (part in s.split(" ")) {
            if (part.length > 0 && part.length + 2 >= n) {
                val p = if (n == 1) part else " $part "
                for (i in 0..p.length - n) {
                    inc(p.substring(i, i + n))
                }
            }
        }
    }

    private fun inc(s: String) {
        val count = grams[s]
        grams[s] = if (count == null) 1 else count + 1
    }
}