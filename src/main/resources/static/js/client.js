let socket = null;
let stompClient = null;

$(document).ready(function() {
    $("form").submit(function(event) {
        sendInput();
        event.preventDefault();
        return false;
    });

    connect();
});

function connect() {
    socket = new SockJS('/mud');
    stompClient = webstomp.over(socket, { heartbeat: false, protocols: ['v12.stomp']});
    stompClient.connect(
        {},
        function(frame) {
            console.log('Connected: ' + frame);
            stompClient.subscribe('/user/queue/output', function(message) {
                let msg = JSON.parse(message.body);

                console.log(`Output: ${msg.output}`);
                showOutput(msg.output);
            },
            {});
        }
    );
}

function sendInput() {
    let inputBox = $("form input");

    $("#output-list").find("li:last-child").append("<span class='yellow'> " + htmlEscape(inputBox.val()).replace(/\s/g, '&nbsp;') + "</span>");

    stompClient.send("/app/input", JSON.stringify({'input': inputBox.val()}));
    inputBox.val('');
}

function showOutput(message) {
    let outputBox = $("#output-box");
    let outputList = $("#output-list");

    for (let i = 0; i < message.length; i++) {
        if ("" === message[i]) {
            outputList.append("<li>&nbsp;</li>");
        } else {
            outputList.append("<li>" + replaceColors(message[i]) + "</li>");
        }
    }
}

function replaceColors(message) {
    return String(message).replace(/\[(\w+)]/g, "<span class='$1'>");
}

function htmlEscape(str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/\//g, '&#x2F;');
}
