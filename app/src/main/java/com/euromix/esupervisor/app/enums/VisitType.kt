package com.euromix.esupervisor.app.enums

enum class VisitType {
    REGULAR,
    ONETIME,
    REMOTE,
    UNDEFINED;

    companion object {

        fun visitTypes() = arrayOf(REGULAR, ONETIME, REMOTE, UNDEFINED)

        fun getByIndex(index: Int): VisitType {

            return if (index == -1) UNDEFINED else {
                val visitTypes = visitTypes()
                if (visitTypes.size > index) visitTypes[index] else UNDEFINED
            }
        }
    }
}