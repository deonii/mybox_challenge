package deonii.mybox.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    NOT_AUTHORIZATION(401, "로그인이 필요합니다."),
    ALREADY_EXISTS_EMAIL(400, "이미 저장된 email 입니다."),
    NOT_EXISTS_EMAIL(400, "존재하지 않는 계정입니다."),
    NOT_CORRECT_PASSWORD(400, "비밀번호를 확인해 주세요.");
//    INTERNAL_SERVER_ERROR(500, "서버 에러입니다.");

    private final int status;
    private final String message;
}
