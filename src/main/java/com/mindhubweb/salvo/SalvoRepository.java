package com.mindhubweb.salvo;


//Importes necesarios
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//Repositorio de tiros
@RepositoryRestResource
public interface SalvoRepository extends JpaRepository<Salvo, Long> {

}