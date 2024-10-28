let timeSlotRegistered = new Map(); // 요일별 time-slot 개수를 저장할 Map
let selectedDay = document.querySelector('.day-btn').getAttribute('data-day'); // 선택한 요일 저장
const clazzTimes = []; // 수업 시간 데이터를 저장하는 배열

// 한글 요일을 영어로 변환하는 객체
const dayTranslationsToEng = {
    "월": "MON",
    "화": "TUE",
    "수": "WED",
    "목": "THU",
    "금": "FRI",
    "토": "SAT",
    "일": "SUN"
};
const dayTranslationsToKor = {
    "MON" : "월",
    "TUE" : "화",
    "WED" : "수",
    "THU" : "목",
    "FRI" : "금",
    "SAT" : "토",
    "SUN" : "일"
};

// 요일 선택 기능
document.querySelectorAll('.day-btn').forEach(function (button) {
    button.addEventListener('click', function () {
        document.querySelectorAll('.day-btn').forEach(function (btn) {
            btn.classList.remove('active');
        });
        button.classList.add('active');
        selectedDay = button.getAttribute('data-day'); // 선택한 요일 저장
    });
});

// Set Time 버튼 클릭 시 타임 다이얼 열기
document.getElementById('set-time-btn').addEventListener('click', function () {
    document.getElementById('time-dial').style.display = 'block'; // 타임 다이얼 표시
});

// 시간 저장 버튼 클릭 시 시간 슬롯 추가
document.getElementById('save-time').addEventListener('click', function () {
    const startTime = document.getElementById('start-time').value;
    const endTime = document.getElementById('end-time').value;
    addTimeSlot(selectedDay, startTime, endTime);
});

// time-slot 생성 메서드
function addTimeSlot(selectedDay, startTime, endTime) {
    if (startTime && endTime) {
        // 시간 슬롯을 추가할 새로운 div 생성
        const timeSlot = document.createElement('div');
        timeSlot.className = 'time-slot';
        timeSlot.setAttribute('day-value', selectedDay);
        timeSlot.innerHTML = `
                                        <div class="day-value" style="font-size: 20px;"> ${selectedDay} </div>
                                        <div style="display: flex; align-items: center; justify-content: center; width: 100%; position: relative;">
                                            <div style="text-align: center; margin-right: 10px;">
                                                <span style="font-size: 12px;">시작</span><br>
                                                <span style="font-size: 20px;">${startTime}</span>
                                            </div>
                                            <div style="font-size: 20px; margin: 0 15px;"> - </div>
                                            <div style="text-align: center; margin-left: 10px;">
                                                <span style="font-size: 12px;">종료</span><br>
                                                <span style="font-size: 20px;">${endTime}</span>
                                            </div>
                                            <button class="btn btn-danger delete-btn" style="position: absolute; right: 10px;" type="button">삭제</button>
                                        </div>`;

        document.getElementById('time-slot-list').appendChild(timeSlot); // 시간 슬롯을 리스트에 추가
        document.getElementById('time-dial').style.display = 'none'; // 타임 다이얼 숨기기

        // 선택된 요일의 time-slot 개수를 증가시킴
        if (!timeSlotRegistered.has(selectedDay)) {
            timeSlotRegistered.set(selectedDay, 0); // 요일이 처음 등록될 경우 초기화

            // date-time = selectedDay 일 때 remove none
            document.querySelector('.day-btn.active').classList.remove('none');
        }
        timeSlotRegistered.set(selectedDay, timeSlotRegistered.get(selectedDay) + 1);

        // 배열에 시간 데이터를 추가
        const timeData = {
            clazzDate: dayTranslationsToEng[selectedDay],
            startTime: startTime,
            endTime: endTime
        };
        clazzTimes.push(timeData);

        // 삭제 버튼 클릭 이벤트 개별 등록
        timeSlot.querySelector('.delete-btn').addEventListener('click', function () {
            // timeSlot 내부의 요일을 가져옴
            const dayValue = timeSlot.querySelector('.day-value').textContent.trim();

            // 선택된 요일의 time-slot 개수를 감소시킴
            timeSlotRegistered.set(dayValue, timeSlotRegistered.get(dayValue) - 1);

            // 남은 time-slot 개수가 0이면 다시 none 클래스 추가
            if (timeSlotRegistered.get(dayValue) === 0) {
                document.querySelector(`.day-btn[data-day="${dayValue}"]`).classList.add('none');
            }

            // clazzTimes 배열에서 해당 시간 데이터를 제거
            const index = clazzTimes.findIndex(slot => slot.clazzDate === dayTranslationsToEng[dayValue] && slot.startTime === startTime && slot.endTime === endTime);
            if (index !== -1) {
                clazzTimes.splice(index, 1); // 배열에서 시간 데이터를 제거
            }

            timeSlot.classList.add('delete'); // 삭제 애니메이션
            setTimeout(() => {
                timeSlot.remove();
            }, 500); // 애니메이션 후 실제 삭제
        });
    }
}

