window.addEventListener(
    "message",
    function (event) {
        if (event.data && event.data.event && event.data.event === 'set_title') {
//            document.getElementsByTagName('title')[0].innerHTML = event.data.value;
            window.document.title = event.data.value;
        }
    },
    false);
function iFrameLoaded() {
    setTimeout(function(){
        var iFrame = document.getElementById('fr_online_order');
        iFrame.contentWindow.postMessage('mainWindow', '*');
    }, 1000);
}