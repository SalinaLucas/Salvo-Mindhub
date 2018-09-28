package com.mindhubweb.salvo;

//Clase Enum con estados de juego
public enum GameState {
    WAIT_OPPONENT_JOIN,
    PLACE_SHIPS,
    WAIT_OPPONENT_SHIPS,
    PLACE_SALVOS,
    WAIT_OPPONENT_SALVOS,
    WIN,
    DRAW,
    LOSE,
    UNDEFINED
}
