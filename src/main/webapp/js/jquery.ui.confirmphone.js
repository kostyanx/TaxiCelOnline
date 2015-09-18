(function($){
    $.widget("ui.confirmphone", {
        options: {
            name: null,
            data: null,
            confirmwin: null
        },
        _create: function() {
            var self = this, o = self.options, el = self.element;
            o.name = $(o.name);
            o.data = $(o.data);
            o.confirmwin = $(o.confirmwin);
            var client = o.data.data('client');
            if (client) {
                o.name.val(client.name);
                el.val(client.phone);
            }
            el.data('phone', el.val());
            console.log('confirmphone create');
        },
        requireConfirm: function() {
            var self = this, o = self.options, el = self.element;
            return (!el.val() || ($.trim(el.val()) === '') || el.val() !== el.data('phone'));
        },
        confirm: function() {
            var self = this, o = self.options, el = self.element;
            el.data('phone', el.val());
        },
        destroy: function() {
            
        }
    });
})(jQuery);
