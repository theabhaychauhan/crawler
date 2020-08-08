document.addEventListener("DOMContentLoaded", function() {
    fetch('http://localhost:8090/medium/article' , {
        method: 'post',
        headers: {
            'Content-type': 'application/json'
        },
        body: JSON.stringify({url: localStorage.getItem("url")})
    })
        .then(function(res){ return res.json(); })
        .then(function(data){
            $('.loading').css("display","none");
            $('body').append(`<h1>Time to Crawl - ${data.time / 1000.0} seconds </h1>`);
            $('body').append(data.status);
        });
});


$(function () {
    console.log("I am here");
});