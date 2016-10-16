package guru.nidi.ngram

import org.junit.Test

class WikipediaCrawlerTest {
    @Test
    fun init() {
        val stopCrawler = { crawler: Crawler, s: String -> crawler.stop() }
        WikipediaCrawler("de", "Spezial:Alle_Seiten").crawl(stopCrawler)
        WikipediaCrawler("en", "Special:AllPages").crawl(stopCrawler)
        WikipediaCrawler("fr", "Sp%C3%A9cial:Toutes_les_pages").crawl(stopCrawler)
        WikipediaCrawler("es", "Especial:Todas").crawl(stopCrawler)
        WikipediaCrawler("als", "Spezial:Alli_Syte").crawl(stopCrawler)
    }

    @Test
    fun bigram() {
        var pages = 0
        val ngram = Ngram(2)
        WikipediaCrawler("de", "Spezial:Alle_Seiten").crawl { crawler, s ->
            ngram.add(s)
            pages++
            if (pages == 10) crawler.stop()
        }
        println(ngram.grams)
    }
}