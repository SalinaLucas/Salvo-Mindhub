package com.mindhubweb.salvo;


import com.fasterxml.jackson.annotation.JsonIgnore;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.*;
import javax.persistence.CascadeType;

import static java.util.stream.Collectors.toList;


@Entity
public class Player {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;
    private String userName;
    private String password;


    @OneToMany(mappedBy="player", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Score> scores = new HashSet<>();

    @JsonIgnore
    public List<Game> getGames() {
        return gamePlayers.stream().map(sub -> sub.getGame()).collect(toList());
    }

    public Player() { }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Player(String email, String password) {
        this.userName = email;
        this.password = password;

    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }

    public void addScore(Score score) {
        score.setPlayer(this);
        this.scores.add(score);
    }


    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public Score getScore (Game game) {
        return scores.stream().filter(score -> score.getGame().getId() == game.getId()).findAny().orElse(null);
    }

}

