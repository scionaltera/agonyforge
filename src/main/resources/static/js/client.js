let socket = null;
let stompClient = null;
const commandHistory = [];
const commandHistoryMax = 50;
let commandHistoryIndex = -1;

$(document).ready(function() {
    $("form").submit(function(event) {
        sendInput();
        event.preventDefault();
        return false;
    });

    connect();
});

$(document).keydown(function(event) {
    if (event.which === 9) { // don't tab away from input
        event.preventDefault();
        return false;
    }
});

$(document).keyup(function(event) {
    const inputBox = $("form input");

    if (event.which === 38) { // up arrow - command history prev
        commandHistoryIndex++;

        if (commandHistoryIndex >= commandHistory.length) {
            commandHistoryIndex = commandHistory.length - 1;
        }

        if (commandHistoryIndex >= 0) {
            inputBox.val(commandHistory[commandHistoryIndex]);
        }
    } else if (event.which === 40) { // down arrow - command history next
        commandHistoryIndex--;

        if (commandHistoryIndex < 0) {
            commandHistoryIndex = -1;
        }

        if (commandHistoryIndex >= 0) {
            inputBox.val(commandHistory[commandHistoryIndex]);
        } else {
            inputBox.val("");
        }
    }
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

    commandHistoryIndex = -1;
    commandHistory.unshift(inputBox.val());

    if (commandHistory.length > commandHistoryMax) {
        commandHistory.pop();
    }

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