// role에 따른 닉네임 검색 표시
const role = getRole();
// 역할에 따라 특정 필드를 숨기기
if (role === 'TEACHER') document.getElementById('teacher-nickname-section').style.display = 'none';
else if (role === 'PARENT') document.getElementById('parent-nickname-section').style.display = 'none';
else if (role === 'STUDENT') document.getElementById('student-nickname-section').style.display = 'none';

function searchMembers(nickname, role, resultElementId, hiddenInputId) {
    fetch(`/members?nickname=${nickname}&role=${role}`)
        .then(response => response.json())
        .then(data => {
            const resultContainer = document.getElementById(resultElementId);
            resultContainer.innerHTML = ''; // 기존 결과 초기화

            if (data.length === 0) {
                // 조회된 멤버가 없을 때
                const noResultDiv = document.createElement('div');
                noResultDiv.textContent = '조회된 데이터가 없습니다.';
                noResultDiv.style.color = 'black'; // 빨간색으로 표시
                resultContainer.appendChild(noResultDiv);
            } else {
                // 멤버 목록 생성
                data.forEach(member => {
                    const div = document.createElement('div');
                    div.classList.add('member-item');
                    div.setAttribute('data-id', member.id);
                    // div.textContent = member.nickname + '#' + '000' + member.id;
                    // nickname 부분을 검정색으로
                    const nicknameSpan = document.createElement('span');
                    nicknameSpan.textContent = member.nickname;
                    nicknameSpan.style.color = 'black';

                    // #과 id 부분을 회색으로
                    const idSpan = document.createElement('span');
                    idSpan.textContent = '    #' + String(member.id).padStart(4, '0');
                    idSpan.style.color = 'gray';

                    // nickname과 id를 하나의 div에 추가
                    div.appendChild(nicknameSpan);
                    div.appendChild(idSpan);

                    // 클릭했을 떄
                    div.addEventListener('click', function () {
                        resultContainer.innerHTML = '';
                        div.classList.add('selected');
                        resultContainer.appendChild(div);

                        // 선택된 멤버의 ID를 숨겨진 input 필드에 저장
                        document.getElementById(hiddenInputId).value = member.id;
                    });

                    resultContainer.appendChild(div);
                });
            }

            resultContainer.style.display = 'block';
        }).catch(error => console.error("Error:", error));
}

// 선생님 조회 버튼 이벤트
document.getElementById("search-teacher").addEventListener("click", function(event) {
    event.preventDefault(); // 폼 제출을 막음
    const nickname = document.getElementById("teacher-nickname").value;
    searchMembers(nickname, 'TEACHER', 'teacher-result', 'teacher-id'); // teacher-id 필드에 저장
});

// 학부모 조회 버튼 이벤트
document.getElementById("search-parent").addEventListener("click", function(event) {
    event.preventDefault(); // 폼 제출을 막음
    const nickname = document.getElementById("parent-nickname").value;
    searchMembers(nickname, 'PARENT', 'parent-result', 'parent-id'); // parent-id 필드에 저장
});

// 학생 조회 버튼 이벤트
document.getElementById("search-student").addEventListener("click", function(event) {
    event.preventDefault(); // 폼 제출을 막음
    const nickname = document.getElementById("student-nickname").value;
    searchMembers(nickname, 'STUDENT', 'student-result', 'student-id'); // student-id 필드에 저장
});

// 클래스를 가져와 폼에 데이터를 채워넣는 함수
function populateFormWithClassData() {
    // 클래스 정보가 있는 경우 필드에 값 채우기
    if (clazz) {
        document.getElementById('class-name').value = clazz.title || ''; // title
        document.querySelector('input[name="subject"]').value = clazz.subject || ''; // subject

        // 진행 상태 라디오 버튼 선택
        document.querySelector(`input[name="isDone-radio"][value="${clazz.isDone}"]`).checked = true;

        // 정산 타입 선택 및 해당 값 설정
        if('round' in clazz.responsePay) { // 회차
            document.querySelector(`input[name="postPay-radio"][value="회차"]`).checked = true;
            document.querySelector('input[name="postPay-amount"]').value = clazz.responsePay.round || '';
        } else { // 지정일
            document.querySelector(`input[name="postPay-radio"][value="지정일"]`).checked = true;
            document.querySelector('input[name="postPay-amount"]').value = clazz.responsePay.date || '';
        }

        document.querySelector('input[name="amount"]').value = clazz.responsePay.amount || ''; // 급여

        // ids 채우기
        document.getElementById('teacher-id').value = clazz.ids.teacherId || ''; // teacherId
        document.getElementById('parent-id').value = clazz.ids.parentId || '';   // parentId
        document.getElementById('student-id').value = clazz.ids.studentId || ''; // studentId

        clazz.clazzTimes.forEach(time => {
            addTimeSlot(dayTranslationsToKor[time.clazzDate], time.startTime, time.endTime);
        });
    } else {
        console.warn("클래스 데이터가 존재하지 않습니다.");
    }
}

// 페이지 로드 시 함수 호출로 데이터를 자동으로 폼에 채워넣기
document.addEventListener('DOMContentLoaded', populateFormWithClassData);