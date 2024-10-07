package com.example.tresenraya

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var activePlayer = true
    private var playerNames = arrayOf("Jugador X", "Jugador O")
    private var gameState = IntArray(9) { 2 }
    private val winningPositions = arrayOf(
        intArrayOf(0, 1, 2),
        intArrayOf(3, 4, 5),
        intArrayOf(6, 7, 8),
        intArrayOf(0, 3, 6),
        intArrayOf(1, 4, 7),
        intArrayOf(2, 5, 8),
        intArrayOf(0, 4, 8),
        intArrayOf(2, 4, 6)
    )
    private var gameActive = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showPlayerNameDialog()

        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        val resetButton = findViewById<Button>(R.id.resetButton)
        val continueButton = findViewById<Button>(R.id.continueButton)

        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as Button
            button.setOnClickListener {
                onButtonClick(it, i)
            }
        }

        resetButton.setOnClickListener {
            resetGame()
        }

        continueButton.setOnClickListener {
            continueGame()
        }
    }

    private fun showPlayerNameDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_player_names, null)
        builder.setView(dialogView)

        val nameInputX = dialogView.findViewById<EditText>(R.id.nameInputX)
        val nameInputO = dialogView.findViewById<EditText>(R.id.nameInputO)

        builder.setTitle("Ingresa los nombres de los jugadores")
        builder.setPositiveButton("Aceptar") { dialog, which ->
            playerNames[0] = nameInputX.text.toString().ifEmpty { "Jugador X" }
            playerNames[1] = nameInputO.text.toString().ifEmpty { "Jugador O" }
            resetBoard()  // Reiniciar el tablero
            showStartingPlayerDialog()  // Mostrar diálogo para elegir quién comienza
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun showStartingPlayerDialog() {
        val options = arrayOf(playerNames[0], playerNames[1])
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Selecciona quién comienza")
        builder.setItems(options) { dialog, which ->
            activePlayer = (which == 0)  // Si elige el primer jugador, activePlayer será true
            findViewById<TextView>(R.id.statusTextView).text = "${playerNames[0]} vs ${playerNames[1]} --- Turno de ${playerNames[if (activePlayer) 0 else 1]}"
        }
        builder.setCancelable(false)
        builder.show()
    }

    private fun onButtonClick(view: View, buttonIndex: Int) {
        val button = view as Button

        if (gameState[buttonIndex] != 2 || !gameActive) {
            return
        }

        gameState[buttonIndex] = if (activePlayer) 1 else 0
        button.text = if (activePlayer) "X" else "O"

        if (checkWinner()) {
            gameActive = false
            val winner = playerNames[if (activePlayer) 0 else 1]
            findViewById<TextView>(R.id.statusTextView).text = "$winner Gana!"
            return
        }

        if (gameState.all { it != 2 }) {
            findViewById<TextView>(R.id.statusTextView).text = "Es empate!"
            highlightDraw()
            gameActive = false
            return
        }

        activePlayer = !activePlayer
        val nextPlayer = playerNames[if (activePlayer) 0 else 1]
        findViewById<TextView>(R.id.statusTextView).text = "Turno de $nextPlayer"
    }

    private fun checkWinner(): Boolean {
        for (position in winningPositions) {
            if (gameState[position[0]] == gameState[position[1]] &&
                gameState[position[1]] == gameState[position[2]] &&
                gameState[position[0]] != 2
            ) {
                highlightWinningLine(position)
                return true
            }
        }
        return false
    }

    private fun highlightWinningLine(winningPosition: IntArray) {
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        for (index in winningPosition) {
            val button = gridLayout.getChildAt(index) as Button
            button.setBackgroundColor(resources.getColor(android.R.color.holo_green_light, theme))
        }
    }

    private fun highlightDraw() {
        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as Button
            button.setBackgroundColor(resources.getColor(android.R.color.holo_red_light, theme))
        }
    }

    private fun resetGame() {
        resetBoard()  // Reiniciar el tablero y mostrar el diálogo para cambiar nombres
        showPlayerNameDialog()
    }

    private fun continueGame() {
        // Limpiar el tablero y continuar el juego
        resetBoard()
        findViewById<TextView>(R.id.statusTextView).text = "${playerNames[0]} vs ${playerNames[1]} - Turno de ${playerNames[0]}"
    }

    private fun resetBoard() {
        activePlayer = true
        gameActive = true
        gameState = IntArray(9) { 2 }
        findViewById<TextView>(R.id.statusTextView).text = "${playerNames[0]} vs ${playerNames[1]} - Turno de ${playerNames[0]}"

        val gridLayout = findViewById<GridLayout>(R.id.gridLayout)
        for (i in 0 until gridLayout.childCount) {
            val button = gridLayout.getChildAt(i) as Button
            button.text = ""
            button.setBackgroundColor(resources.getColor(android.R.color.transparent, theme))  // Reset color
        }
    }
}
