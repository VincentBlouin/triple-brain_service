package guru.bubl.service.recaptcha;

public class RecaptchaResult {
    private Boolean isSuccess;
    private Double score;

    public static RecaptchaResult isSuccessAndScore(Boolean isSuccess, Double score) {
        return new RecaptchaResult(
                isSuccess,
                score
        );
    }

    protected RecaptchaResult(Boolean isSuccess, Double score) {
        this.isSuccess = isSuccess;
        this.score = score;
    }

    public Boolean isSuccess() {
        return this.isSuccess;
    }

    public Boolean isOk() {
        return this.isSuccess && score >= 0.5;
    }
}
