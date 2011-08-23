$(function(){
    var $allSelected = $(".allSelected");
    var $table = $allSelected.closest("table");
    var $allCheckboxes = $table.find(".selected");

    $allSelected.click(function(){
        $allCheckboxes.attr("checked", $allSelected.is(":checked"));
    });

    $allCheckboxes.click(function(){
        $allSelected.attr("checked", false);
    });
});