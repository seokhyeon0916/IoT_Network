(function ($) {

  "use strict";

    // PRE LOADER
    $(window).load(function(){
      $('.preloader').fadeOut(1000); // set duration in brackets    
    });


    // MENU
    $('.menu-burger').on('click', function() {
      $('.menu-bg, .menu-items, .menu-burger').toggleClass('fs');
      $('.menu-burger').text() == "☰" ? $('.menu-burger').text('✕') : $('.menu-burger').text('☰');
    });


    // ABOUT SLIDER
    $('body').vegas({
        slides: [
            // { src: 'images/slide-image01.jpg' },
            // { src: 'images/slide-image02.jpg' },
            // { src: 'images/slide-image03.jpg' },
            // { src: 'images/slide-image04.jpg' },
            // { src: 'images/slide-image05.jpg' },
            { src: 'images/slide-image06.jpg' },
            {src: 'images/slide-image07.jpg' },
            { src : 'images/slide-image08.jpg'}
        ],
        timer: false,
        transition: [ 'zoomOut', ]
    });

})(jQuery);
