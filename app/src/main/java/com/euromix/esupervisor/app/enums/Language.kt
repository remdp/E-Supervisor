package com.euromix.esupervisor.app.enums

import com.euromix.esupervisor.R
import java.util.*

enum class Language {

    ENG{
        override fun nameStringRes(): Int {
            return R.string.language_eng
        }

        override fun locale(): Locale {
            return Locale.ENGLISH
        }
    },
    UA{
        override fun nameStringRes(): Int {
            return R.string.language_ua
        }

        override fun locale(): Locale {
            return Locale("uk", "UA")
        }
    },

    UNDEFINED{
        override fun nameStringRes(): Int {
            return R.string.language_eng
        }

        override fun locale(): Locale {
            return Locale.ENGLISH
        }
    };

    abstract fun nameStringRes(): Int
    abstract fun locale(): Locale
}