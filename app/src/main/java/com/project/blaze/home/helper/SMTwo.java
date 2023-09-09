package com.project.blaze.home.helper;

import static com.project.blaze.home.presentation.ReviewFragment.AGAIN;
import static com.project.blaze.home.presentation.ReviewFragment.EASY;
import static com.project.blaze.home.presentation.ReviewFragment.GOOD;
import static com.project.blaze.home.presentation.ReviewFragment.HARD;

public class SMTwo {
    private String learningTag;
    private long timeMinutes;
    private  double easinessFactor;

    public SMTwo() {
    }

    public static class ReviewResult {
        private final boolean graduated;
        private final long timeMinutes;
        private final double easinessFactor;

        public ReviewResult(boolean graduated, long timeMinutes, double easinessFactor) {
            this.graduated = graduated;
            this.timeMinutes = timeMinutes;
            this.easinessFactor = easinessFactor;
        }

        public boolean isGraduated() { return  graduated; }
        public long getTimeMinutes() {
            return timeMinutes;
        }
        public double getEasinessFactor() { return easinessFactor; }
    }

    private void learning() {
        // Default easiness factor
        easinessFactor = 2.5;

        // Define the intervals for each learning tag when not graduated
        long againInterval = 1; // Minutes
        long hardInterval = 3; // Minutes
        long goodInterval = 5; // Minutes
        long easyInterval = 10; // Minutes

        // Calculate the next review time based on the learning tag and previous time

        // Calculate the time in minutes until the next review
        timeMinutes = switch (learningTag) {
            case "Hard" -> hardInterval;
            case "Good" -> goodInterval;
            case "Easy" -> easyInterval;
            default -> againInterval;
        };


    }

    public  ReviewResult reviewCard(boolean graduated, String Tag, String previousLearningTag, long previousMinutes, double previousEasinessfactor) {
        learningTag = Tag;
        // NOT GRADUATED //
        if (!graduated ) {

            // card graduates after 2 consecutive "Easy" user rating
            if(learningTag.equals(EASY) && previousLearningTag.equals(EASY)) {
                graduated = true;
                easinessFactor = 2.5;
                // Update the timeMinutes with 1 day
                timeMinutes = 1440;
            }
            else {
                learning();
            }
        }

        // graduated card becomes false after "Again" user rating

        else if(learningTag.equals(AGAIN)) {


            graduated = false;
            //restart learning
            learning();
        }

        //already graduated

        else {
            // Calculate the value of q based on the user rating
            double q = switch (learningTag) {
                case HARD -> 2.0;
                case GOOD -> 3.0;
                case EASY -> 4.0;
                default -> 0.0;
            };
            // Update the easiness factor based on formula and the calculated q
            easinessFactor = previousEasinessfactor + (0.1 - (5 - q) * (0.08 + (5 - q) * 0.02));

            // Ensure that the easiness factor remains within the specified range
            easinessFactor = Math.max(easinessFactor, 1.3);

            // Update the timeMinutes with the new interval
            timeMinutes = (long) (previousMinutes * easinessFactor);
        }

        // Return the results
        return new ReviewResult(graduated, timeMinutes, easinessFactor);
    }


}

