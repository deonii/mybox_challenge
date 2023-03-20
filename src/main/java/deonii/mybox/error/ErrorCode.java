package deonii.mybox.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    ALREADY_EXISTS_EMAIL(400, "이미 저장된 email 입니다."),
    FILE_DOES_NOT_EXIST(400, "파일이 존재하지 않습니다."),
    ALREADY_EXISTS_FILE(400, "동일한 파일이 존재합니다."),
    NOT_ENOUGH_VOLUME(400, "용량이 부족합니다."),
    NOT_EXISTS_EMAIL(400, "존재하지 않는 계정입니다."),
    NOT_EXISTS_UUID(400, "존재하지 않는 계정입니다."),
    NOT_CORRECT_PASSWORD(400, "비밀번호를 확인해 주세요."),
    ALREADY_EXISTS_FOLDER(400, "이미 저장된 folder 입니다."),
    NOT_EXISTS_FOLDER(400, "존재하지 않는 folder 입니다."),
    NOT_AUTHORIZATION(401, "로그인이 필요합니다.");
//    INTERNAL_SERVER_ERROR(500, "서버 에러입니다.");

    private final int status;
    private final String message;
}
