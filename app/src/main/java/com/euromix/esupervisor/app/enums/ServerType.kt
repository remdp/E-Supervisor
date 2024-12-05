package com.euromix.esupervisor.app.enums

enum class ServerType(val typeName: String) {

    BALANCE_UNIT("СправочникСсылка.БалансовыеЕдиницы"),
    TRADING_TEAM("СправочникСсылка.ТорговыеКоманды"),
    TRADING_TEAM_HR("СправочникСсылка.ТорговыеКомандыЗУП"),
    TRADING_AGENT("СправочникСсылка.ТорговыеАгенты");

    companion object {
        private val map = entries.associateBy(ServerType::typeName)
        fun fromTypeName(typeName: String): ServerType = map[typeName]
            ?: throw IllegalArgumentException("Unknown type name: $typeName")
    }
}