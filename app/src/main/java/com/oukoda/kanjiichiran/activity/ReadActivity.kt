package com.oukoda.kanjiichiran.activity

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.oukoda.kanjiichiran.MyApplication
import com.oukoda.kanjiichiran.databinding.ActivityReadBinding
import com.oukoda.kanjiichiran.dataclass.Word
import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class ReadActivity : AppCompatActivity(), TextToSpeech.OnInitListener{
    companion object{
        private val TAG : String? = ReadActivity::class.simpleName
        private const val UTTERANCE_ID = "utteranceId"
    }
    private lateinit var binding: ActivityReadBinding
    private var nowIndex = -1;
    private lateinit var innerThread: HandlerThread
    private lateinit var innerThreadHandler: Handler
    private lateinit var uiThreadHandler: Handler
    private lateinit var wordList: List<Word>
    private var runningThreadFlag = true
    private lateinit var textToSpeech: TextToSpeech
    private val lock = ReentrantLock()
    private val condition = lock.newCondition()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makeWordList()
        binding.progressBar.max = wordList.size
        uiThreadHandler = Handler(Looper.getMainLooper())
        innerThread = HandlerThread(TAG);
        innerThread.start()
        innerThreadHandler = Handler(innerThread.looper)
        textToSpeech = TextToSpeech(this, this)

        binding.btStart.setOnClickListener {
            runningThreadFlag = true
            binding.btStart.isClickable = false
            binding.btStop.isClickable = true
            doInnerThreadWork()
        }

        binding.btStop.setOnClickListener {
            binding.btStart.isClickable = true
            binding.btStop.isClickable = false
            runningThreadFlag = false
        }

        binding.etQuestionTime.addTextChangedListener(textWatcher)
        binding.etAnswerTime.addTextChangedListener(textWatcher)

        binding.btNext.setOnClickListener {
            updateWord()
            showAnswer()
        }

        setRequiredTime()
        binding.tvCount.text = "${nowIndex+1}/${wordList.size}"
    }

    private fun makeWordList(){
        val tempList = mutableListOf<Word>()
        tempList.addAll((application as MyApplication).yomiList)
        tempList.addAll((application as MyApplication).onkunList)
        tempList.addAll((application as MyApplication).yomiQuestionList)
        wordList = tempList.toList().distinctBy { it -> it.word }.shuffled()
    }

    private fun updateWord(){
        nowIndex += 1
        if (nowIndex == wordList.size){
            nowIndex = 0
        }
        if (!binding.swKanji.isChecked){
            uiThreadHandler.post {
                binding.tvKanji.setTextColor(Color.GRAY)
                binding.tvKanji.text = wordList[nowIndex].word
                binding.tvYomi.text = ""
            }
        } else {
            uiThreadHandler.post {
                binding.tvKanji.text = ""
                binding.tvYomi.setTextColor(Color.GRAY)
                binding.tvYomi.text = wordList[nowIndex].yomi
            }
            readNowWord()
        }
        uiThreadHandler.post {
            binding.tvCount.text = "${nowIndex+1}/${wordList.size}"
            binding.progressBar.progress = nowIndex + 1
        }
    }

    private fun showAnswer(){            uiThreadHandler.post {

    if (!binding.swKanji.isChecked){
                binding.tvYomi.setTextColor(Color.RED)
                binding.tvYomi.text = wordList[nowIndex].yomi
            }
            readNowWord()
        } else {
            uiThreadHandler.post {
                binding.tvYomi.setTextColor(Color.RED)
                binding.tvKanji.text = wordList[nowIndex].word
            }
        }
    }

    private fun setRequiredTime(){
        if (binding.etQuestionTime.text.toString() == "" ||
                binding.etAnswerTime.text.toString() == ""){
            return
        }
        val questionTime = (binding.etQuestionTime.text.toString().toDouble()/1000) * wordList.size
        val answerTime = (binding.etAnswerTime.text.toString().toDouble()/1000) * wordList.size
        val requiredTime = questionTime + answerTime
        val requiredMinute = (requiredTime / 60).toInt()
        val requiredSecond = (requiredTime % 60).toInt()
        binding.tvRequiredTime.text = "およそ${requiredMinute}分${requiredSecond}秒かかる見込みです。"
    }

    private fun doInnerThreadWork() {
        innerThreadHandler.post {
            while (runningThreadFlag) {
                updateWord()
                Thread.sleep(binding.etQuestionTime.text.toString().toLong())
                showAnswer()
                Thread.sleep(binding.etAnswerTime.text.toString().toLong())
            }
        }
    }

    private fun readNowWord(){
        textToSpeech.speak(
            wordList[nowIndex].yomi,
            TextToSpeech.QUEUE_ADD,
            null,
            UTTERANCE_ID
        )
        lock.withLock { condition.await() }
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

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val locale = Locale.JAPAN
            if (textToSpeech.isLanguageAvailable(locale) > TextToSpeech.LANG_AVAILABLE) {
                textToSpeech.language = Locale.JAPAN
                textToSpeech.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onDone(utteranceId: String) {
                        lock.withLock {
                            condition.signalAll()
                        }
                    }
                    override fun onError(utteranceId: String) { }
                    override fun onStart(utteranceId: String) { }
                })
            } else {
                Log.e(TAG, "onInit: fatal init")
            }
        }
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            setRequiredTime()
        }
    }
}