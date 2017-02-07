package com.multipong.model;

public class MultiplayerStateManager {
    public void sendBallToNext(BallInfo ballInfo){
        // TODO: Send ball info to coordinator via net
    }

    public static BallInfo createBallInfo(double speedX, double speedY, double position) {
        return new BallInfo().withBallSpeedX(speedX)
                             .withBallSpeedY(speedY)
                             .withPosition(position);
    }

    public static class BallInfo {
        private double ballSpeedX;
        private double ballSpeedY;
        private double position;

        public double getBallSpeedX() {
            return ballSpeedX;
        }

        public BallInfo withBallSpeedX(double ballSpeedX) {
            this.ballSpeedX = ballSpeedX;
            return this;
        }

        public double getBallSpeedY() {
            return ballSpeedY;
        }

        public BallInfo withBallSpeedY(double ballSpeedY) {
            this.ballSpeedY = ballSpeedY;
            return this;
        }

        public double getPosition() {
            return position;
        }

        public BallInfo withPosition(double position) {
            this.position = position;
            return this;
        }
    }
}
