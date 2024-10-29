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

function getClassId() {
    return getLocalStorageWithExpiry('classId');
}

async function fetchWithAuth(url, options = {}) {
    try {
        const accessToken = getAccessToken();
        const response = await fetch(url, {
            ...options,
            headers: {
                ...options.headers,
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${accessToken}`
            }
        });

        if (response.status === 401) {  // 토큰 만료
            const refreshToken = getRefreshToken(); // Refresh Token 가져오기
            const refreshResponse = await fetch('/auth/refresh', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ access: accessToken, refresh: refreshToken })
            });

            // 새로운 access 토큰 재발행
            if (refreshResponse.ok) {
                const { access, refresh } = await refreshResponse.json();
                setLocalStorageWithExpiry("access", access, 1); // 새 accessToken 저장
                setLocalStorageWithExpiry("refresh", refresh, 1); // 새 refreshToken 저장

                // 원래 요청을 재시도
                return await fetch(url, {
                    ...options,
                    headers: {
                        ...options.headers,
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${access}`
                    }
                });
            } else { // Refresh token이 만료되었을 때
                alert('세션이 만료되었습니다. 다시 로그인해 주세요.');
                localStorage.clear(); // 토큰 정보 삭제
                window.location.href = '/auth/login'; // 로그인 페이지로 이동
                return; // 이후 코드 실행 중지
            }
        }
        return response;
    } catch (error) {
        console.error('Error in fetchWithAuth:', error);
        throw error;
    }
}