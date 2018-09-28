package com.mindhubweb.salvo;

//Importes necesarios
import javafx.scene.control.Cell;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//Entidad
@Entity
public class Salvo {
//Propiedades y relaciones
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int turn;

    @ElementCollection
    @Column(name="location")
    private List<String> cells = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

//Constructor nulo
    public Salvo() {}

//Constructor de tiros
    public Salvo(List<String> cells, int turn) {
        this.turn = turn;
        this.cells = cells;
    }

//Getters y setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getCells() {
        return cells;
    }

    public void setCells(List<String> cells) {
        this.cells = cells;
    }

    public void addCell(Cell cell) {
        cells.addAll(cells);
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }


    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }



}
