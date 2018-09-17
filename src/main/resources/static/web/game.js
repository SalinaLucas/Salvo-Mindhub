//Variable y función madre que trae la data
var start;
var salvoPlayerId;

function getData (id) {
$.get("/api/game_view/"+id).done(function(data){
        start = data;
        //head();
        createVersus(id);
        createFireTable();
        createFire();
        if (data.ships.length <= 0) {
            placeNewShips();
        } else {
            placeShips();
        }
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

function createFireTable () {

        for (var i=0; i<11; i++){
            $("#rowSalvo").append("<th  class='border border-light heading'>"+i+"</th>");
        }

        for (i=65; i<75; i++){
         $("#columnsSalvo").append("<tr id='tdw' class='"+String.fromCharCode(i)+"'><td class='border border-light'>"+String.fromCharCode(i)+"</td></tr>");
        }

        var x = 65;
        for (i=1; i<11; i++){
            $("#columnsSalvo tr").each(function() {
                $(this).append("<td id='"+$(this).attr("class")+i+"' class='border border-light cell "+$(this).attr("class")+i+"'></td>");

            })

            }
}

//Celdas clickeables
$("#salvoTable").on('click', '.cell', function() {
    if(!$(this).hasClass("scope")){

    if ($(this).hasClass('scope1')) {
        $(this).removeClass('scope1');
    } else {
        $(this).addClass('scope1');
    }
    }
})

//Enviar los tiros
$("#submitSalvo").click(function()  {

var salvoes = getShoot();

if ($("#salvoTable .scope1").length <= 5 && $("#salvoTable .scope1").length > 0) {

$.post({
  url: "/api/games/players/"+getId(url)+"/salvos",
  data: JSON.stringify(salvoes),
  dataType: "text",
  contentType: "application/json"
})
.done(function () {
     window.location.reload();
     console.log("success");

})
.fail(function () {

  console.log("fail");
})

} else {
    $("#spanShoot").html("You only have 5 shots").addClass("spanMsg");
}

})


//Función para colorear los tiros dependiendo del jugador
function createFire() {
        for (var i in start.salvoes) {
           if (start.salvoes[i].player == salvoPlayerId) {
               for (var j in start.salvoes[i].locations) {
                      $("#salvoTable ." + start.salvoes[i].locations[j] + "").addClass("scope");
               }
           } else {
                for (var k in start.salvoes[i].locations) {
                    var hitted = false;
                    var x = parseInt(start.salvoes[i].locations[k].slice(1))-1;
                    var y = getYPosition(start.salvoes[i].locations[k].slice(0, 1));
                    for (var j in start.ships) {
                       if(start.ships[j].locations.indexOf(start.salvoes[i].locations[k]) != -1)
                        hitted = true;
                    }
                    if(hitted)
                        $("#grid").append('<div style="position:absolute; top:'+y*45+'px; left:'+x*45+'px" class="reachFire" ></div>');
                    else
                        $("#grid").append('<div style="position:absolute; top:'+y*45+'px; left:'+x*45+'px" class="letterX" ></div>');
                }
             }
        }

}

//Función que agarra y arma un objeto para rellenar tabla de tiros
function getShoot() {
    var salvo = {
            cells: []
    }

    $("#salvoTable .scope1").each(function(){
        var getIdCell = $(this).attr('id');
        salvo.cells.push(getIdCell);
    })
    return salvo;
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



//Post list of ships to the server
$("#dinamicBtn").click(function()  {

var ships = positions();

$.post({
  url: "/api/games/players/"+getId(url)+"/ships",
  data: JSON.stringify(ships),
  dataType: "text",
  contentType: "application/json"
})
.done(function () {
  console.log("success");
  window.location.reload();
})
.fail(function () {
  $("#errorMsg").html("Ships already placed").addClass("spanColor");
  console.log("fail");
})
})

function positions() {

   var shipObject = [];

   $('.grid-stack-item').each(function() {
     var x = $(this).data('gs-x');
     var y = $(this).data('gs-y');
     var charY;
     var width = $(this).data('gs-width');
     var height = $(this).data('gs-height');
     var type = $(this).children().attr('id');
     var locations = [];

    charY = getChar(y);
   var celda = charY + (x + 1);
   locations.push(celda);

   if (width > height) {
     for (i = 1; i < width ; i++) {
           celda = charY + (x + 1 + i);
           locations.push(celda);

     }
   } else {
     for (i = 1; i < height ; i++) {
        celda = getChar(y + i) + (x+1);
        locations.push(celda);
     }
   }

    var ship = { shipType: type,
    cells: locations}

    shipObject.push(ship);
})

return shipObject;
}


//Convertir las celdas que corresponden a una letra
function getChar(y){
var charY;

     switch (y) {
      case 0:
        charY = 'A';
        break;
      case 1:
        charY = 'B';
        break;
      case 2:
        charY = 'C';
        break;
      case 3:
        charY = 'D';
        break;
      case 4:
        charY = 'E';
        break;
      case 5:
        charY = 'F';
        break;
      case 6:
        charY = 'G';
        break;
      case 7:
        charY = 'H';
        break;
      case 8:
        charY = 'I';
        break;
      case 9:
        charY = 'J';
        break;
     }
return charY;

}


//Place ships
function setListener(grid) {
/*$('.grid-stack-item').dblclick(function(){
    var w = parseInt($(this).attr('data-gs-width'))
    var h = parseInt($(this).attr('data-gs-height'))
        grid.resize($(this), h, w);*/

    $(".grid-stack-item").dblclick(function() {
       var shipContainer = $(this);
       var selectedShip = $(this).find(".grid-stack-item-content")

       var x = parseInt(shipContainer.attr("data-gs-x"));
       var y = parseInt(shipContainer.attr("data-gs-y"));
       var newHeight = parseInt(shipContainer.attr("data-gs-width"));
       var newWidth = parseInt(shipContainer.attr("data-gs-height"));

       var willFit = willItFit(x, y, newWidth, newHeight);

       if(selectedShip.hasClass("vertical")){
           areaEmpty = grid.isAreaEmpty(x + 1, y, newWidth - 1, newHeight)
       }else{
           areaEmpty = grid.isAreaEmpty(x, y + 1, newWidth, newHeight - 1)
       }

       if (willFit && areaEmpty){
           grid.resize(shipContainer, newWidth, newHeight)


        if ($(this).children().hasClass('horizontal')) {

               $(this).children().removeClass('horizontal').addClass('vertical').attr('src','imagenes css/'+$(this).children().attr('id')+'-v.icon.png');
           } else if ($(this).children().hasClass('vertical')) {
               $(this).children().removeClass('vertical').addClass('horizontal').attr('src','imagenes css/'+$(this).children().attr('id')+'-h.icon.png');
           }


       }
       else {
           alert("Caution with Grid boundaries and Ship's overlapping")
       }


       /* if ($(this).children().attr('class') === 'horizontal') {
               $(this).children().attr('class', 'vertical').attr('src','imagenes css/ship3subV.icon.png');
           } else if ($(this).children().attr('class') === ('vertical')) {
               $(this).children().attr('class', 'horizontal').attr('src','imagenes css/ship3sub.icon.png');
           }

        if ($(this).children().attr('class') === 'horizontal') {
               $(this).children().attr('class', 'vertical').attr('src','imagenes css/ship3V.icon.png');
           } else if ($(this).children().attr('class') === ('vertical')) {
               $(this).children().attr('class', 'horizontal').attr('src','imagenes css/ship3.icon.png');
           }

       if ($(this).children().attr('class') === 'horizontal') {
               $(this).children().attr('class', 'vertical').attr('src','imagenes css/ship5V.icon.png');
           }  else if ($(this).children().attr('class') === ('vertical')) {
               $(this).children().attr('class', 'horizontal').attr('src','imagenes css/ship5.icon.png');
           }

       if ($(this).children().attr('class') === 'horizontal') {
               $(this).children().attr('class', 'vertical').attr('src','imagenes css/ship4V.icon.png');
           }  else if ($(this).children().attr('class') === ('vertical')) {
               $(this).children().attr('class', 'horizontal').attr('src','imagenes css/ship4.icon.png');
           }*/




   })
}

function willItFit(x, y, width, height){
   if ((x + width) > 10){
       return false
   }
   if((y + height) > 10){
       return false
   }
   return true;
}

function placeNewShips() {
   var options = {
       //grilla de 10 x 10
       width: 10,
       height: 10,
       //separacion entre elementos (les llaman widgets)
       verticalMargin: 0,
       //altura de las celdas
       cellHeight: 45,
       cellWidth: 45,
       //desabilitando el resize de los widgets
       disableResize: true,
       //widgets flotantes
       float: true,
       //removeTimeout: 100,
       //permite que el widget ocupe mas de una columna
       disableOneColumnMode: true,
       //false permite mover, true impide
       staticGrid: false,
       //activa animaciones (cuando se suelta el elemento se ve más suave la caida)
       animate: true
   }
   //se inicializa el grid con las opciones
   $('.grid-stack').gridstack(options);
   var grid = $('#grid').data('gridstack');

       grid.addWidget($('<div><img src="imagenes css/carrier-h.icon.png" alt="ship5" id="carrier" class="grid-stack-item-content ship carrier horizontal"></div>'),
       0, 4, 5, 1, false);
       grid.addWidget($('<div><img src="imagenes css/destroyer-h.icon.png" alt="ship4" id="destroyer" class="grid-stack-item-content ship destroyer horizontal"></div>'),
       0, 3, 4, 1, false);
       grid.addWidget($('<div><img src="imagenes css/submarine-h.icon.png" alt="ship3" id="submarine" class="grid-stack-item-content ship submarine horizontal"></div>'),
       0, 2, 3, 1, false);
       grid.addWidget($('<div><img src="imagenes css/battleship-h.icon.png" id="battleship" alt="battleship" class="ship battleship grid-stack-item-content horizontal"></div>'),
       0, 1, 3, 1, false);
       grid.addWidget($('<div><img src="imagenes css/patrol-h.icon.png" alt="ship2" id="patrol" class="grid-stack-item-content ship patrolBoat horizontal"></div>'),
       0, 0, 2, 1, false);

   setListener(grid);
}

function placeShips () {
    var options = {
           //grilla de 10 x 10
           width: 10,
           height: 10,
           //separacion entre elementos (les llaman widgets)
           verticalMargin: 0,
           //altura de las celdas
           cellHeight: 45,
           cellWidth: 45,
           //desabilitando el resize de los widgets
           disableResize: true,
           //widgets flotantes
           float: true,
           //removeTimeout: 100,
           //permite que el widget ocupe mas de una columna
           disableOneColumnMode: true,
           //false permite mover, true impide
           staticGrid: true,
           //activa animaciones (cuando se suelta el elemento se ve más suave la caida)
           animate: true
       }
       //se inicializa el grid con las opciones
       $('.grid-stack').gridstack(options);
       var grid = $('#grid').data('gridstack');

       start.ships.forEach(function (ship) {

            var firstYPosition = getYPosition(ship.locations[0].charAt(0));
            var firstXPosition = (ship.locations[0].charAt(1))-1;
            var isHorizontal = (ship.locations[0].charAt(0) == ship.locations[1].charAt(0));
            var length = ship.locations.length;



            switch (ship.type) {
               case 'patrol':
                  if (isHorizontal){
                      grid.addWidget($('<div><img src="imagenes css/patrol-h.icon.png" alt="ship2" id="patrol" class="grid-stack-item-content ship patrolBoat horizontal"></div>'),
                      firstXPosition, firstYPosition, length, 1, false);
                  } else {
                      grid.addWidget($('<div><img src="imagenes css/patrol-v.icon.png" alt="ship2" id="patrol" class="grid-stack-item-content ship patrolBoat vertical"></div>'),
                      firstXPosition, firstYPosition, 1, length, false);
                  }
               break;

               case 'carrier':
                  if (isHorizontal) {
                  grid.addWidget($('<div><img src="imagenes css/carrier-h.icon.png" alt="ship5" id="carrier" class="grid-stack-item-content ship carrier horizontal"></div>'),
                  firstXPosition, firstYPosition, length, 1, false);
                  } else {
                  grid.addWidget($('<div><img src="imagenes css/carrier-v.icon.png" alt="ship5" id="carrier" class="grid-stack-item-content ship carrier vertical"></div>'),
                  firstXPosition, firstYPosition, 1, length, false);
                  }
               break

               case 'destroyer':
                  if (isHorizontal) {
                  grid.addWidget($('<div><img src="imagenes css/destroyer-h.icon.png" alt="ship4" id="destroyer" class="grid-stack-item-content ship destroyer horizontal"></div>'),
                  firstXPosition, firstYPosition, length, 1, false);
                  } else {
                  grid.addWidget($('<div><img src="imagenes css/destroyer-v.icon.png" alt="ship4" id="destroyer" class="grid-stack-item-content ship destroyer vertical"></div>'),
                  firstXPosition, firstYPosition, 1, length, false);
                  }
               break

               case 'battleship':
                  if (isHorizontal) {
                  grid.addWidget($('<div><img src="imagenes css/battleship-h.icon.png" id="battleship" alt="battleship" class="ship battleship grid-stack-item-content horizontal"></div>'),
                  firstXPosition, firstYPosition, length, 1, false);
                  } else {
                  grid.addWidget($('<div><img src="imagenes css/battleship-v.icon.png" id="battleship" alt="battleship" class="ship battleship grid-stack-item-content vertical"></div>'),
                  firstXPosition, firstYPosition, 1, length, false);
                  }
               break

               case 'submarine':
                 if (isHorizontal) {
                 grid.addWidget($('<div><img src="imagenes css/submarine-h.icon.png" alt="ship3" id="submarine" class="grid-stack-item-content ship submarine horizontal"></div>'),
                 firstXPosition, firstYPosition, length, 1, false);
                 } else {
                 grid.addWidget($('<div><img src="imagenes css/submarine-v.icon.png" alt="ship3" id="submarine" class="grid-stack-item-content ship submarine vertical"></div>'),
                 firstXPosition, firstYPosition, 1, length, false);
                 }
               break
            }
       });



}


function getYPosition(y) {

            var charY;

                 switch (y) {
                  case 'A':
                    charY = 0;
                    break;
                  case 'B':
                    charY = 1;
                    break;
                  case 'C':
                    charY = 2;
                    break;
                  case 'D':
                    charY = 3;
                    break;
                  case 'E':
                    charY = 4;
                    break;
                  case 'F':
                    charY = 5;
                    break;
                  case 'G':
                    charY = 6;
                    break;
                  case 'H':
                    charY = 7;
                    break;
                  case 'I':
                    charY = 8;
                    break;
                  case 'J':
                    charY = 9;
                    break;
                 }
            return charY;


       }