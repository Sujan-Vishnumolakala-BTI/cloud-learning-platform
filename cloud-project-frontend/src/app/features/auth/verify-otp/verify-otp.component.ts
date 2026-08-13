import {
    Component,
    OnDestroy,
    OnInit,
    ViewChild,
    signal,
} from '@angular/core';

import {
    Router,
    RouterLink,
} from '@angular/router';

import {
    AuthService
} from '../../../core/services/auth.service';

import {
    OtpInputComponent
} from '../../../shared/components/otp-input/otp-input.component';


@Component({
    selector: 'app-verify-otp',

    standalone: true,

    imports: [
        RouterLink,
        OtpInputComponent,
    ],

    templateUrl:
        './verify-otp.component.html',
})
export class VerifyOtpComponent
    implements OnInit, OnDestroy {


    // -----------------------------------------
    // OTP INPUT COMPONENT
    // -----------------------------------------

    @ViewChild(OtpInputComponent)
    otpInput!: OtpInputComponent;


    // -----------------------------------------
    // PAGE STATE
    // -----------------------------------------

    readonly email =
        signal<string | null>(null);

    readonly code =
        signal('');

    readonly loading =
        signal(false);

    readonly hasError =
        signal(false);

    readonly errorMessage =
        signal<string | null>(null);

    readonly verified =
        signal(false);

    readonly resending =
        signal(false);

    readonly countdown =
        signal(30);


    // -----------------------------------------
    // TIMER
    // -----------------------------------------

    private timer:
        ReturnType<typeof setInterval> | null =
        null;


    // -----------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------

    constructor(
        private auth: AuthService,
        private router: Router
    ) {}


    // -----------------------------------------
    // INIT
    // -----------------------------------------

    ngOnInit(): void {

        /*
         * Get email saved during registration.
         */

        const pendingEmail =
            this.auth.getPendingEmail();

        this.email.set(
            pendingEmail
        );


        /*
         * No pending registration.
         */

        if (!pendingEmail) {

            this.errorMessage.set(
                'No pending registration found. Please register again.'
            );

            this.hasError.set(true);

            return;
        }


        /*
         * Start resend countdown.
         */

        this.startCountdown();
    }


    // -----------------------------------------
    // DESTROY
    // -----------------------------------------

    ngOnDestroy(): void {

        if (this.timer) {

            clearInterval(
                this.timer
            );

            this.timer = null;
        }
    }


    // -----------------------------------------
    // COUNTDOWN
    // -----------------------------------------

    private startCountdown(): void {

        this.countdown.set(30);


        if (this.timer) {

            clearInterval(
                this.timer
            );
        }


        this.timer =
            setInterval(() => {

                this.countdown.update(
                    value =>
                        value > 0
                            ? value - 1
                            : 0
                );


                if (
                    this.countdown() === 0 &&
                    this.timer
                ) {

                    clearInterval(
                        this.timer
                    );

                    this.timer = null;
                }

            }, 1000);
    }


    // -----------------------------------------
    // OTP INPUT CHANGE
    // -----------------------------------------

    onCodeChange(
        code: string
    ): void {

        this.code.set(code);

        this.hasError.set(false);

        this.errorMessage.set(null);
    }


    // -----------------------------------------
    // OTP COMPLETED
    // -----------------------------------------

    onCompleted(
        code: string
    ): void {

        /*
         * Automatically verify once
         * all six digits are entered.
         */

        this.verify(code);
    }


    // -----------------------------------------
    // VERIFY OTP
    // -----------------------------------------

    verify(
        code = this.code()
    ): void {

        /*
         * Prevent duplicate requests.
         */

        if (this.loading()) {
            return;
        }


        /*
         * Validate OTP.
         */

        if (
            !code ||
            code.length !== 6
        ) {

            this.hasError.set(true);

            this.errorMessage.set(
                'Enter the complete 6-digit code.'
            );

            return;
        }


        /*
         * Start loading.
         */

        this.loading.set(true);

        this.hasError.set(false);

        this.errorMessage.set(null);


        /*
         * REAL BACKEND OTP VERIFICATION
         */

        this.auth
            .verifyOtp(code)
            .subscribe({

                // -----------------------------------
                // SUCCESS
                // -----------------------------------

                next: response => {

                    console.log(
                        'OTP VERIFICATION SUCCESS:',
                        response
                    );


                    this.loading.set(false);

                    this.verified.set(true);

                    this.hasError.set(false);

                    this.errorMessage.set(null);


                    /*
                     * OTP verification is complete.
                     *
                     * At this point the backend has
                     * verified the user's email.
                     *
                     * Send user to login rather than
                     * assuming they are authenticated.
                     */

                    setTimeout(() => {

                        this.router.navigate([
                            '/login'
                        ]);

                    }, 1000);
                },


                // -----------------------------------
                // ERROR
                // -----------------------------------

                error: err => {

                    console.error(
                        'OTP VERIFICATION ERROR:',
                        err
                    );


                    this.loading.set(false);

                    this.hasError.set(true);


                    const message =
                        err?.error?.message ??
                        err?.message ??
                        'Incorrect or expired OTP. Please try again.';


                    this.errorMessage.set(
                        message
                    );


                    /*
                     * Clear OTP boxes.
                     */

                    this.otpInput?.reset();
                },
            });
    }


    // -----------------------------------------
    // RESEND OTP
    // -----------------------------------------

    resend(): void {

        /*
         * Don't resend while countdown
         * is active.
         */

        if (
            this.countdown() > 0 ||
            this.resending()
        ) {
            return;
        }


        this.resending.set(true);

        this.hasError.set(false);

        this.errorMessage.set(null);


        /*
         * REAL BACKEND RESEND REQUEST
         */

        this.auth
            .resendOtp()
            .subscribe({

                // -----------------------------------
                // SUCCESS
                // -----------------------------------

                next: response => {

                    console.log(
                        'OTP RESEND SUCCESS:',
                        response
                    );


                    this.resending.set(false);


                    /*
                     * Clear old OTP.
                     */

                    this.code.set('');

                    this.otpInput?.reset();


                    /*
                     * Start new 30-second
                     * resend countdown.
                     */

                    this.startCountdown();
                },


                // -----------------------------------
                // ERROR
                // -----------------------------------

                error: err => {

                    console.error(
                        'OTP RESEND ERROR:',
                        err
                    );


                    this.resending.set(false);

                    this.hasError.set(true);


                    const message =
                        err?.error?.message ??
                        err?.message ??
                        'Unable to resend OTP. Please try again.';


                    this.errorMessage.set(
                        message
                    );
                },
            });
    }
}