function showToast(message, duration = 3000) {
    const toastContainer = document.getElementById('toast-container');

    // 토스트 알림 요소 생성
    const toast = document.createElement('div');
    toast.textContent = message;
    toast.style.backgroundColor = '#333';
    toast.style.color = '#fff';
    toast.style.padding = '15px 30px';
    toast.style.borderRadius = '8px';
    toast.style.marginTop = '10px';
    toast.style.boxShadow = '0px 4px 8px rgba(0, 0, 0, 0.2)';
    toast.style.opacity = '1';
    toast.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
    toast.style.textAlign = 'center'; // 텍스트 중앙 정렬

    // 토스트 컨테이너에 추가
    toastContainer.appendChild(toast);

    // 일정 시간 후에 토스트가 서서히 사라지도록 설정
    setTimeout(() => {
        toast.style.opacity = '0'; // 서서히 사라짐 효과
        toast.style.transform = 'translateY(-20px)'; // 위로 이동하며 사라지기
        setTimeout(() => toastContainer.removeChild(toast), 500); // 완전히 사라지면 DOM에서 제거
    }, duration);
}