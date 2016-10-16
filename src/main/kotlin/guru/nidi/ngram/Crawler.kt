package guru.nidi.ngram

interface Crawler {
    fun stop(): Unit
    fun crawl(consumer: (Crawler, String) -> Unit): Unit
}