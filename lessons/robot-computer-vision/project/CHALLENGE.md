# Advanced challenge

You finished the lesson, so this one's open. It's **optional** and harder: it isn't needed for the next lesson, and Dozer doesn't walk through it. You work it out, the way you would on a real robot. When you think it's right, open **Checkpoints** and **Verify** the challenge.

## Knocked off course

There's a trap in the code you were given. `isSensible()` drops any solution more than 1 meter from the
estimate, which protects you from wildly wrong camera answers. But suppose another robot shoves yours and the
wheels slip, so the estimate is 1.5 meters off. Now **every** correct camera answer looks wrong, and the robot stays lost for the rest of the match, while its cameras tell it the truth.

Fix it in `LocalizationSubsystem`, without giving up the protection:

- **One** far-away answer is probably wrong: ignore it.
- **Several in a row that agree with each other** mean the estimate is what's wrong: start the estimate over from what the cameras see, with `pose_estimator_.resetPosition(...)`.

Deleting the 1 meter check won't pass. Try it and see why.

**How it's checked:** with the simulator running, enable Teleop and tap **I** (the Y button). This starts a
test of about 15 seconds; you don't need to touch anything else. For 5 seconds, false sightings appear, and your
estimate must stay within 0.3 meters of the truth. Then your estimate gets knocked 1.5 meters off (see
`knockEstimate()`), and it has to get back within 0.2 meters in 5 seconds and stay within 0.3 meters for 5 more. The false sightings keep coming the whole time, so "agree with each other" has to mean it. Plot
`Subsystem/Localization/PositionErrorMeters`. `Check/Challenge/KnockedOffPhase` says where the test is: 4 passed, -1 failed. Tap **I** to try again.
