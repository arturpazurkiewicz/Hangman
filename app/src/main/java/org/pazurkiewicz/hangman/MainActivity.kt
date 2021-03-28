package org.pazurkiewicz.hangman

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.setMargins

import androidx.core.view.setPadding
import org.pazurkiewicz.hangman.databinding.ActivityMainBinding
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var wordTiles: ArrayList<MyCharText>
    private val alphabet = List(26) {
        (it + 65).toChar()
    }
    private var points = 0
    private val startMistakes = 0
    private var mistakes = startMistakes
    private var maxMistakes = 8
    private var charCounterToGuess by Delegates.notNull<Int>()
    private lateinit var words: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        words = resources.getStringArray(R.array.words)
        refreshPoints()
        generateWord()
        refreshImage()


    }

    private fun clearPreviousGame() {
        mistakes = startMistakes
        refreshImage()
        binding.wordLayout.removeAllViews()
        wordTiles = ArrayList()
        for (tag in alphabet) {
            val keyboardButton = binding.root.findViewWithTag<Button>(tag.toString())
            keyboardButton.isEnabled = true
            keyboardButton.backgroundTintList = getColorStateList(R.color.enabled)
        }
    }

    private fun generateWord() {
        clearPreviousGame()
        val word = words.random()
        charCounterToGuess = word.length
        val space = -word.length / 2 + 10

        for (letter in word) {
            val charText = MyCharText(this, letter, space)
            binding.wordLayout.addView(charText)
            wordTiles.add(charText)
        }


    }

    private fun refreshPoints() {
        binding.points.text = getString(R.string.points, points)
    }

    private fun refreshImage() {
        val image = binding.image
        when (mistakes) {
            -1 -> image.setImageResource(R.drawable.hangman_escape)
            0 -> image.setImageResource(R.drawable.hangman1)
            1 -> image.setImageResource(R.drawable.hangman2)
            2 -> image.setImageResource(R.drawable.hangman3)
            3 -> image.setImageResource(R.drawable.hangman4)
            4 -> image.setImageResource(R.drawable.hangman5)
            5 -> image.setImageResource(R.drawable.hangman6)
            6 -> image.setImageResource(R.drawable.hangman7)
            7 -> image.setImageResource(R.drawable.hangman8)
            8 -> image.setImageResource(R.drawable.hangman9)
            9 -> image.setImageResource(R.drawable.hangman10)
        }
    }

    fun keyboardClick(view: View) {
        if (mistakes == -1 || mistakes > maxMistakes) {
            generateWord()
            return
        }
        if (view is Button) {
            var found = false
            for (tile in wordTiles) {
                if (!tile.wasGuessed && tile.isGuessed(view.text[0])) {
                    found = true
                    charCounterToGuess--
                }
            }
            if (!found) {
                mistakes++
                refreshImage()
            } else if (charCounterToGuess == 0) {
                mistakes = -1
                refreshImage()
                points++
                refreshPoints()
            }

            if (mistakes > maxMistakes) {
                points--
                refreshPoints()
                showNotFound()
            }
            if (mistakes == -1 || mistakes > maxMistakes) {
                for (tag in alphabet) {
                    val keyboardButton = binding.root.findViewWithTag<Button>(tag.toString())
                    keyboardButton.isEnabled = true
                }
            } else {
                view.isEnabled = false
                view.backgroundTintList = getColorStateList(R.color.disabled)
            }
        }
    }

    fun resetClick(view: View) {
        points = 0
        refreshPoints()
        generateWord()
    }

    private fun showNotFound() {
        for (tile in wordTiles) {
            if (!tile.wasGuessed) {
                tile.text = tile.wordChar.toString()
                tile.setTextColor(getColor(R.color.not_found))
            }
        }
    }


    private class MyCharText(context: Context, val wordChar: Char, space: Int) :
        AppCompatTextView(context) {
        var wasGuessed = false

        init {
            val layout = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
            if (space > 0) {
                this.setPadding(space.px)
                layout.setMargins(space.px)
            }
            this.layoutParams = layout
            this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f)
            this.text = context.getString(R.string.unknown)
        }


        fun isGuessed(char: Char): Boolean {
            if (char.equals(wordChar, ignoreCase = true)) {
                wasGuessed = true
                this.text = wordChar.toString()
                this.refreshDrawableState()
            }
            return wasGuessed
        }

        val Int.px: Int
            get() = (this * Resources.getSystem().displayMetrics.density).toInt()


    }
}