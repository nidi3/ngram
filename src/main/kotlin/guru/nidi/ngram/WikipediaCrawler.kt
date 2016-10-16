package guru.nidi.ngram

import org.apache.commons.lang3.StringEscapeUtils
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import org.slf4j.LoggerFactory
import java.lang.Character.isLetter
import java.util.regex.Matcher
import java.util.regex.Pattern

class WikipediaCrawler(lang: String, val allPagesPath: String) : Crawler {
    private val log = LoggerFactory.getLogger(WikipediaCrawler::class.java)
    private val client = HttpClientBuilder.create().build()
    private var stopped = false
    private val baseUrl = "https://$lang.wikipedia.org"

    override fun stop() {
        stopped = true
    }

    override fun crawl(consumer: (Crawler, String) -> Unit) {
        val links = Pattern.compile("""<div class="mw-allpages-nav">(\s*<a .*?</a>)?\s*<a .*?href="(.*?)"""")
        var content = get("/wiki/$allPagesPath")
        while (!stopped) {
            crawlPages(content, consumer)
            val link = find(content, links)
            content = get(link.group(2))
        }
    }

    private fun crawlPages(list: String, consumer: (Crawler, String) -> Unit) {
        val start = list.indexOf("mw-allpages-body")
        val end = list.indexOf("mw-allpages-nav", start + 1)
        val matcher = find(list.substring(start, end), Pattern.compile("""<a [^>]*href="(.*?)"[^>]*>"""))
        do {
            val page = get(matcher.group(1))
            consumer.invoke(this, cleanup(page))
        } while (!stopped && matcher.find())
    }

    private fun get(url: String): String {
        log.info("Fetching $url")
        val req = HttpGet(baseUrl + url)
        val res = client.execute(req)
        if (res.statusLine.statusCode != 200) {
            throw CrawlerException("Error getting ${url}: Response code: ${res.statusLine.statusCode}")
        }
        return EntityUtils.toString(res.entity)
    }

    private fun find(content: String, pattern: Pattern): Matcher {
        val matcher = pattern.matcher(content)
        if (!matcher.find()) {
            throw CrawlerException("Pattern $pattern not found")
        }
        return matcher
    }

    private fun cleanup(s: String): String {
        val res = StringBuilder()
        val start = s.indexOf("""<div id="mw-content-text"""")
        val end = s.indexOf("""<div id="mw-navigation"""")
        var text = true
        var index = start
        do {
            if (text) {
                val next = s.indexOf("<", index)
                val text = removeNonLetters(removeLinks(StringEscapeUtils.unescapeHtml4(s.substring(index, next).toLowerCase().trim())))
                res.append(text + " ")
                index = next
            } else {
                index = s.indexOf(">", index) + 1
            }
            text = !text
        } while (index < end)
        return res.toString().replace(Regex("\\s\\s+"), " ")
    }


    private fun removeLinks(s: String): String {
        return if (s.startsWith("http://") || s.startsWith("https://")) "" else s;
    }

    private fun removeNonLetters(s: String): String {
        val res = StringBuilder()
        for (c in s) {
            if (isLetter(c) || c == ' ' || c == '\'') {
                res.append(c)
            } else if (c == '-' || c == '.') {
                res.append(' ');
            } else if (c == '’') {
                res.append('\'');
            }
        }
        return res.toString()
    }
}


class CrawlerException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)