package com.apps.get

import android.util.Log
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.IOException
import java.io.StringReader
import java.io.UnsupportedEncodingException
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

class RSSParser  // constructor
{
    fun getRSSFeedItems(rss_url: String?): List<RSSItem> {
        val itemsList: MutableList<RSSItem> = ArrayList()
        val rss_feed_xml: String?
        rss_feed_xml = getXmlFromUrl(rss_url)
        if (rss_feed_xml != null) {
            try {
                val doc = getDomElement(rss_feed_xml)
                val nodeList = doc!!.getElementsByTagName(TAG_CHANNEL)
                val e = nodeList.item(0) as Element
                val items = e.getElementsByTagName(TAG_ITEM)
                for (i in 0 until items.length) {
                    val e1 = items.item(i) as Element
                    val title = this.getValue(e1, TAG_TITLE)
                    val link = this.getValue(e1, TAG_LINK)
                    val description = this.getValue(e1, TAG_DESRIPTION)
                    val pubdate = this.getValue(e1, TAG_PUB_DATE)
                    val guid = this.getValue(e1, TAG_GUID)
                    if (description.startsWith("<p>")) {
                        val cleanDesc = description.substring(description.indexOf("<p>") + 4, description.substring(description.indexOf("</p>") - 4).toInt())
                        val rssItem = RSSItem(title, link, cleanDesc, pubdate, guid)
                        itemsList.add(rssItem)
                    } else {
                        val rssItem = RSSItem(title, link, description.replace("</p>", ""), pubdate, guid)
                        itemsList.add(rssItem)
                    }
                }
            } catch (e: Exception) {
                // Check log for errors
                e.printStackTrace()
            }
        }

        // return item list
        return itemsList
    }

    private fun getXmlFromUrl(url: String?): String? {
        var xml: String? = null
        try {
            val httpClient = DefaultHttpClient()
            val httpGet = HttpGet(url)
            val httpResponse = httpClient.execute(httpGet)
            val httpEntity = httpResponse.entity
            xml = EntityUtils.toString(httpEntity)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        } catch (e: ClientProtocolException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // return XML
        return xml
    }

    fun getDomElement(xml: String?): Document? {
        var doc: Document? = null
        val dbf = DocumentBuilderFactory.newInstance()
        try {
            val db = dbf.newDocumentBuilder()
            val `is` = InputSource()
            `is`.characterStream = StringReader(xml)
            doc = db.parse(`is`)
        } catch (e: ParserConfigurationException) {
            Log.e("Error: ", e.message!!)
            return null
        } catch (e: SAXException) {
            Log.e("Error: ", e.message!!)
            return null
        } catch (e: IOException) {
            Log.e("Error: ", e.message!!)
            return null
        }
        return doc
    }

    fun getElementValue(elem: Node?): String {
        var child: Node?
        if (elem != null) {
            if (elem.hasChildNodes()) {
                child = elem.firstChild
                while (child != null) {
                    if (child.nodeType == Node.TEXT_NODE || child.nodeType == Node.CDATA_SECTION_NODE) {
                        return child.nodeValue
                    }
                    child = child
                            .nextSibling
                }
            }
        }
        return ""
    }

    fun getValue(item: Element, str: String?): String {
        val n = item.getElementsByTagName(str)
        return getElementValue(n.item(0))
    }

    companion object {
        // RSS XML document CHANNEL tag
        private const val TAG_CHANNEL = "channel"
        private const val TAG_TITLE = "title"
        private const val TAG_LINK = "link"
        private const val TAG_DESRIPTION = "description"
        private const val TAG_ITEM = "item"
        private const val TAG_PUB_DATE = "pubDate"
        private const val TAG_GUID = "guid"
    }
}