(function($) {
    $.widget("ui.floatwin", {
        options: {
            title: null,
            content: null
        },
        _create: function() {
            var self =  this, o = self.options, el = self.element;
            el.find('div.close').bind('click.floatwin', function() {
                self.hide();
            });
            self.title(o.title);
            self.content(o.content);
        },
        destroy: function() {
            var self =  this, o = self.options, el = self.element;
            el.find('div.close').unbind('.floatwin');
        },
        _calc: function() {
            var self = this, o =  self.options, el = self.element;
            var fw = el.find('div.float-block');
            var h = fw.height();
            var wh = $(window).height();
            var mtop = Math.round(wh/2.0 - h/2.0);
            fw.css('margin-top', mtop+'px');
        },
        // set title of message window
        title: function(title) {
            var self =  this, o = self.options, el = self.element;
            if (title) { o.title = title }
            if (o.title) { el.find('div.block-title span').html(o.title); }
        },
        // set content of message window
        content: function(content) {
            var self =  this, o = self.options, el = self.element;
            if (content) { o.content = content }
            if (o.content) { el.find('div.text-block p').html(o.content); }
        },
        // show window
        show: function() {
            var self = this, o =  self.options, el = self.element;
            if (el.hasClass('hidden')) {
                el.removeClass('hidden');
            }
            self._calc();
        },
        // hide window
        hide: function() {
            var self = this, o =  self.options, el = self.element;
            if (!el.hasClass('hidden')) {
                el.addClass('hidden');
            }
            self._trigger('close', null, null);
        },
        close: function() {
            this.hide();
        }
    });
})(jQuery);