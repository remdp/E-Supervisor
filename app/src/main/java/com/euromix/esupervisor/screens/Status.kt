package com.euromix.esupervisor.screens

import com.euromix.esupervisor.R

class Status {

    companion object {
        fun getColorStatus(status: String) =
            when (status) {
                "Выполнено" -> R.color.colorMountainMeadow
                "В процессе согласования" -> R.color.colorPictonBlue
                "Согласован" -> R.color.colorAtlantis
                "Ошибка" -> R.color.colorPomegranate
                "Отклонён" -> R.color.colorBeautyBush
                "В работе" -> R.color.colorSelectiveYellow
                else -> R.color.colorCasper
            }
    }
}