let socket = null;
let stompClient = null;

const scrollBackLength = 1000;

const commandHistory = [];
const commandHistoryMax = 50;
let commandHistoryIndex = -1;

let reconnectDelay = 2;
let isReconnecting = false;

const faviconDefault = '/img/favicon.ico';
const faviconNotify = '/img/favicon-green.ico';
let ownMessage = false;

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
            showOutput(["[green]Connected to server."]);
            reconnectDelay = 2;

            stompClient.subscribe('/user/queue/output', function(message) {
                let msg = JSON.parse(message.body);
                showOutput(msg.output);

                if (ownMessage) {
                    ownMessage = false;
                } else {
                    setFavicon(faviconNotify);
                }
            },
            {});
        },
        function() {
            const delay = Math.random() * reconnectDelay;

            if (!isReconnecting) {
                console.log('Disconnected.');
                showOutput([`[red]Disconnected from server. Reconnecting in ${delay.toFixed(0)} seconds.`]);

                isReconnecting = true;

                setTimeout(function() {
                    showOutput(["[dyellow]Reconnecting to server..."]);

                    isReconnecting = false;

                    this.connect();
                }, delay * 1000);
            }

            reconnectDelay = Math.min(reconnectDelay * 2, 120);
        }
    );
}

function sendInput() {
    const inputBox = $("form input");

    commandHistoryIndex = -1;
    commandHistory.unshift(inputBox.val());

    if (commandHistory.length > commandHistoryMax) {
        commandHistory.pop();
    }

    $("#output-list").find("li:last-child").append("<span class='yellow'> " + htmlEscape(inputBox.val()).replace(/\s/g, '&nbsp;') + "</span>");

    stompClient.send("/app/input", JSON.stringify({'input': inputBox.val()}));
    inputBox.val('');

    setFavicon(faviconDefault);
    ownMessage = true;
}

function showOutput(message) {
    const outputBox = $("#output-box");
    const outputList = $("#output-list");

    for (let i = 0; i < message.length; i++) {
        if ("" === message[i]) {
            outputList.append("<li>&nbsp;</li>");
        } else {
            outputList.append("<li>" + replaceColors(message[i]) + "</li>");
        }
    }

    // scroll to bottom
    outputBox.prop("scrollTop", outputBox.prop("scrollHeight"));

    // cut off the top output when it gets too big
    const scrollBackOverflow = outputList.find("li").length - scrollBackLength;

    if (scrollBackOverflow > 0) {
        outputList.find("li").slice(0, scrollBackOverflow).remove();
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

function setFavicon(filename) {
    let link = $("link[rel='icon']");

    if (!link) {
        link = $("head").append(`<link rel='icon' href='${filename}'`);
    }

    link.attr("href", filename);
}
