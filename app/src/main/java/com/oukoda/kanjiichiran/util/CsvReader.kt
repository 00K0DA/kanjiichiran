package com.oukoda.kanjiichiran.util

import android.content.res.Resources
import android.util.Log
import com.opencsv.CSVReader
import com.oukoda.kanjiichiran.activity.ReadActivity
import com.oukoda.kanjiichiran.dataclass.FourWord
import com.oukoda.kanjiichiran.dataclass.Word
import java.io.InputStream
import java.io.InputStreamReader

class CsvReader {
    companion object{
        fun getWordList(resources: Resources): List<Word> {
            val reader = readCsv(resources, "2kyu.csv")
            return reader.readAll().map { it ->
                Word(it[0].replace("\uFEFF", "").toInt(), it[1], it[2], it[3])
            }
        }
        fun getFourWordList(resources: Resources): List<FourWord>{
            val reader = readCsv(resources, "fourword_comp.csv")
            return reader.readAll().map { it ->
                FourWord(it[0].replace("\uFEFF", "").toInt(), it[1], it[2], it[3])
            }
        }
        fun getYomiList(resources: Resources): List<Word> {
            val reader = readCsv(resources, "yomi.csv")
            return reader.readAll().map { it ->
                Word(it[0].replace("\uFEFF", "").toInt(), it[1], "", kata2hira(it[2]))
            }
        }

        fun getOnkunList(resources: Resources): List<Word> {
            val reader = readCsv(resources, "onkun.csv")
            return reader.readAll().map { it ->
                Word(it[0].replace("\uFEFF", "").toInt(), it[1], "", kata2hira(it[2]))
            }
        }

        fun getYomiQuestion(resources: Resources): List<Word> {
            val reader = readCsv(resources, "yomi_question.csv")
            return reader.readAll().map { it ->
                Word(it[0].replace("\uFEFF", "").toInt(), it[1], "", kata2hira(it[2]))
            }
        }
        private fun readCsv(resources: Resources, fileName: String): CSVReader {
            val input: InputStream = resources.assets.open(fileName)
            val iterator = InputStreamReader(input, "UTF-8")
            return CSVReader(iterator)
        }

        private fun kata2hira(str: String) =
            str.map {
                if (it in 0x30A1.toChar()..0x30F6.toChar()) {
                    it - 0x60
                } else {
                    it
                }
            }.joinToString("")
    }
}