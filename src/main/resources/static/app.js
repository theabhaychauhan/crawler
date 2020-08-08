var stompClient = null;

document.addEventListener("DOMContentLoaded", function() {
    connect();
});

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#articleInfo").html("");
}

function connect() {
    var socket = new SockJS('/medium-crawler');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/medium', function (response) {
            showResponse(JSON.parse(response.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendTag() {
    $("#articleInfo").html("");
    stompClient.send("/app/medium", {}, JSON.stringify({'tag': $("#tag").val(), 'page': 1}));
    $('.list-group').prepend(`<li class="list-group-item">` + $("#tag").val() + `</li>`);
    createNextTenArticles(1);
    localStorage.setItem("page","1");
}

function createNextTenArticles(page) {
    $(':button').prop('disabled', true);
    var index = (page - 1) * 10;
    for (var i=0;i<10;i++) {
        if (i==0) {
            $("#articleInfo").append(`<tr><td><div id=${index +  i}>` + "Crawling..." + "</div></td></tr>");
        } else {
            $("#articleInfo").append(`<tr><td><div id=${index +  i}>` + "Pending............." + "</div></td></tr>");
        }
    }
}

function showArticle(object) {
    var url = $(object).attr("data-url");
    localStorage.setItem("url",url.toString());
    window.open("http://localhost:8090/article.html", '_blank');
}

function showTagArticle(object) {
    var tagName = $(object).text();
    $("#tag").val(tagName);
    sendTag();
}

function showResponse(response) {
    var currentPage = parseInt(localStorage.getItem("page"));
    $(`#${(currentPage - 1) * 10 + response.index}`).html("<p class='lead'>Creator - " + "<span style='font-size: 16px' class='lead'>" + (response.medium.creator ? response.medium.creator : "Error" ) + "</span></p>");
    $(`#${(currentPage - 1) * 10 + response.index}`).append("<p class='lead'>Posted On - " + "<span style='font-size: 16px' class='lead'>" + (response.medium.date ? response.medium.date : "Error" ) + "</span></p>");
    $(`#${(currentPage - 1) * 10 + response.index}`).append("<p class='lead'>Time to Read - " + "<span style='font-size: 16px' class='lead'>" + (response.medium.timeToRead ? response.medium.timeToRead : "Error" ) + "</span></p>");
    $(`#${(currentPage - 1) * 10 + response.index}`).append("<p class='lead'>Title - " + "<span style='font-size: 16px' class='lead'>" + (response.medium.title ? response.medium.title : "Error" ) + "</span></p>");
    if (response.tags) {
        $(`#${(currentPage - 1) * 10 + response.index}`).append("<p class='lead tagsList'>Tags - </p>");
        var tagsList = response.tags.split(",");
        for (var i=0;i<tagsList.length;i++) {
            $(`#${(currentPage - 1) * 10 + response.index}`).children(".tagsList").append("<button class='btn btn-warning tagButton' disabled='true' onclick='showTagArticle(this)'>" + "<span style='font-size: 16px' class='lead'>" + tagsList[i] + "</span></button>");
        }
    }
    $(`#${(currentPage - 1) * 10 + response.index}`).append(`<button class="articleUrl btn btn-primary" onclick="showArticle(this)" data-url=${response.medium.articleLink} target="_blank">Read More</button>`);
    $(`#${(currentPage - 1) * 10 + response.index + 1}`).html("Crawling...");
    $(`#${(currentPage - 1) * 10 + response.index + 1}`).animate({opacity:0},200,"linear",function(){
        $(this).animate({opacity:1},200);
    });
    if (response.index == 9) {
        $("#next").css("display","inline");
        $(':button').prop('disabled', false);
    }
}

function getNextTen() {
    $('html,body').animate({ scrollTop: 9999 }, 'slow');
    var currentPage = localStorage.getItem("page");
    stompClient.send("/app/medium", {}, JSON.stringify({'tag': $("#tag").val(), 'page': currentPage + 1}));
    createNextTenArticles(parseInt(currentPage) + 1);
    localStorage.setItem("page",(parseInt(currentPage) + 1).toString());
}

$(function () {
    $("form").on('submit', function (e) { e.preventDefault(); });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendTag(); });
    $( "#next" ).children("button").click(function () { getNextTen(); });
});



