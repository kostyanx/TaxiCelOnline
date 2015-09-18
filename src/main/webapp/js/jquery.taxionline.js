(function($){
    $.widget("ui.taxionline", {
        options: {
            state: 0,
            order_id: null,
            client: null,
            event_receiving: false
        },
        _create: function() {
            var self = this,
                o =  self.options,
                el = self.element;
        },
        destroy: function() {
            var self = this,
                o =  self.options,
                el = self.element;
        },
        state: function(state) {
            
        }
    });
}(jQuery));

