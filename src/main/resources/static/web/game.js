//Variable y función madre que trae la data
var start;
var salvoPlayerId;

function getData (id) {
$.get("/api/game_view/"+id).done(function(data){
        start = data;
        //head();
        createDinamicGrid();
        createVersus(id);
        createFire();
})}


var url = window.location.href;

//Función para variar la URL
function getId (string) {
    var newString = string.slice(string.indexOf("=") + 1);
    return  newString;
}

getData(getId(url)) ;

//Función para  crear título dinámico
/*function head () {
    $("#tittle").append("<h1 class='text-center text-white'>Oceanic War</h1>");
}*/


//Funciones que crean las grillas con JQuery, asigna IDs y colorea las posiciones mediante ciclos FOR
function createDinamicGrid () {

     for (var i=0; i<11; i++){
       $("#row").append("<th class='heading'>"+i+"</th>");


     }

     for (i=65; i<75; i++){
       $("#columns").append("<tr id='tdw' class='"+String.fromCharCode(i)+"'><td>"+String.fromCharCode(i)+"</td></tr>");

     }


     var x = 65;
     for (i=1; i<11; i++){
        $("#columns tr").each(function() {
        $(this).append("<td class='"+$(this).attr("class")+i+"'></td>")
      })
     }

     for (i in start.ships) {
       for (var j in start.ships[i].locations) {
         if (start.ships[i].locations[j] == $("#table ." + start.ships[i].locations[j] + "").attr("class")) {
            $("#table ." + start.ships[i].locations[j] + "").addClass("shipColor");
               }
       }

     }
}

function createFire () {

        for (var i=0; i<11; i++){
            $("#rowSalvo").append("<th  class='heading'>"+i+"</th>");
        }

        for (i=65; i<75; i++){
         $("#columnsSalvo").append("<tr id='tdw' class='"+String.fromCharCode(i)+"'><td>"+String.fromCharCode(i)+"</td></tr>");
        }

        var x = 65;
        for (i=1; i<11; i++){
            $("#columnsSalvo tr").each(function() {
                $(this).append("<td class='"+$(this).attr("class")+i+"'></td>")

            })
        }

        //Ciclo para colorear los tiros dependiendo del jugador
        for (i in start.salvoes) {
           if (start.salvoes[i].player == salvoPlayerId) {
               for (var j in start.salvoes[i].locations) {
                  if (start.salvoes[i].locations[j] == $("#salvoTable ." + start.salvoes[i].locations[j] + "").attr("class")) {
                      $("#salvoTable ." + start.salvoes[i].locations[j] + "").addClass("salvoColor");
                  }
               }
           } else {
                 for (var j in start.ships) {
                    for (var k in start.salvoes[i].locations) {
                        //console.log(hi.salvoes[i].locations[k])
                        var index = start.ships[j].locations.indexOf(start.salvoes[i].locations[k]);
                       if(index!=-1){
                        $("#table ." + start.ships[j].locations[index] + "").html("<img src='imagenes css/image18.png'>");
                       }
                    }
                 }
             }
        }

}

//Función que muestra los jugadores presentes
function createVersus (px) {
    for (var i in start.gamePlayers) {
            if (start.gamePlayers[i].id == px) {

                $("#pOne").html(start.gamePlayers[i].player.username + "(you) ");
                salvoPlayerId = start.gamePlayers[i].player.id;
            }
            else {
                $("#pTwo").html("vs " + start.gamePlayers[i].player.username);

            }

        }

}


//Log out
$("#btn-logout").click(function(){
  $.post("/api/logout")
        .done(function(data) {
           window.location.replace('/web/games.html');
        })
})







