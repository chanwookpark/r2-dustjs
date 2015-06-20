package r2.common;

/**
 * R2 공통 예외 클래스
 *
 * @author chanwook
 */
public class R2Exception extends RuntimeException {
    public R2Exception(String msg, Throwable cause) {
        super(msg, cause);
    }

    public R2Exception(String msg) {
        super(msg);
    }
}
