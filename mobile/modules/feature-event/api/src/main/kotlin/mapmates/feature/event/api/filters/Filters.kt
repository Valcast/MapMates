package mapmates.feature.event.api.filters

sealed interface Filter {
    data class ByCategory(val category: Category) : Filter {
        override fun equals(other: Any?): Boolean = other is ByCategory
        override fun hashCode(): Int = ByCategory::class.hashCode()
    }

    data class ByLocation(
        val latitude: Double,
        val longitude: Double,
        val radiusInKm: Double,
    ) : Filter {
        override fun equals(other: Any?): Boolean = other is ByLocation
        override fun hashCode(): Int = ByLocation::class.hashCode()
    }

    data class ByDateRange(val dateRange: DateRange) : Filter {
        override fun equals(other: Any?): Boolean = other is ByDateRange
        override fun hashCode(): Int = ByDateRange::class.hashCode()
    }

    data class BySortOrder(val sortOrder: SortOrder) : Filter {
        override fun equals(other: Any?): Boolean = other is BySortOrder
        override fun hashCode(): Int = BySortOrder::class.hashCode()
    }
}


inline fun <reified T : Filter> Set<Filter>.get(): T? =
    this.firstOrNull { it is T } as? T
