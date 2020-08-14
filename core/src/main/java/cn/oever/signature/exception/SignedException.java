package cn.oever.signature.exception;

public class SignedException {
    public static class NullParam extends BaseException {
        public NullParam(String msg) {
            super(msg);
        }
    }

    public static class AppIdInvalid extends BaseException {
        public AppIdInvalid(String msg) {
            super(msg);
        }
    }

    public static class ReplayAttack extends BaseException {
        public ReplayAttack(String arg0, long arg1, int arg2) {
            super("appId: " + arg0 + ", timestamp: " + arg1 + ", nonce: " + arg2);
        }
    }

    public static class SignatureError extends BaseException {
        public SignatureError(String msg) {
            super(msg);
        }
    }

    public static class TimestampError extends BaseException {
        public TimestampError(String msg) {
            super(msg);
        }
    }
}