// constants
const SERVER_HOST = "localhost:8080"
const SCHEMA = "http"
const BASE_PATH = `${SCHEMA}://${SERVER_HOST}`

async function authorizeTeacherForContentDelete(contentId) {
    const password = prompt("Для удаления материала введите свой пароль: ", '');
    alert(contentId);

    const response = await fetch(`${BASE_PATH}/zayct/courses/user/teacher/deleteContent/${contentId}`, {
        method: "GET",
        headers : {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ "password": password })
    })

}

function askTeacherToContinue(event){

    let submitConfirm = confirm("Удаление материала приведет к удалению работ студентов, свззанных с этим файлом! " +
        "Действительно хотите продолжить?");
    if (submitConfirm !==true) {
        event.preventDefault();
    }
}

async function sendFormData(formId, idFieldName, event) {
    try {
        event.preventDefault();

        const submitConfirm = confirm(
            "Удаление материала может привести к удалению работ студентов, свззанных с этим файлом! " +
            "Действительно хотите продолжить?"
        );

        if (submitConfirm === false) {
            return false;
        }

        const form = document.getElementById(formId);
        const contentId = form.getAttribute(idFieldName);

        const dataToSend = new URLSearchParams();
        const formData = new FormData(form);

        for (const [key, value] of formData) {
            dataToSend.append(key, value.toString());
        }

        const response = await fetch(`${BASE_PATH}/zayct/courses/user/teacher/deleteContent/${contentId}`,{
            method: "POST",
            body: dataToSend,
        });

        switch (response.status) {
            case 200:
                window.location.replace(`${BASE_PATH}/zayct/courses/user/myCourses/page/1`);
                break;
            case 401:
                alert("Вы ввели неправильный пароль, попробуйте снова!");
                break;
            default:
                throw new Error(`Bad server response. Status code: ${response.status}, message: ${response.statusText}`);
        }
        return true;

       /* if (response.status !== 200) {
            throw new Error(`Bad server response. Status code: ${response.status}, message: ${response.statusText}`);
        }

        if (response.status === 401) {
            alert("Вы ввели неправильный пароль, попробуйте снова!");
            return false;
        }
        window.location.replace(`${BASE_PATH}/zayct/courses/user/myCourses/page/1`);
        return true;*/
    } catch (error) {
        console.error(`Form data send error. Message: ${error.message}, stack: ${error.stack}`);
        return false;
    }
}
