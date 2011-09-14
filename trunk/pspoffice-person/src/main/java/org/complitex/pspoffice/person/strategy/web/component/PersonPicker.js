$(function(){
    $(".person-picker-search-form .person-picker-search-button").live("click", function(){
        var searchForm = $(this).closest(".person-picker-search-form");
        searchForm.find(".ui-autocomplete-input").autocomplete("close");
    });
    $(".person-picker-search-form .ui-autocomplete-input")
        .live("keyup", function(event){
            if(event.which == $.ui.keyCode.ENTER){
                $(this).closest(".person-picker-search-form").find(".person-picker-search-button").click();
            }
        })
        .live("autocompleteselect", function(){
            var input = $(this);
            setTimeout(function(){
                var nextAutocompleter = input.closest("td").next("td").find(".ui-autocomplete-input");
                if(nextAutocompleter.size() == 1){
                    nextAutocompleter.focus();
                } else if(nextAutocompleter.size() == 0){
                    input.closest(".person-picker-search-form").find(".person-picker-search-button").click();
                }
            }, 200);
        });
});