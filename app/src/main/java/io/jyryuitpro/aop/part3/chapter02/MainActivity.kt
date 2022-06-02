package io.jyryuitpro.aop.part3.chapter02

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.viewPager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initData()
    }

    private fun initData() {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(
                remoteConfigSettings {
                    minimumFetchIntervalInSeconds = 0
                }
        )
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                val quotes = parseQuoutesJson(remoteConfig.getString("quotes"))
                val isNameRevealed = remoteConfig.getBoolean("is_name_revealed")

                displayQuotesPager(quotes, isNameRevealed)
            }
        }
    }

    private fun parseQuoutesJson(json: String): List<Quote> {
        val jsonArray = JSONArray(json)
        var jsonList = emptyList<JSONObject>()

        for (index in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(index)
            jsonObject?.let {
                jsonList = jsonList + it
            }
        }

        return jsonList.map {
            Quote(
                quote = it.getString("quote"),
                name = it.getString("name")
            )
        }
    }

    private fun displayQuotesPager(quotes: List<Quote>, isNameRevealed: Boolean) {
        viewPager.adapter = QuotesPagerAdapter(
            quotes,
            isNameRevealed
        )
    }
}

//[
//    {
//        "quote": "지식보다 더 중요한 것은 상상력이다.",
//        "name": "아인슈타인"
//    },
//    {
//        "quote": "나는 생각한다 고로 나는 존재한다.",
//        "name": "데카르트"
//    },
//    {
//        "quote": "산다는 것, 그것은 치열한 전투이다.",
//        "name": "로망로랑"
//    },
//    {
//        "quote": "하루에 3 시간을 걸으면 7 년 후에 지구를 한바퀴 돌 수 있다.",
//        "name": "사무엘존슨"
//    },
//    {
//        "quote": "언제나 현재에 집중할 수 있다면 행복할것이다.",
//        "name": "아인슈타인"
//    }
//]