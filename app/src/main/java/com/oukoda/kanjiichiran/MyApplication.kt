package com.oukoda.kanjiichiran

import android.app.Application
import com.oukoda.kanjiichiran.dataclass.FourWord
import com.oukoda.kanjiichiran.dataclass.Word
import com.oukoda.kanjiichiran.util.CsvReader

class MyApplication: Application(){
    companion object{
        private val TAG: String? = MyApplication::class.simpleName
    }
    lateinit var wordList : List<Word>
    lateinit var fourWordList: List<FourWord>
    lateinit var yomiList: List<Word>
    lateinit var onkunList: List<Word>
    lateinit var yomiQuestionList: List<Word>
    var wordListLength = 0
    var fourWordListLength = 0
    var yomiListLength = 0
    var onkunListLength = 0
    var yomiQuestionListLength = 0
    @Override
    override fun onCreate() {
        super.onCreate()
        wordList = CsvReader.getWordList(resources)
        wordListLength = wordList.size
        fourWordList = CsvReader.getFourWordList(resources)
        fourWordListLength = fourWordList.size
        yomiList = CsvReader.getYomiList(resources)
        yomiListLength = yomiList.size
        onkunList = CsvReader.getOnkunList(resources)
        onkunListLength = onkunList.size
        yomiQuestionList = CsvReader.getYomiQuestion(resources)
        yomiQuestionListLength = yomiQuestionList.size
    }
}