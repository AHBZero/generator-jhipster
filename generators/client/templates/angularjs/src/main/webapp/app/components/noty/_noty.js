(function (angular, $) {
    return angular.module('notyModule', []).provider('noty', function () {

        var settings = {
            type: 'alert',
            layout: 'bottom',
            theme: 'sunset',
            timeout: 5000,
            progressBar: true,
            closeWith: ['click', 'button'],
            animation: {
                open: 'noty_effects_open',
                close: 'noty_effects_close'
            },
            id: false,
            force: false,
            killer: false,
            queue: 'global'
        };
        return {
            settings: {},
            $get: function () {
                var callNoty = function (newSettings) {
                    new Noty(angular.extend({}, settings, newSettings)).show();
                };

                return {
                    show: function (message, type) {
                        callNoty({text: message || settings.text, type: type || settings.type});
                    },

                    alert: function (message) {
                        callNoty({text: message || settings.text, type: "alert"});
                    },

                    success: function (message) {
                        callNoty({text: message || settings.text, type: "success"});
                    },

                    error: function (message) {
                        callNoty({text: message, type: "error"});
                    },

                    closeAll: function () {
                        return Noty.closeAll()
                    },
                    clearShowQueue: function () {
                        return Noty.clearQueue();
                    }.bind(this)
                }
            }

        };
    })
}(angular, jQuery));
