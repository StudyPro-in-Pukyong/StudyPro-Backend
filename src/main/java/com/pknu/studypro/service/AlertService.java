package com.pknu.studypro.service;

import com.pknu.studypro.domain.Alert.Alert;
import com.pknu.studypro.domain.clazz.Clazz;
import com.pknu.studypro.domain.clazz.FixedDatePay;
import com.pknu.studypro.domain.clazz.RoundPay;
import com.pknu.studypro.domain.member.Member;
import com.pknu.studypro.domain.member.Role;
import com.pknu.studypro.dto.auth.LoginUser;
import com.pknu.studypro.exception.BusinessLogicException;
import com.pknu.studypro.exception.ExceptionCode;
import com.pknu.studypro.repository.AlertRepository;
import com.pknu.studypro.util.FindMember;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class AlertService {
    private final AlertRepository alertRepository;
    private final ClazzService clazzService;
    private final FindMember findMember;

    // 알람 생성
    public Alert createAlert(LoginUser loginUser, long clazzId) {
        // 클래스
        Clazz clazz = clazzService.verifiedClazz(clazzId);

        // 선생님
        Member teacher = findMember.findMemberByToken(loginUser);
        if(teacher.getRole() != Role.TEACHER) { // 선생님이 아닌 다른 사람이 정산 요청을 한 경우
            throw new BusinessLogicException(ExceptionCode.NOT_TEACHER);
        }

        // 학부모
        Member parent = clazz.getParent();
        if(parent == null) { // 학부모가 연결되어 있지 않아서 정산 기능을 사용할 수 없는 경우
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
        }
        if(parent.getRole() != Role.PARENT) { // 학부모이 아닌 다른 사람이 정산 요청을 한 경우
            throw new BusinessLogicException(ExceptionCode.NOT_PARENT);
        }

        // 월급 계산
        LocalDate now = LocalDate.now();
        LocalDate lastSettleDate = clazz.getSettleDate(); // 마지막 정산 요청일
        LocalDate nextSettleDate = now; // 다음 정산일
        LocalDate settleDate = now; // 마지막 월급일
        int amount = clazz.getPay().getAmount(); // 월급
        if (clazz.getPay().getClass().getName().contains("FixedDatePay")) { // FixedDatePay(지정일)
            FixedDatePay pay = (FixedDatePay) clazz.getPay();

            if(lastSettleDate != null) { // 처음 정산 요청하는 것이 아닌 경우
                // 지난번에 받은 다음달
                nextSettleDate = findNextSettleDate(lastSettleDate.plusMonths(1), pay);

                // 이번달에 이미 요청을 한 경우
                if(now.isBefore(nextSettleDate))
                    throw new BusinessLogicException(ExceptionCode.ALERT_ALREADY_SETTLED);
            }

            nextSettleDate = findNextSettleDate(now, pay); // 이번달 월급일
            // 이번달 월급일 전이면 지난달 월급일로 설정
            if(now.isBefore(nextSettleDate)) nextSettleDate = findNextSettleDate(nextSettleDate.minusMonths(1), pay);

            settleDate = nextSettleDate;
            pay.setCurrentRound(0); // round 제거(결제 방식이 바뀔 경우를 위해서)
        } else { // RoundPay(지정일)
            RoundPay pay = (RoundPay) clazz.getPay();
            if(pay.getCurrentRound() <= 0) { // 월급 요청할 것이 없는 경우
                throw new BusinessLogicException(ExceptionCode.NO_CURRENT_ROUND);
            } else if(pay.getCurrentRound() < pay.getRound()) { // 정해진 횟수보다 적게 일한 경우
                amount = pay.getCurrentRound() * pay.getAmount();
                pay.setCurrentRound(0);
            } else {
                amount = pay.getRound() * pay.getAmount();
                pay.setCurrentRound(pay.getCurrentRound() - pay.getRound());
            }
            settleDate = null;
        }

        // clazz 업데이트
        clazz.setSettleDate(nextSettleDate); // 월급 요청일 업데이트
        clazzService.updateClazz(clazz);

        // 알람 생성하기
        Alert alert = new Alert(
                teacher, parent, clazz, amount, now, settleDate
        );

        return alertRepository.save(alert);
    }

    // 요청 수락
    public Alert acceptAlert(LoginUser loginUser, long alertId) {
        // 알람 검증
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ALERT_NOT_FOUND));

        // 학부모 검증
        Member parent = findMember.findMemberByToken(loginUser);
        if(!parent.getId().equals(alert.getParent().getId()) || !parent.getRole().equals(Role.PARENT)) {
            throw new BusinessLogicException(ExceptionCode.NOT_PARENT);
        }

        // 이미 수락한 경우
        if(alert.getAcceptDate() != null) {
            System.out.println("!!");
            throw new BusinessLogicException(ExceptionCode.ALERT_ALREADY_ACCEPTED);
        }

        // 알람 수락
        LocalDate now = LocalDate.now();
        alert.accept(now);

        return alertRepository.save(alert);
    }

    // id로 알람 가져오기
    public Alert verifiedAlert(long alertId) {
        return alertRepository.findById(alertId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ALERT_NOT_FOUND));
    }

    // 사용자를 이용해서 알람을 가져오기
    public List<Alert> getAlerts(LoginUser loginUser) {
        Member member = findMember.findMemberByToken(loginUser);
        if(member.getRole().equals(Role.TEACHER)) {
            return alertRepository.findAllByTeacherOrderByModifiedAtDesc(member, PageRequest.of(0, 10));
        } else if(member.getRole().equals(Role.PARENT)) {
            return alertRepository.findAllByParentOrderByModifiedAtDesc(member, PageRequest.of(0, 10));
        } else {
            throw new BusinessLogicException(ExceptionCode.NOT_STUDENT);
        }
    }

    public LocalDate findNextSettleDate(LocalDate localDate, FixedDatePay pay) {
        // 다음 월급 요청 가능일
        if(localDate.lengthOfMonth() < pay.getDate()) // 지정일이 이번달에 없는 경우
            localDate = localDate.withDayOfMonth(localDate.lengthOfMonth());
        else localDate = localDate.withDayOfMonth(pay.getDate());
        return localDate;
    }
}
