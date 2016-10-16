package guru.nidi.ngram

import org.junit.Assert.assertEquals
import org.junit.Test

class NgramTest {
    @Test
    fun supportEmpty() {
        val ngram = Ngram(2)
        ngram.add("   ")
    }

    @Test
    fun simple1() {
        val ngram = Ngram(1)
        ngram.add("a ab abc  ")
        assertEquals(3, ngram.grams.size)
        assertEquals(3, ngram.grams["a"])
        assertEquals(2, ngram.grams["b"])
        assertEquals(1, ngram.grams["c"])
    }

    @Test
    fun simple2() {
        val ngram = Ngram(2)
        ngram.add("a ab abc  ")
        assertEquals(6, ngram.grams.size)
        assertEquals(3, ngram.grams[" a"])
        assertEquals(1, ngram.grams["a "])
        assertEquals(2, ngram.grams["ab"])
        assertEquals(1, ngram.grams["b "])
        assertEquals(1, ngram.grams["bc"])
        assertEquals(1, ngram.grams["c "])
    }

    @Test
    fun simple3() {
        val ngram = Ngram(3)
        ngram.add("a ab abc  ")
        assertEquals(5, ngram.grams.size)
        assertEquals(1, ngram.grams[" a "])
        assertEquals(2, ngram.grams[" ab"])
        assertEquals(1, ngram.grams["ab "])
        assertEquals(1, ngram.grams["abc"])
        assertEquals(1, ngram.grams["bc "])
    }

    @Test
    fun simple4() {
        val ngram = Ngram(4)
        ngram.add("a ab abc  ")
        assertEquals(3, ngram.grams.size)
        assertEquals(1, ngram.grams[" ab "])
        assertEquals(1, ngram.grams[" abc"])
        assertEquals(1, ngram.grams["abc "])
    }
}