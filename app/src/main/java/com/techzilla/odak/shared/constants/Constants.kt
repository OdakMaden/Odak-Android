package com.techzilla.odak.shared.constants

import com.techzilla.odak.shared.model.CurrencyModel
import com.techzilla.odak.shared.model.User


val USER = User("32", true, ArrayList())


val list : MutableList<CurrencyModel> = mutableListOf(
    CurrencyModel("AMERİKAN DOLARI", "USD", 8.4436, 8.4431, "% -0,08", 0),
        CurrencyModel("EURO", "EUR/TRY", 8.4436, 8.4431, "% 0,08", 0),
        CurrencyModel("İNGİLİZ STERLİNİ", "GBP/TRY", 8.4436, 8.4431, "% -0,08", 0),
        CurrencyModel("İSVEÇ FRANKI", "CHF/TRY", 8.4436, 8.4431, "% 0,08", 0),
        CurrencyModel("", "GR ALTIN", 8.4436, 8.4431, "% -0,08", 1),
        CurrencyModel("", "HAS ALTIN", 8.4436, 8.4431, "% 0,08", 1),
        CurrencyModel("", "22 AYAR", 8.4436, 8.4431, "% -0,08", 1),
        CurrencyModel("BITCOIN", "BTC/TRY", 8.4436, 8.4431, "% -0,08", 2),
        CurrencyModel("CARDANO", "ADA/TRY", 8.4436, 8.4431, "% 0,08", 2),
        CurrencyModel("COSMOS", "ATOM/TRY", 8.4436, 8.4431, "% -0,08", 2),
        CurrencyModel("ETHEREUM", "ETH/TRY", 8.4436, 8.4431, "% 0,08", 2),
)