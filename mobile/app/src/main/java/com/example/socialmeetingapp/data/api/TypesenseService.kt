package com.example.socialmeetingapp.data.api

import com.example.socialmeetingapp.domain.model.Category
import com.example.socialmeetingapp.domain.model.EventPreview
import org.typesense.api.Client
import org.typesense.api.Configuration
import org.typesense.model.SearchParameters
import org.typesense.model.SearchResultHit
import org.typesense.resources.Node
import java.time.Duration


class TypesenseService {

    lateinit var typesenseClient: Client

    init {
        setupClient()
    }

    private fun setupClient() {
        val nodes: MutableList<Node> = ArrayList()
        nodes.add(
            Node(
                "https",
                "7iwndt2gx13yhsq8p-1.a1.typesense.net",
                "443"
            )
        )

        val config = Configuration(
            nodes,
            Duration.ofSeconds(2),
            "3mBA21QAXkaaMXZrJ2kY1ISlzq7If8tu"
        )

        typesenseClient = Client(config)
    }

    fun searchEvents(query: String): List<EventPreview> {
        val searchParameters = buildSearchParameters(query, 1)

        val searchResults =
            typesenseClient.collections("events").documents().search(searchParameters)



        return searchResults.hits.map { it.toEventPreview() }
    }

    private fun buildSearchParameters(
        query: String,
        perPage: Int,
        filterBy: String? = null,
        page: Int = 1
    ): SearchParameters {
        return SearchParameters().apply {
            q = query
            queryBy = "name"
            facetBy = "name"
            this.perPage = perPage
            this.page = page
            filterBy?.let { this.filterBy = it }
        }
    }

    private fun SearchResultHit.toEventPreview(): EventPreview {
        return EventPreview(
            id = this.document["id"] as String,
            title = this.document["title"] as String,
            locationAddress = this.document["locationAddress"] as String,
            category = this.document["category"].let { Category.valueOf(it.toString().uppercase()) }
        )
    }
}