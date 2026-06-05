  'use strict'

  //音楽判定
  window.onload = async function(){
    var music = new Audio("BGM.mp3");
    music.pause();
  };

  function start(){
    let ms = document.getElementById('radio').music.value;
    console.log(ms);
      jojo(ms);
  }

  function delay(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

//アニメーション
  function jojo(ms){
    if(ms === 'on'){
      window.location.href = "next.html?sound";
    }else{
      window.location.href = "next.html";
    }
  }
