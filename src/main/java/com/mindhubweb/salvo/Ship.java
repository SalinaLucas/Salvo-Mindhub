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
public class Ship {
//Propiedades y relaciones
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String shipType;

    @ElementCollection
    @Column(name="location")
    private List<String> cells = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

//Constructor nulo
    public Ship() {}

//Constructor de barcos
    public Ship(String shipType, List<String> cells) {
        this.shipType = shipType;
        this.cells = cells;
    }

//Getters y setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getShipType() {
        return shipType;
    }

    public void setShipType(String shipType) {
        this.shipType = shipType;
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


    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }



}
