package com.oukoda.kanjiichiran.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.oukoda.kanjiichiran.MyApplication
import com.oukoda.kanjiichiran.databinding.ActivityFourwordWriteBinding
import com.oukoda.kanjiichiran.dataclass.FourWord

class WriteFourWordActivity : AppCompatActivity() {
    companion object {
        private val TAG: String? = MeaningFourWordActivity::class.simpleName
    }
    private lateinit var binding: ActivityFourwordWriteBinding
    private lateinit var fourWordList: List<FourWord>
    private var nowIndex = 0
    private var trueCount = 0
    private var hintList: ArrayList<Boolean> = arrayListOf<Boolean>(false, false, false, false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFourwordWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fourWordList = (application as MyApplication).fourWordList.shuffled()
        binding.btAnswer.setOnClickListener {
            showAnswer()
        }
        binding.btHint.setOnClickListener {
            addHint()
        }
        binding.btTrue.setOnClickListener {
            trueCount += 1
            setNextQuestion()
        }

        binding.btFalse.setOnClickListener {
            setNextQuestion()
        }
        setUpperInfo()
        setWord()
    }

    private fun setWord() {
        val nowFourWord = fourWordList[nowIndex]
        binding.tvMean.text = getDescriptorString(nowFourWord.descriptor)
        binding.tvYomi.text = nowFourWord.yomi
        binding.tvAnswer.text = getAnswerString()
    }

    private fun getDescriptorString(descriptor: String): String {
        return descriptor.replace("。", "。\n").split("▽")[0]
    }

    private fun getAnswerString(): String {
        var answerList = fourWordList[nowIndex].word.toList().toMutableList()
        for (hintIndex in hintList.indices) {
            if (!hintList[hintIndex]) {
                answerList[hintIndex] = '□'
            }
        }
        return answerList.joinToString("")
    }

    private fun addHint() {
        val falseCount = hintList.filter { !it }.size
        if (falseCount == 0) {
            return
        }
        var openHintIndex = (0 until falseCount).random()
        for (hintIndex in hintList.indices) {
            if (hintList[hintIndex]) {
                openHintIndex += 1
                continue
            }
            if (hintIndex == openHintIndex) {
                hintList[hintIndex] = true
                break
            }
        }
        binding.tvAnswer.text = getAnswerString()
    }

    private fun showAnswer() {
        hintList = arrayListOf(true, true, true, true)
        binding.tvAnswer.text = fourWordList[nowIndex].word
    }

    private fun setNextQuestion() {
        nowIndex += 1
        if (nowIndex >= fourWordList.size) {
            nowIndex = 0
        }
        hintList = arrayListOf(false, false, false, false)
        setWord()
        setUpperInfo()
    }

    private fun setUpperInfo() {
        binding.tvNumber.text = "${nowIndex + 1}/${fourWordList.size}"
        binding.tvTrueCount.text = "$trueCount"
        val acceuracy: Double = (trueCount * 100 / (nowIndex + 1)).toDouble()
        binding.tvAccuracy.text = "%.2f".format(acceuracy)
    }

    override fun onStart() {
        super.onStart()
    }
    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
