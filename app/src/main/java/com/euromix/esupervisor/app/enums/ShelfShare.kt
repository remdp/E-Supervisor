package com.euromix.esupervisor.app.enums

enum class ShelfShare {

    FACE, CM, UNDEFINED;

    fun getIndex() = if (this == UNDEFINED) -1 else entries.indexOf(this)

    companion object {
        fun getByIndex(index: Int) = entries.getOrNull(index) ?: UNDEFINED
    }

}