# 📚 StudyPro
<img src="src/main/resources/static/images/logo-static.png" alt="logo" width="200"/>

**과외 수업 관리 플랫폼**으로, 과외 선생님과 학부모 간의 소통 및 급여 관리 문제를 해결합니다. 과외 선생님들이 학생의 성적 향상도와 수업 내용을 효율적으로 관리하고 학부모와 원활하게 소통할 수 있도록 돕는 플랫폼입니다.

---

## 📌 프로젝트 개요

### 목표
- 과외 선생님과 학부모 간의 비효율적인 급여 시스템 개선
- 수업 내용 및 성적 향상도를 실시간으로 확인 가능하게 지원
- 수업 진행 후 즉각적인 내용 공유를 통해 학부모와의 소통 효율성 강화

---

## 🚀 배포
https://studypro.fly.dev/auth/login

## 🛠️ Architecture
아래 다이어그램은 StudyPro의 전체 시스템 아키텍처를 설명합니다.

![Architecture](%3CmxGraphModel%3E%3Croot%3E%3CmxCell%20id%3D%220%22%2F%3E%3CmxCell%20id%3D%221%22%20parent%3D%220%22%2F%3E%3CmxCell%20id%3D%222%22%20value%3D%22%22%20style%3D%22shape%3Dimage%3BverticalLabelPosition%3Dbottom%3BlabelBackgroundColor%3D%23ffffff%3BverticalAlign%3Dtop%3Baspect%3Dfixed%3BimageAspect%3D0%3Bimage%3Dhttps%3A%2F%2Fsimplecore.intel.com%2Fintel-capital%2Fwp-content%2Fuploads%2Fsites%2F99%2FFly.io-logo_1536x600.jpg%3B%22%20vertex%3D%221%22%20parent%3D%221%22%3E%3CmxGeometry%20x%3D%22578.0699999999999%22%20y%3D%22200%22%20width%3D%22172.9%22%20height%3D%22113%22%20as%3D%22geometry%22%2F%3E%3C%2FmxCell%3E%3C%2Froot%3E%3C%2FmxGraphModel%3E.png)

## 🗂️ Entity Relationship Diagram (ERD)
데이터베이스 구조는 아래와 같습니다.

![studypro_erd.png](..%2F..%2F..%2FDownloads%2Fstudypro_erd.png)

## 🔧 기술 스택

- **Backend**: SpringBoot, PostgreSQL
- **Frontend**: Thymeleaf
- **CI/CD**: GitHub Actions
- **Deploy**: AWS EC2, Fly.io, Docker
- **ETC**: FCM, Kakao login

---

## 🔍 문제 정의

### 1. 급여 관리 문제
- 과외 특성상 선생님이 회차별 수업 내용을 기록하고 봉급을 수동 계산
- 수업 완료 시 학부모에게 문자로 통지하는 방식으로, 시간과 정성 소모
- 매달 변동되는 급여 날짜로 인해 학부모가 확인하기 어려움

### 2. 수업 내용 확인 및 소통 문제
- 다양한 과외 장소 및 복잡한 일정으로 인해 학부모의 수업 내용 문의가 불편함
- 종이에 작성된 수업 기록은 관리가 어려워 지난 수업 내용 확인이 불편함
- 학생의 성적 향상도를 한눈에 파악하기 어려움

---

## 🌟 기대 효과

- 과외 선생님의 시간과 에너지를 절감
- 수업 및 급여 관리를 자동화하여 학부모와의 금전적 대화 감소
- 수업 진행 현황을 실시간으로 확인하고 피드백을 반영하여 수업 품질 향상

## ⚓ 핵심 기능

1. **과외 수업 기록 및 급여 관리**
    - 과외 수업이 끝날 때마다 간단한 메모(진도, 숙제 등)와 시간을 기록
    - 회차별 봉급을 자동 계산하고, 정산 완료 시 학부모에게 알림 전송

2. **간편한 UI/UX**
    - 다수의 학생과 선생님을 효율적으로 관리할 수 있는 사용자 친화적인 인터페이스 제공

## 💰 수익 모델

- 광고 배너를 통한 수익 창출
- 초기에는 저비용 서버(라즈베리파이) 사용으로 고정 비용 절감
- 플랫폼이 성장하면 출판사와의 제휴 광고를 통해 추가 수익 기대

## 📈 향후 계획

1. 사용자 피드백을 반영한 지속적인 개선
2. 학생의 성적을 입력하면 그래프로 시각화
3. 앱 출시
4. 모바일에 맞는 화면 구현
5. 백엔드 성능 개선 & 서버 비용 절감

---

### 💡 건의사항
https://forms.gle/Wpr4v9h96iKq1nWs7