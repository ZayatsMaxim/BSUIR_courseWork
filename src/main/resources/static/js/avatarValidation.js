function getFileExtension(filename){
    // get file extension
    return filename.substring(filename.lastIndexOf('.') + 1, filename.length);
}
function checkIfFileIsSelected(){

    if (document.getElementById("updateAvatar").value === ""){
        alert("Вы не выбрали аватар для загрузки!");
    }
}