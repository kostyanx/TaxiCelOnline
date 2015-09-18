$(function(){
    $('#tx_typeahead').typeahead({
        name: 'twitter-oss',
        prefetch: '../repos.json',
        template: [
            '<p class="repo-language">{{language}}</p>',
            '<p class="repo-name">{{name}}</p>',
            '<p class="repo-description">{{description}}</p>'
        ].join(''),
        engine: Hogan
    });
});