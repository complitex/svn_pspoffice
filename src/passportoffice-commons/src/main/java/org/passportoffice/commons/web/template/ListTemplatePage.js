$(document).ready(function() {
    $("form input, form select").live('keypress', function (e) {
        if ($(this).parents('form').find('button[type=submit].default, input[type=submit].default').length <= 0)
            return true;
        if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
            $(this).parents('form').find('button[type=submit].default, input[type=submit].default').click();
            return false;
        } else {
            return true;
        }
    });
});       