$(document).ready(function(){
    $(".flexslider").flexslider({
      slideshowSpeed: 4000,
      directionNav: false
    });
    if (window.PIE) {
        $(".driver-requirements .advantages").each(function() {
            PIE.attach(this);
        });
    }
    $("#top-menu-box td").bind("mouseenter mouseleave", function(){
        if($(this).find(".sub").length) {
            $(this).toggleClass("active-sub");
        } else if(!$(this).find("img").length) {
            $(this).toggleClass("active");
        }
    });
    $(".contact-variants-vert li").bind("mouseenter mouseleave", function(){
        $(this).toggleClass("active");
    });
    $(".contact-variants-vert li").bind("click", function(){
        lnk = $(this).find("a").attr("href");
        if (lnk) { window.location = lnk; }
    });
    $(".contact-variants-horiz li").bind("mouseenter mouseleave", function(){
        $(this).toggleClass("active");
    });
    $(".contact-variants-horiz li").bind("click", function(){
        lnk = $(this).find("a").attr("href");
        if (lnk) { window.location = lnk; }
    });
    $(".popup-box").bind("click", function(){
        hs.close(this);
    });
    $("input:text, textarea").bind("click", function(){
        $(this).select();
    });
});