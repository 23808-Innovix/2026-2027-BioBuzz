package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.opencv.ColorBlobProcessor;
import org.firstinspires.ftc.vision.opencv.ColorRange;
import org.firstinspires.ftc.vision.opencv.ImageRegion;
import android.util.Size;

@Autonomous(name = "BioBuzz: C270 Webcam Base", group = "Vision")
public class BioBuzzVisionBase extends LinearOpMode {

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    private ColorBlobProcessor yellowPollenProcessor;

    @Override
    public void runOpMode() {
        // 1. Build the AprilTag Processor for field localization
        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawAxes(true)
                .setDrawCubeProjection(true)
                .build();

        // 2. Build the Color Blob Processor targeting Yellow Pollen elements
        yellowPollenProcessor = new ColorBlobProcessor.Builder()
                .setThresholdBackgroundColor(ColorRange.YELLOW) // Built-in Yellow thresholding
                .setContoursDiscardSizeRange(ColorBlobProcessor.ContourSizeDiscardMethod.MAX_AREA_FRACTION, 0.005, 0.5)
                .setRoi(ImageRegion.entireFrame()) // Define sub-regions here if needed to avoid backdrop noise
                .build();

        // 3. Initialize the VisionPortal with the Logitech C270
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(640, 480)) // The only pre-calibrated resolution for the C270 in the SDK
                .addProcessors(aprilTagProcessor, yellowPollenProcessor)
                .build();

        telemetry.addLine("Camera Initialized. View stream on Driver Station!");
        telemetry.update();

        // Wait for the match to start
        waitForStart();

        while (opModeIsActive()) {
            // --- Process Pollen Detection Data ---
            var blobs = yellowPollenProcessor.getBlobs();
            telemetry.addData("Yellow Blobs Detected", blobs.size());

            if (!blobs.isEmpty()) {
                // Focus on the largest blob found
                var largestBlob = blobs.get(0);
                telemetry.addData("Pollen Center X", largestBlob.getBoxFit().center.x);
                telemetry.addData("Pollen Center Y", largestBlob.getBoxFit().center.y);
                telemetry.addData("Pollen Contour Area", largestBlob.getContourArea());
            }

            // --- Process AprilTag Data ---
            var detections = aprilTagProcessor.getDetections();
            telemetry.addData("AprilTags Detected", detections.size());

            for (var detection : detections) {
                if (detection.metadata != null) {
                    telemetry.addLine(String.format("\nTag ID %d (%s)", detection.id, detection.metadata.name));
                    telemetry.addData("Distance (Range)", "%6.2f inches", detection.ftcPose.range);
                    telemetry.addData("Bearing (Angle)", "%6.2f deg", detection.ftcPose.bearing);
                }
            }

            telemetry.update();
            sleep(50); // Small delay to avoid overloading CPU loops
        }

        // Close the portal when the OpMode ends
        visionPortal.close();
    }
}
