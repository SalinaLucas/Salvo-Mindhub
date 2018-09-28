package com.mindhubweb.salvo;


//Importes necesarios
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Arrays;


//Repositorio de GamePlayer
@RepositoryRestResource
public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {


}