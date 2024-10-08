package hello.imagine.attendance.service;

import hello.imagine.attendance.model.Attendance;
import hello.imagine.attendance.repository.AttendanceRepository;
import hello.imagine.login.model.Member;
import hello.imagine.login.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public void checkAttendance(Long memberId, LocalDate date) throws Exception {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new Exception("Member not found"));
        if (attendanceRepository.existsByMemberAndDate(member, date)) {
            throw new Exception("Attendance already checked for today");
        }

        // attendance 포인트 업데이트
        Attendance attendance = new Attendance();
        attendance.setMember(member);
        attendance.setDate(date);
        attendance.setPoints(20);
        attendanceRepository.save(attendance);


        // Member 포인트 업데이트
        member.setPoints(member.getPoints() + 20);
        memberRepository.save(member);
    }

    @Override
    public List<Attendance> getMonthlyAttendance(Long memberId, int year, int month) throws Exception {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new Exception("Member not found"));
        return attendanceRepository.findByMemberAndDateBetween(
                member,
                LocalDate.of(year, month, 1),
                LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth())
        );
    }
}