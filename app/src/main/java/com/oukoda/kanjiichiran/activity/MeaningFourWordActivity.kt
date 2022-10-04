package com.oukoda.kanjiichiran.activity

import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.oukoda.kanjiichiran.MyApplication
import com.oukoda.kanjiichiran.R
import com.oukoda.kanjiichiran.databinding.ActivityFourwordMeaningBinding
import com.oukoda.kanjiichiran.dataclass.FourWord

class MeaningFourWordActivity : AppCompatActivity() {
    companion object {
        private val TAG: String? = MeaningFourWordActivity::class.simpleName
    }
    private lateinit var binding: ActivityFourwordMeaningBinding
    private lateinit var fourWordList: List<FourWord>
    private lateinit var soundPool: SoundPool
    private var correctSoundId: Int = 0
    private var wrongSoundId: Int = 0

    private var nowIndex = -1
    private lateinit var randomIndices: List<Int>
    private var runningThreadFlag = false
    private var answerList = mutableListOf<String>()
    private var comboCount = 0
    private var successCount = 0
    private val onclickListener = View.OnClickListener { view ->
        when (view?.id) {
            binding.tvCharA.id -> verifyAnswer(0)
            binding.tvCharB.id -> verifyAnswer(1)
            binding.tvCharC.id -> verifyAnswer(2)
            binding.tvCharD.id -> verifyAnswer(3)
            else -> Log.e(TAG, "onClick: unknown view" + view?.id)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFourwordMeaningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setAudioAttributes(audioAttributes)
            .setMaxStreams(2)
            .build()
        correctSoundId = soundPool.load(this, R.raw.correct, 1)
        wrongSoundId = soundPool.load(this, R.raw.wrong, 1)
        fourWordList = (application as MyApplication).fourWordList.toList().shuffled()
        setQuestion()
        binding.tvCharA.setOnClickListener(onclickListener)
        binding.tvCharB.setOnClickListener(onclickListener)
        binding.tvCharC.setOnClickListener(onclickListener)
        binding.tvCharD.setOnClickListener(onclickListener)
        binding.btNext.setOnClickListener {
            disableNextButton(true)
            setQuestion()
        }
        disableNextButton(true)
    }

    private fun setQuestion() {
        nowIndex += 1
        if (nowIndex >= fourWordList.size) {
            nowIndex = 0
        }
        binding.tvNumber.text = "$nowIndex/${fourWordList.size}"
        binding.tvAcc.text = if (nowIndex != 0) {
            "正答率: ${(successCount * 100 / (nowIndex)).toDouble()}%"
        } else {
            "正答率: -%"
        }
        binding.tvCombo.text = "連続正解:${comboCount}回"
        binding.tvYomi.text = ""
        binding.tvAnswer.text = ""
        binding.tvQuestion.text = ""
        answerList.removeAll(answerList)
        setRandomIndices()
        setSelection()
        setQuestionText()
    }

    private fun setRandomIndices() {
        // todo refactor
        var indices = (fourWordList.indices).toMutableList()
        indices.remove(nowIndex)
        indices = indices.shuffled().subList(0, 3).toMutableList()
        indices.add(nowIndex)
        randomIndices = indices
    }

    private fun setQuestionText() {
        val questionThread = HandlerThread("questionThread")
        questionThread.start()
        val questionHandler = Handler(questionThread.looper)
        val uiThreadHandler = Handler(Looper.getMainLooper())
        questionHandler.post {
            runningThreadFlag = false
            Thread.sleep(150)
            runningThreadFlag = true
            for (c in getDescriptorString()) {
                if (runningThreadFlag) {
                    uiThreadHandler.post {
                        binding.tvQuestion.text = binding.tvQuestion.text.toString() + c
                        binding.svQuestion.fullScroll(View.FOCUS_DOWN)
                    }
                    Thread.sleep(100)
                } else {
                    break
                }
            }
            runningThreadFlag = false
        }
        disableSelection(false)
    }

    private fun setSelection() {
        val charIndex = answerList.size
        randomIndices = randomIndices.shuffled()
        binding.tvCharA.text = fourWordList[randomIndices[0]].word[charIndex].toString()
        binding.tvCharB.text = fourWordList[randomIndices[1]].word[charIndex].toString()
        binding.tvCharC.text = fourWordList[randomIndices[2]].word[charIndex].toString()
        binding.tvCharD.text = fourWordList[randomIndices[3]].word[charIndex].toString()
    }

    private fun verifyAnswer(index: Int) {
        val charIndex = answerList.size
        if (fourWordList[nowIndex].word[charIndex] ==
            fourWordList[randomIndices[index]].word[charIndex]
        ) {
            answerList.add(fourWordList[nowIndex].word[charIndex].toString())
            if (answerList.size >= 4) {
                soundPool.play(correctSoundId, 1.0f, 1.0f, 0, 0, 1.0f)
                showAnswer()
            } else {
                applyAnswer()
                setSelection()
            }
        } else {
            soundPool.play(wrongSoundId, 1.0f, 1.0f, 0, 0, 1.0f)
            showAnswer()
        }
    }

    private fun applyAnswer() {
        binding.tvAnswer.text = answerList.joinToString("")
    }

    private fun showAnswer() {
        disableSelection(true)
        disableNextButton(false)
        binding.tvAnswer.text = fourWordList[nowIndex].word
        binding.tvYomi.text = fourWordList[nowIndex].yomi
        if (answerList.size == 4) {
            comboCount += 1
            successCount += 1
            binding.tvCharA.text = "せ"
            binding.tvCharB.text = "い"
            binding.tvCharC.text = "か"
            binding.tvCharD.text = "い"
        } else {
            comboCount = 0
            binding.tvCharA.text = "ど"
            binding.tvCharB.text = "ん"
            binding.tvCharC.text = "ま"
            binding.tvCharD.text = "い"
        }
    }

    private fun getDescriptorString(): String {
        Log.d(TAG, "getDescriptorString: ")
        return fourWordList[nowIndex].descriptor
            .replace("。", "。\n").split("▽")[0]
    }

    private fun disableSelection(isDisable: Boolean) {
        binding.tvCharA.isClickable = !isDisable
        binding.tvCharB.isClickable = !isDisable
        binding.tvCharC.isClickable = !isDisable
        binding.tvCharD.isClickable = !isDisable
    }

    private fun disableNextButton(isDisable: Boolean) {
        binding.btNext.isClickable = !isDisable
        binding.btNext.isEnabled = !isDisable
        binding.btNext.visibility = if (isDisable) View.INVISIBLE else View.VISIBLE
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
