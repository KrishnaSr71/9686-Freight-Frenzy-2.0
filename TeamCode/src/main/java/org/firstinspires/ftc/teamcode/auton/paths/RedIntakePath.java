package org.firstinspires.ftc.teamcode.auton.paths;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.arcrobotics.ftclib.command.InstantCommand;
import com.arcrobotics.ftclib.command.ParallelCommandGroup;
import com.arcrobotics.ftclib.command.SequentialCommandGroup;
import com.arcrobotics.ftclib.command.WaitCommand;
import com.arcrobotics.ftclib.hardware.motors.Motor;

import org.firstinspires.ftc.teamcode.commands.RunCommand;
import org.firstinspires.ftc.teamcode.commands.TrajectoryFollowerCommand;
import org.firstinspires.ftc.teamcode.drive.TurnCommand;
import org.firstinspires.ftc.teamcode.subsystems.DropSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.MecanumDriveSubsystem;

@Config
public class RedIntakePath extends SequentialCommandGroup {

    private Pose2d startPose = new Pose2d(9.0, -62.0, 0.0);

    public RedIntakePath(MecanumDriveSubsystem drive, DropSubsystem drop, Motor intake) {
        drive.setPoseEstimate(startPose);

        Trajectory traj0 = drive.trajectoryBuilder(startPose)
//                .lineToLinearHeading(new Pose2d(-6.0, 34.0, Math.toRadians(60.0))) // Drop init freight
//                .lineToLinearHeading(new Pose2d(0.0, 30.0, Math.toRadians(60.0))) // Drop init freight
//                .lineToLinearHeading(new Pose2d(6.0, 30.0, Math.toRadians(60.0))) // Drop init freight
                .lineToLinearHeading(new Pose2d(6.0, -24.0, Math.toRadians(-60.0))) // Drop init freight
                .build();

        Trajectory traj1 = drive.trajectoryBuilder(traj0.end())
                .lineToLinearHeading(new Pose2d(9.0, -62.0, 0.0)) // Go back to start pos
                .build();

        Trajectory traj2 = drive.trajectoryBuilder(traj1.end()) // Go into warehouse
                .forward(50)
                .build();

        Trajectory traj3 = drive.trajectoryBuilder(traj2.end())
                .strafeRight(30)
                .build();

        Trajectory traj4 = drive.trajectoryBuilder(traj3.end())
//                .lineToLinearHeading(new Pose2d(9.0, 62.0, 0.0)) // Exit warehouse
//                .lineToLinearHeading(new Pose2d(9.0, 80.0, 0.0)) // Exit warehouse
                .back(50)
                .build();

        Trajectory traj0_1 = drive.trajectoryBuilder(traj4.end())
//                .lineToLinearHeading(new Pose2d(9.0, 58.0, Math.toRadians(0.0))) // Drop init freight after exiting warehouse
                .lineToLinearHeading(new Pose2d(9.0, -49.0, Math.toRadians(0.0))) // Drop init freight after exiting warehouse
                .build();

        Trajectory traj0_2 = drive.trajectoryBuilder(traj4.end())
                .lineToLinearHeading(new Pose2d(-6.0, -34.0, Math.toRadians(-60.0))) // Drop init freight after exiting warehouse
                .build();

        addCommands(
                new InstantCommand(() -> intake.set(-0.5)),
                new TrajectoryFollowerCommand(drive, traj0).alongWith(new InstantCommand(drop::dropTwo).andThen(new WaitCommand(1200).andThen(new InstantCommand(drop::dropThree)))),
                new InstantCommand(() -> intake.set(0.5)),
                new TrajectoryFollowerCommand(drive, traj1)
                    .alongWith(new InstantCommand(drop::dropFour)),
                new ParallelCommandGroup(
                    new RunCommand(() -> intake.set(0)).raceWith(new WaitCommand(1000)),
                    new InstantCommand(drop::dropOne)
                ),
                new InstantCommand(() -> intake.set(-0.45)),
                new TrajectoryFollowerCommand(drive, traj2),
                new WaitCommand(1000),
                new TrajectoryFollowerCommand(drive, traj3)
                    .alongWith(new InstantCommand(() -> intake.set(1))),
                new TrajectoryFollowerCommand(drive, traj4)
                        .alongWith(new InstantCommand(() -> intake.set(1))),
                new InstantCommand(() -> intake.set(0)),
                new InstantCommand(() -> intake.set(-0.5)),
                new TrajectoryFollowerCommand(drive, traj0_1),
                new TurnCommand(drive, Math.toRadians(-100.0)).alongWith(new InstantCommand(drop::dropTwo).andThen(new InstantCommand(drop::dropThree))),
                new InstantCommand(() -> intake.set(0.5)),
                new TrajectoryFollowerCommand(drive, traj1)
                        .alongWith(new InstantCommand(drop::dropFour)),
                new ParallelCommandGroup(
                        new RunCommand(() -> intake.set(0)).raceWith(new WaitCommand(1000)),
                        new InstantCommand(drop::dropOne)
                ),
                new TrajectoryFollowerCommand(drive, traj2)
                    .alongWith(new InstantCommand(() -> intake.set(-0.5))),
                new WaitCommand(1000),
                new InstantCommand(() -> intake.set(1)).raceWith(new WaitCommand(1000))
        );

        // addCommands(
        //         new TrajectoryFollowerCommand(drive, traj0),
        //         new RunCommand(drop::initDrop).raceWith(new WaitCommand(1000)),
        //         new TrajectoryFollowerCommand(drive, traj1),
        //         new RunCommand(drop::halfDrop).raceWith(new WaitCommand(1000)),
        //         new RunCommand(drop::drop).raceWith(new WaitCommand(1000)),
        //         // new ParallelRaceGroup(
        //         //         new RunCommand(() -> intake.set(-0.5)).raceWith(new WaitCommand(3000)),
        //         //         new SequentialCommandGroup(
        //         //                 new TrajectoryFollowerCommand(drive, traj2),
        //         //                 new TrajectoryFollowerCommand(drive, traj3)
        //         //         )
        //         // ),
        //         new SequentialCommandGroup(
        //                 new TrajectoryFollowerCommand(drive, traj2),
        //                 new TrajectoryFollowerCommand(drive, traj3)
        //         ),
        //         // Intake
        //         // new ParallelDeadlineGroup(
        //         //         new SequentialCommandGroup(
        //         //                 new TrajectoryFollowerCommand(drive, traj1),
        //         //                 new TrajectoryFollowerCommand(drive, traj2),
        //         //                 new TrajectoryFollowerCommand(drive, traj3)
        //         //         ),
        //         //         new SequentialCommandGroup(
        //         //                 new RunCommand(drop::halfDrop).raceWith(new WaitCommand(1000)),
        //         //                 new RunCommand(drop::drop).raceWith(new WaitCommand(1000)),
        //         //                 new RunCommand(() -> intake.set(-0.5)).raceWith(new WaitCommand(3000))
        //         //         )
        //         // ),
        //         new TrajectoryFollowerCommand(drive, traj0),
        //         new RunCommand(drop::miniDrop).raceWith(new WaitCommand(1000)),
        //         new RunCommand(drop::initDrop).raceWith(new WaitCommand(1000))
        // );
    }
}