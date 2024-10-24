// 쿠키 설정 함수
function setCookie(name, value, days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "") + expires + "; path=/";
}

// 쿠키 가져오기 함수
function getCookie(name) {
    var value = "; " + document.cookie;
    var parts = value.split("; " + name + "=");
    if (parts.length === 2) return parts.pop().split(";").shift();
}

// local storage 설정 함수
function setLocalStorageWithExpiry(key, value, days) {
    const now = new Date();
    // 현재 시간에 유효기간(일수)을 더해 만료시간 설정
    const expiryTime = now.getTime() + days * 24 * 60 * 60 * 1000;

    const item = {
        value: value,
        expiry: expiryTime // 만료 시간 저장
    };

    localStorage.setItem(key, JSON.stringify(item)); // 객체를 JSON 문자열로 변환하여 저장
}

// local storage 가져오는 함수
function getLocalStorageWithExpiry(key) {
    const itemStr = localStorage.getItem(key); // localStorage에서 데이터를 가져옴

    // 데이터가 존재하지 않으면 null 반환
    if (!itemStr) {
        return null;
    }

    const item = JSON.parse(itemStr); // JSON 문자열을 다시 객체로 변환
    const now = new Date();

    // 만료 시간이 현재 시간보다 작으면 (즉, 만료되었으면) 데이터 삭제 후 null 반환
    if (now.getTime() > item.expiry) {
        localStorage.removeItem(key);
        return null;
    }

    return item.value; // 만료되지 않았으면 저장된 값 반환
}

function getRole() {
    return getLocalStorageWithExpiry('role');
}

function getAccessToken() {
    return getLocalStorageWithExpiry('access');
}

function getRefreshToken() {
    return getLocalStorageWithExpiry('refresh');
}
