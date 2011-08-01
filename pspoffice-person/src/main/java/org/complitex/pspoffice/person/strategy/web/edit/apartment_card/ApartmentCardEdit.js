$(document).ready(function(){
    $(".allSelected:checkbox").bind("click", function(){
        var $allSelected = $(this);
        var $table = $allSelected.closest("table");
        $table.find(".selected:checkbox:enabled:").attr("checked", $allSelected.is(":checked"));
    });
});