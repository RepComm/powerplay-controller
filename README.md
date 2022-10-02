# powerplay-controller

Source code of an REV-31-1152 controller

Base project copied from [FTC SDK](https://github.com/FIRST-Tech-Challenge/FtcRobotController)

The [TeamCode](./TeamCode/src/main/java/org/firstinspires/ftc/teamcode) directory contains the custom source code for this repo

### User Documentation and Tutorials
[FtcRobotController Online Documentation](https://github.com/FIRST-Tech-Challenge/FtcRobotController/wiki)
[FTC Javadoc Documentation](https://javadoc.io/doc/org.firstinspires.ftc)
[FTC Technology Forum](https://ftcforum.firstinspires.org/forum/ftc-technology)

### Implemented
Omnidrive - math to calculate driving in a direction given arbitrary wheel count and angles
Debounce - think windows 'sticky keys' feature
Vec2 - 2d vector math library (ported from npm @repcomm/vec2 typescript module)
Main - Main OpMode, left stick = omni-drive, right stick = turn/align turn, A = toggle align/turn mode
